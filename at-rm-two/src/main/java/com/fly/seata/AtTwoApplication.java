package com.fly.seata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author: peijiepang
 * @date 2019-11-13
 * @Description:
 */
@EnableEurekaClient
@SpringBootApplication
public class AtTwoApplication {

  public static void main(String[] args) {
    SpringApplication.run(AtTwoApplication.class,args);
  }

}
