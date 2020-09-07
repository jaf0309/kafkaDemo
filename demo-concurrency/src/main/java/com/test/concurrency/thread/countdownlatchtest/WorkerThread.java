package com.test.concurrency.thread.countdownlatchtest;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WorkerThread implements Runnable{

    private String name;
    private CountDownLatch countDownLatch;

    public WorkerThread( String name,CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        this.name = name;
    }
    @Override
    public void run() {
        System.out.println(this.name + "正在干活!");
        try{
            TimeUnit.SECONDS.sleep(new Random().nextInt(10));
        }catch(InterruptedException ie){
        }
        System.out.println(this.name + "活干完了！");
        this.countDownLatch.countDown();
    }
}
