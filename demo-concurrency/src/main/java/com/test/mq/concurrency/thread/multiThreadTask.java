package com.test.mq.concurrency.thread;


import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class multiThreadTask extends Thread {
    private List<String> stringList;

    private CountDownLatch countDownLatch;

    public multiThreadTask(List<String> stringList, CountDownLatch countDownLatch) {
        this.stringList = stringList;
        this.countDownLatch = countDownLatch;
    }
    public multiThreadTask(List<String> stringList) {
        this.stringList = stringList;
    }
    public multiThreadTask() {}

    @Override
    public void run() {
        if(!CollectionUtils.isEmpty(stringList)){
            try {
                countDownLatch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stringList.forEach(s -> {
                System.out.println(s);
            });
            countDownLatch.countDown();
        }
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        List<String> stringList1 = new ArrayList<>();
        stringList1.add("a");
        stringList1.add("b");
        stringList1.add("c");
        CountDownLatch countDownLatch = new CountDownLatch(2);

           // System.out.println("主线程await");

            System.out.println("主线程继续执行");

        multiThreadTask multiThreadTask = new multiThreadTask(stringList1,countDownLatch);
        Thread t1 = new Thread(multiThreadTask);
        System.out.println(t1.getName());
        t1.start();
        // countDownLatch.countDown();
        /*try {
            //t1线程执行完之后再执行t2线程
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        List<String> stringList = new ArrayList<>();
        stringList.add("1");
        stringList.add("2");
        stringList.add("3");
        multiThreadTask t2 = new multiThreadTask(stringList,countDownLatch);
        //Thread t2 = new Thread(multiThreadTask2);
        System.out.println(t2.getName());
        t2.start();
        System.out.println("主线程执行结束");
    }
}
