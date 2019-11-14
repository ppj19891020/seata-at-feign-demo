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
import org.springframework.context.annotation.Primary;

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
