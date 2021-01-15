package com.test.mq.concurrency.queue;

import org.springframework.lang.NonNull;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedElement implements Delayed {
    private String name;
    private long delayTime;
    private long tamp;

    DelayedElement(String name,long delay) {
        this.name = name;
        this.delayTime = delay;
        this.tamp = delay + System.currentTimeMillis();
    }

    @Override
    public long getDelay(@NonNull TimeUnit unit) {
        return tamp - System.currentTimeMillis();
//        return -1;
    }

    @Override
    public int compareTo(@NonNull Delayed o) {
        return tamp - ((DelayedElement) o).tamp > 0 ? 1 : -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public long getTamp() {
        return tamp;
    }

    public void setTamp(long tamp) {
        this.tamp = tamp;
    }
}
