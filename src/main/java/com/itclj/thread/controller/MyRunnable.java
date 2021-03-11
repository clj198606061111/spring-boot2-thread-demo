package com.itclj.thread.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MyRunnable implements Runnable {

    private Logger logger = LoggerFactory.getLogger(MyRunnable.class);

    private Integer id;

    private CompletableFuture future;

    public MyRunnable(Integer id, CompletableFuture future) {
        this.id = id;
        this.future = future;
    }

    @Override
    public void run() {
        logger.info("Inner thread id={},thread name={}", id, Thread.currentThread().getName());
        if (id < 30) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception ex) {
                logger.error("error,", ex);
            }
            this.future.thenRun(new MyRunnable(++id, future));
        }
    }
}
