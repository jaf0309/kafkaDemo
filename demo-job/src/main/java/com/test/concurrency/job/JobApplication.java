package com.test.concurrency.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.TimeZone;

@SpringBootApplication
@EnableDiscoveryClient
public class JobApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(JobApplication.class, args);
        System.out.println("ヾ(◍°∇°◍)ﾉﾞ    【JOB-TEST服务】启动成功      ヾ(◍°∇°◍)ﾉﾞ");

    }
}
