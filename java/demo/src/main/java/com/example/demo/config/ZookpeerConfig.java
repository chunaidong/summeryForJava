package com.example.demo.config;

import com.example.demo.zookeeper.ZookeeperClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookpeerConfig {

    @Bean(initMethod = "init")
    public ZookeeperClient zookeeperClient() {
        return new ZookeeperClient();
    }

}
