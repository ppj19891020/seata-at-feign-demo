## seata AT模式简介
AT模式是 Seata 主推的分布式事务解决方案,它使得应用代码可以像使用本地事物一样使用分布式事物，完全屏蔽了底层细节，主要有以下几点：
- AT模式依赖全局事物注解和代理数据源，其余代码不需要变更，对业务无侵入、接入成本低；
- AT模式的作用范围在于底层数据，通过保存操作行记录的前后快照和生成反向SQL语句进行补偿操作，对上层应用透明；
- AT模式需借助全局锁和GlobalLock注解来解决全局事务间的写冲突问题，如果一阶段分支事物成功则二阶段一开始全局锁即被释放，否则需要等到分支事务二阶段回滚完成才能释放全局锁；

### seata AT工作流程

![seata AT工作流程](https://raw.githubusercontent.com/ppj19891020/pictures/master/seata/1.jpg "seata AT工作流程")

概括来讲，AT 模式的工作流程分为两阶段。一阶段进行业务 SQL 执行，并通过 SQL 拦截、SQL 改写等过程生成修改数据前后的快照（Image），并作为 UndoLog 和业务修改在同一个本地事务中提交。

如果一阶段成功那么二阶段仅仅异步删除刚刚插入的 UndoLog；如果二阶段失败则通过 UndoLog 生成反向 SQL 语句回滚一阶段的数据修改。

## springCloud-Feign seata AT接入指南

### 1. 引入jar包
```xml
<!--seata组件包-->
<dependency>
  <groupId>io.seata</groupId>
  <artifactId>seata-all</artifactId>
  <version>${seata.version}</version>
</dependency>

<!--spring cloud 相关定制-->
<dependency>
  <groupId>com.alibaba.cloud</groupId>
  <artifactId>spring-cloud-alibaba-seata</artifactId>
  <version>x.y.z</version>
</dependency>
```
注意：[seata兼容版本说明](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)

### 2. seata 注册中心配置
registry.conf配置文件，euraka中的application是指seata的服务端的服务器，这边要注意seata server有事物分组的概念，用于不同业务方的集群分区。
```
registry {
  # 注册中心支持file 、nacos 、eureka、redis、zk,推荐eureka做负载均衡
  type = "eureka"

  eureka {
    serviceUrl = "http://192.168.202.137:8761/eureka"
    # seata server注册中心的服务名
    application = "seata-server-default-group"
    weight = "1"
  }
}

config {
  # 配置中心支持file、nacos 、apollo、zk,推荐apollo
  type = "file"

  file {
    name = "file.conf"
  }
}
```

### 3. seata 配置中心配置
file.conf配置文件，这里需要注意service中的vgroup_mapping配置，其中vgroup_mapping.my_test_tx_group的my_test_tx_group是表示逻辑服务分组，值表示seata server的实际服务分组，一定要存在seata serve的分组名
```
transport {
  # tcp udt unix-domain-socket
  type = "TCP"
  #NIO NATIVE
  server = "NIO"
  #enable heartbeat
  heartbeat = true
  #thread factory for netty
  thread-factory {
    boss-thread-prefix = "NettyBoss"
    worker-thread-prefix = "NettyServerNIOWorker"
    server-executor-thread-prefix = "NettyServerBizHandler"
    share-boss-worker = false
    client-selector-thread-prefix = "NettyClientSelector"
    client-selector-thread-size = 1
    client-worker-thread-prefix = "NettyClientWorkerThread"
    # netty boss thread size,will not be used for UDT
    boss-thread-size = 1
    #auto default pin or 8
    worker-thread-size = 8
  }
  shutdown {
    # when destroy server, wait seconds
    wait = 3
  }
  serialization = "seata"
  compressor = "none"
}

service {
  #vgroup->rgroup
  vgroup_mapping.my_test_tx_group = "seata-server-default-group"
  #only support single node
  default.grouplist = "127.0.0.1:8091"
  #degrade current not support
  enableDegrade = false
  #disable
  disable = false
  #unit ms,s,m,h,d represents milliseconds, seconds, minutes, hours, days, default permanent
  max.commit.retry.timeout = "-1"
  max.rollback.retry.timeout = "-1"
  disableGlobalTransaction = false
}

client {
  async.commit.buffer.limit = 10000
  lock {
    retry.internal = 10
    retry.times = 30
  }
  report.retry.count = 5
  tm.commit.retry.count = 1
  tm.rollback.retry.count = 1
}

transaction {
  undo.data.validation = true
  undo.log.serialization = "jackson"
  undo.log.save.days = 7
  #schedule delete expired undo_log in milliseconds
  undo.log.delete.period = 86400000
  undo.log.table = "undo_log"
}

support {
  ## spring
  spring {
    # auto proxy the DataSource bean
    datasource.autoproxy = false
  }
}
```

### 4. RM 配置服务分组名
application.yml配置文件
```yaml
spring:
  cloud:
    alibaba:
      seata:
        ## 该服务分组名一定要和file.conf配置文件中的service.vgroup_mapping一致，不然找不到对应的seata server集群名
        tx-service-group: my_test_tx_group
```

### 5. RM 配置 AT 模式
配置代理数据源(DataSourceProxy)即可.
```java
@Configuration
@MapperScan("com.fly.seata.dao")
public class DatasourceAutoConfig {

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource druidDataSource(){
    DruidDataSource druidDataSource = new DruidDataSource();
    return druidDataSource;
  }

  @Bean("dataSourceProxy")
  public DataSourceProxy dataSourceProxy(DataSource druidDataSource){
    return new DataSourceProxy(druidDataSource);
  }

  @Bean
  public SqlSessionFactory sqlSessionFactory(DataSourceProxy dataSourceProxy)throws Exception{
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dataSourceProxy);
    sqlSessionFactoryBean.setTransactionFactory(new SpringManagedTransactionFactory());
    return sqlSessionFactoryBean.getObject();
  }
}
```

### 6. TM 全局事物配置
```java
@RestController
public class TmController {

  @Autowired
  private OrderApi orderApi;

  @Autowired
  private StorageApi storageApi;

  @GlobalLock
  @GlobalTransactional
  @GetMapping("/tm/purchase")
  public String purchase(){
    OrderDTO order = new OrderDTO();
    order.setProductId(1l);
    order.setCount(1);
    order.setMoney(new BigDecimal(1));
    order.setUserId(1l);
    orderApi.createOrder(order);
    storageApi.reduce(order.getProductId(),order.getCount());
    return "ok";
  }

}
```

### 7. 观察日志

TM端启动日志发现注册成功的日志，即可说明配置成功！
```
2019-11-14 16:55:33.302  INFO 5420 --- [imeoutChecker_1] i.s.c.r.netty.NettyClientChannelManager  : will connect to 192.168.202.149:8091
2019-11-14 16:55:33.304  INFO 5420 --- [imeoutChecker_1] i.s.core.rpc.netty.NettyPoolableFactory  : NettyPool create channel to transactionRole:TMROLE,address:192.168.202.149:8091,msg:< RegisterTMRequest{applicationId='seata-at-rm-one', transactionServiceGroup='my_test_tx_group'} >
2019-11-14 16:55:33.320  INFO 5420 --- [imeoutChecker_1] i.s.core.rpc.netty.NettyPoolableFactory  : register success, cost 11 ms, version:0.9.0,role:TMROLE,channel:[id: 0xd6e6cdde, L:/192.168.202.149:53471 - R:/192.168.202.149:8091]
```

测试Demo成功日志如下

TM端日志：
```
2019-11-14 17:18:34.841  INFO 5478 --- [nio-8080-exec-7] i.seata.tm.api.DefaultGlobalTransaction  : Begin new global transaction [192.168.202.149:8091:2027442865]
2019-11-14 17:18:35.234  INFO 5478 --- [nio-8080-exec-7] i.seata.tm.api.DefaultGlobalTransaction  : [192.168.202.149:8091:2027442865] commit status:Committed
```

RM端日志：
```
2019-11-14 17:18:35.642  INFO 5472 --- [atch_RMROLE_3_8] i.s.core.rpc.netty.RmMessageListener     : onMessage:xid=192.168.202.149:8091:2027442865,branchId=2027442870,branchType=AT,resourceId=jdbc:mysql://127.0.0.1:3306/seata-storage,applicationData=null
2019-11-14 17:18:35.642  INFO 5472 --- [atch_RMROLE_3_8] io.seata.rm.AbstractRMHandler            : Branch committing: 192.168.202.149:8091:2027442865 2027442870 jdbc:mysql://127.0.0.1:3306/seata-storage null
2019-11-14 17:18:35.642  INFO 5472 --- [atch_RMROLE_3_8] io.seata.rm.AbstractRMHandler            : Branch commit result: PhaseTwo_Committed
2019-11-14 17:18:42.510  INFO 5472 --- [trap-executor-0] c.n.d.s.r.aws.ConfigClusterResolver      : Resolving eureka endpoints via configuration
2019-11-14 17:18:44.733  INFO 5472 --- [trap-executor-0] c.n.d.s.r.aws.ConfigClusterResolver      : Resolving eureka endpoints via configuration
```

TC端日志：
```
2019-11-14 17:18:34.828 INFO [batchLoggerPrint_1]io.seata.core.rpc.DefaultServerMessageListenerImpl.run:198 -SeataMergeMessage timeout=60000,transactionName=purchase()
,clientIp:192.168.202.149,vgroup:my_test_tx_group
2019-11-14 17:18:34.838 INFO [ServerHandlerThread_66_500]io.seata.server.coordinator.DefaultCore.begin:145 -Successfully begin global transaction xid = 192.168.202.149:8091:2027442865
2019-11-14 17:18:34.868 INFO [batchLoggerPrint_1]io.seata.core.rpc.DefaultServerMessageListenerImpl.run:198 -SeataMergeMessage xid=192.168.202.149:8091:2027442865,branchType=AT,resourceId=jdbc:mysql://127.0.0.1:3306/seata-order,lockKey=`order`:59
,clientIp:192.168.202.149,vgroup:my_test_tx_group
2019-11-14 17:18:34.912 INFO [ServerHandlerThread_67_500]io.seata.server.coordinator.DefaultCore.lambda$branchRegister$0:94 -Successfully register branch xid = 192.168.202.149:8091:2027442865, branchId = 2027442867
2019-11-14 17:18:34.951 INFO [batchLoggerPrint_1]io.seata.core.rpc.DefaultServerMessageListenerImpl.run:198 -SeataMergeMessage xid=192.168.202.149:8091:2027442865,branchId=2027442867,resourceId=null,status=PhaseOne_Done,applicationData=null
,clientIp:192.168.202.149,vgroup:my_test_tx_group
2019-11-14 17:18:34.970 INFO [ServerHandlerThread_68_500]io.seata.server.coordinator.DefaultCore.branchReport:118 -Successfully branch report xid = 192.168.202.149:8091:2027442865, branchId = 2027442867
2019-11-14 17:18:35.011 INFO [batchLoggerPrint_1]io.seata.core.rpc.DefaultServerMessageListenerImpl.run:198 -SeataMergeMessage xid=192.168.202.149:8091:2027442865,branchType=AT,resourceId=jdbc:mysql://127.0.0.1:3306/seata-storage,lockKey=storage:1
,clientIp:192.168.202.149,vgroup:my_test_tx_group
2019-11-14 17:18:35.063 INFO [ServerHandlerThread_69_500]io.seata.server.coordinator.DefaultCore.lambda$branchRegister$0:94 -Successfully register branch xid = 192.168.202.149:8091:2027442865, branchId = 2027442870
2019-11-14 17:18:35.112 INFO [batchLoggerPrint_1]io.seata.core.rpc.DefaultServerMessageListenerImpl.run:198 -SeataMergeMessage xid=192.168.202.149:8091:2027442865,branchId=2027442870,resourceId=null,status=PhaseOne_Done,applicationData=null
,clientIp:192.168.202.149,vgroup:my_test_tx_group
2019-11-14 17:18:35.158 INFO [ServerHandlerThread_70_500]io.seata.server.coordinator.DefaultCore.branchReport:118 -Successfully branch report xid = 192.168.202.149:8091:2027442865, branchId = 2027442870
2019-11-14 17:18:35.170 INFO [batchLoggerPrint_1]io.seata.core.rpc.DefaultServerMessageListenerImpl.run:198 -SeataMergeMessage xid=192.168.202.149:8091:2027442865,extraData=null
,clientIp:192.168.202.149,vgroup:my_test_tx_group
2019-11-14 17:18:35.660 INFO [AsyncCommitting_1]io.seata.server.coordinator.DefaultCore.doGlobalCommit:303 -Global[192.168.202.149:8091:2027442865] committing is successfully done.
```
