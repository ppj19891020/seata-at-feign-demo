package com.fly.seata.config;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: peijiepang
 * @date 2019-12-31
 * @Description:
 */
@Configuration
public class FeignConfigure {
    public static int connectTimeOutMillis = 60000;//超时时间
    public static int readTimeOutMillis = 60000;
    @Bean
    public Request.Options options() {
        return new Request.Options(connectTimeOutMillis, readTimeOutMillis);
    }

}
