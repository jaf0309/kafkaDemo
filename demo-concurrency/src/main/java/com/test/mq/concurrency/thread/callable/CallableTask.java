package com.test.mq.concurrency.thread.callable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class CallableTask implements Callable {


    private List<String> stringList;


    public CallableTask(List<String> stringList) {
        this.stringList = stringList;
    }

    public CallableTask() {}


    @Override
    public Object call() throws Exception {
        System.out.println("call start*************");
        stringList.forEach(s -> System.out.println(s));
        return "success";
    }




    /**
          * @param args
          */
            public static void main(String[] args) {

            List<String> stringList1 = new ArrayList<>();
            stringList1.add("a");
            stringList1.add("b");
            stringList1.add("c");

            FutureTask<String> ft = new FutureTask<>(new CallableTask(stringList1));
            new Thread(ft).start();
                try {
                    //接收返回结果
                    System.out.println(ft.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }


}
