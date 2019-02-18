package com.example.demo.controller;

import com.example.demo.zookeeper.ZookeeperClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    private ZookeeperClient client;
    @RequestMapping("/demo")
    public String hello() throws InterruptedException {
        client.getLock("222333");
        Thread.sleep(100000);
        client.releaseLock("222333");
        return "hello Spring";
    }

    @RequestMapping("/demo2")
    public String demo() throws InterruptedException {
        client.getLock("333444");
        //throw new RuntimeException("3333");
        System.out.println("ddddddddd");
        client.releaseLock("333444");
        return "hello Spring2";
    }

    @RequestMapping("/demo3")
    public String demo2() throws InterruptedException {
        client.getLock("222333");
        System.out.println("aaaaa");
        client.releaseLock("222333");
        return "hello Spring2";
    }
}
