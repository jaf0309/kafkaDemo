package com.demo.serviceone;

import org.springframework.boot.SpringApplication;

import java.util.TimeZone;

public class ServiceOneApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(ServiceOneApplication.class, args);
        System.out.println("ヾ(◍°∇°◍)ﾉﾞ    【应用】启动成功      ヾ(◍°∇°◍)ﾉﾞ");
    }
}
