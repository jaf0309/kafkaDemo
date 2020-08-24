package com.test.concurrency.thread.countdownlatchtest;

import java.util.concurrent.*;

public class CountDownLatchDemo {

    public static void main(String[] args) {
        //ExecutorService executor = Executors.newCachedThreadPool();
        ExecutorService executor = new ThreadPoolExecutor(10,20,60, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());
        CountDownLatch latch = new CountDownLatch(3);
        WorkerThread w1 = new WorkerThread("张三",latch);
        WorkerThread w2 = new WorkerThread("李四",latch);
        WorkerThread w3 = new WorkerThread("王二嘛",latch);

        BossThread boss = new BossThread(latch);

        executor.execute(w3);
        executor.execute(w2);
        executor.execute(w1);

        executor.execute(boss);
        executor.shutdown();
    }
}
