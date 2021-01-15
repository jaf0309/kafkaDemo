package com.test.mq.concurrency.queue;

import java.util.concurrent.DelayQueue;

public class DelayQueueExample {

    public static void main(String[] args) throws InterruptedException {
        DelayQueue<DelayedElement> queue = new DelayQueue<>();

        new Thread(() -> {
            DelayedElement element1 = new DelayedElement("张三",2000);
            DelayedElement element2 = new DelayedElement("李四",0);
            DelayedElement element3 = new DelayedElement("王二麻",3000);
            DelayedElement element4 = new DelayedElement("王小五",3000);
            queue.put(element1);
            queue.put(element2);
            queue.put(element3);
        }).start();


        while(true){
            DelayedElement e1 = queue.take();
            System.out.println("e1:" + e1.getName());
            DelayedElement e2 = queue.take();
            System.out.println("e2:" + e2.getName());
            DelayedElement e3 = queue.take();
            System.out.println("e3:" + e3.getName());
            System.out.println("等待中*********************");
        }


    }

}