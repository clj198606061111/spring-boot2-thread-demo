package com.itclj.thread.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class MyThread implements Callable<String> {

    private Logger logger = LoggerFactory.getLogger(MyThread.class);

    @Override
    public String call() throws Exception {
        long threadId = Thread.currentThread().getId();
        logger.info("inner thread id={}", threadId);
        Thread.sleep(10000);
        logger.info("inner thread id={} finished.", threadId);
        return "Itclj thread. " + threadId;
    }
}
