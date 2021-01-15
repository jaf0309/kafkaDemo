package com.test.mq.concurrency.controller;

import com.test.mq.concurrency.base.spring.life.BaseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerController {

    @Autowired
    private BaseBean baseBean;
    @RequestMapping(value = "/test/life")
     public void testLife(){
        baseBean.setName("张三");
        System.out.println(baseBean);
     }
}
