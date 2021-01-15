package com.test.mq.concurrency.thread;


import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MultiRunableThreadTask implements Runnable{

    private List<String> stringList;

    private CountDownLatch countDownLatch;

    public MultiRunableThreadTask(List<String> stringList, CountDownLatch countDownLatch) {
        this.stringList = stringList;
        this.countDownLatch = countDownLatch;
    }
    public MultiRunableThreadTask(List<String> stringList) {
        this.stringList = stringList;
    }
    public MultiRunableThreadTask() {}

    /**
     * 任务
     */
    @Override
    public void run() {
        if(!CollectionUtils.isEmpty(stringList)){

            System.out.println(Thread.currentThread().getName()+"await");
            /*try {
                //countDownLatch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            System.out.println(Thread.currentThread().getName()+"继续执行");
            stringList.forEach(s -> {
                System.out.println(s);
            });
            if(countDownLatch != null ){
                countDownLatch.countDown();
            }
        }
    }





    /**
     * @param args
     */
    public static void main(String[] args) {
        test1();
        //test2();
    }

    private static void test1(){
        // TODO Auto-generated method stub
        List<String> stringList1 = new ArrayList<>();
        stringList1.add("a");
        stringList1.add("b");
        stringList1.add("c");
        CountDownLatch countDownLatch = new CountDownLatch(3);
        System.out.println("主线程继续执行");
        try {
            System.out.println("主线程await");
            countDownLatch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MultiRunableThreadTask multiThreadTask = new MultiRunableThreadTask(stringList1,countDownLatch);
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
        MultiRunableThreadTask multiThreadTask2 = new MultiRunableThreadTask(stringList,countDownLatch);
        Thread t2 = new Thread(multiThreadTask2);
        System.out.println(t2.getName());
        t2.start();
        // countDownLatch.countDown();
      /*  try {
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        List<String> stringList2 = new ArrayList<>();
        stringList2.add("7");
        stringList2.add("8");
        stringList2.add("9");
        MultiRunableThreadTask multiThreadTask3 = new MultiRunableThreadTask(stringList2,countDownLatch);
        Thread t3 = new Thread(multiThreadTask3);
        System.out.println(t3.getName());
        t3.start();
        System.out.println("主线程执行结束");

    }

    private static void test2(){
        // TODO Auto-generated method stub
        List<String> stringList1 = new ArrayList<>();
        stringList1.add("a");
        stringList1.add("b");
        stringList1.add("c");
        CountDownLatch countDownLatch = new CountDownLatch(3);
        System.out.println("主线程继续执行");
        try {
            System.out.println("主线程await");
            countDownLatch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MultiRunableThreadTask multiThreadTask = new MultiRunableThreadTask(stringList1,countDownLatch);
        Thread t1 = new Thread(multiThreadTask);
        System.out.println(t1.getName());
        t1.start();
         countDownLatch.countDown();
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
        MultiRunableThreadTask multiThreadTask2 = new MultiRunableThreadTask(stringList,countDownLatch);
        Thread t2 = new Thread(multiThreadTask2);
        System.out.println(t2.getName());
        t2.start();
         countDownLatch.countDown();
      /*  try {
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        List<String> stringList2 = new ArrayList<>();
        stringList2.add("7");
        stringList2.add("8");
        stringList2.add("9");
        MultiRunableThreadTask multiThreadTask3 = new MultiRunableThreadTask(stringList2,countDownLatch);
        Thread t3 = new Thread(multiThreadTask3);
        System.out.println(t3.getName());
        t3.start();
        System.out.println("主线程执行结束");

    }

}
