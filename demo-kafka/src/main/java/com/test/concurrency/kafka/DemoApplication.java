package com.test.concurrency.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(DemoApplication.class, args);
        System.out.println("ヾ(◍°∇°◍)ﾉﾞ    【KAFKA-TEST服务】启动成功      ヾ(◍°∇°◍)ﾉﾞ");

    }
}
