package com.fly.seata.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据源配置类
 * @author: peijiepang
 * @date 2019-11-13
 * @Description:
 */
@Configuration
@MapperScan("com.fly.seata.dao")
public class DatasourceAutoConfig {

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource druidDataSource(){
    DruidDataSource druidDataSource = new DruidDataSource();
    return commonConfigurete(druidDataSource);
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

  /**
   * 通用数据源配置
   * @param dataSource
   * @return
   */
  public DruidDataSource commonConfigurete(DruidDataSource dataSource){
    dataSource.setInitialSize(30);
    dataSource.setMinIdle(30);
    dataSource.setMaxActive(30);
    //配置间隔多久启动一次DestroyThread，对连接池内的连接才进行一次检测，
    // 单位是毫秒。检测时:1.如果连接空闲并且超过minIdle以外的连接，如果空闲时间超过minEvictableIdleTimeMillis设置的值则直接物理关闭。2.在minIdle以内的不处理。
    dataSource.setTimeBetweenEvictionRunsMillis(60000);
    //配置一个连接在池中最大空闲时间，单位是毫秒
    dataSource.setMinEvictableIdleTimeMillis(30000);
    //设置从连接池获取连接时是否检查连接有效性，true时，如果连接空闲时间超过minEvictableIdleTimeMillis进行检查，否则不检查;false时，不检查
    dataSource.setTestWhileIdle(true);
    //检验连接是否有效的查询语句。如果数据库Driver支持ping()方法，则优先使用ping()方法进行检查，否则使用validationQuery查询进行检查。(Oracle jdbc Driver目前不支持ping方法)
    dataSource.setValidationQuery("select 1 from dual");
    //打开后，增强timeBetweenEvictionRunsMillis的周期性连接检查，minIdle内的空闲连接，每次检查强制验证连接有效性. 参考：https://github.com/alibaba/druid/wiki/KeepAlive_cn
    dataSource.setKeepAlive(true);

    //是否缓存preparedStatement，也就是PSCachef。使用sharding-jdbc的时候，一定要打开该操作，用于缓存sql解析器的缓存
    dataSource.setPoolPreparedStatements(true);

    //连接泄露检查，打开removeAbandoned功能 , 连接从连接池借出后，长时间不归还，将触发强制回连接。回收周期随timeBetweenEvictionRunsMillis进行，如果连接为从连接池借出状态，并且未执行任何sql，并且从借出时间起已超过removeAbandonedTimeout时间，则强制归还连接到连接池中。
    dataSource.setRemoveAbandoned(true);
    //超时时间，秒
    dataSource.setRemoveAbandonedTimeout(80);
    //关闭abanded连接时输出错误日志，这样出现连接泄露时可以通过错误日志定位忘记关闭连接的位置
    dataSource.setLogAbandoned(true);

//        //通过connectProperties属性来打开mergeSql功能；慢SQL记录
//        Properties properties = new Properties();
//        properties.setProperty("rsa.stat.mergeSql","true");
//        properties.setProperty("rsa.stat.slowSqlMillis","5000");
//        dataSource.setConnectProperties(properties);
    return dataSource;
  }

}
