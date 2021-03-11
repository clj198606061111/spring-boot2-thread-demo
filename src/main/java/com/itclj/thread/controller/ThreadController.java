package com.itclj.thread.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping("/itclj")
public class ThreadController {

    private Logger logger = LoggerFactory.getLogger(ThreadController.class);

    private Map<Integer, Future> threadMap = new HashMap<>();

    @RequestMapping("/thd")
    public boolean thd() {
        for (int n = 0; n < 10; n++) {
            ExecutorService threadPool = Executors.newSingleThreadExecutor();

            Future<String> future = threadPool.submit(new MyThread());
            threadMap.put(n, future);
//            try {
//                logger.info(future.get());
//            } catch (Exception e) {
//                logger.error("Exception ,", e);
//            } finally {
//                threadPool.shutdown();
//            }
        }
        threadMap.get(5).cancel(true);

        logger.info("======================================");
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        Future<String> future = threadPool.submit(new MyThread());
        threadMap.put(5, future);

        logger.info("main thread finish");
        return true;
    }

    @RequestMapping("/thd2")
    public boolean thd2() {
        CompletableFuture<String> ref1 = CompletableFuture.supplyAsync(() -> {
            try {
                logger.info(Thread.currentThread().getName() + " supplyAsync开始执行任务1.... ");
                TimeUnit.SECONDS.sleep(10);
            } catch (Exception e) {
                logger.error("CompletableFuture exception,", e);
            }
            logger.info(Thread.currentThread().getName() + " supplyAsync: 任务1");
            return null;
        });

        //结束子线程
        //ref1.cancel(true);
        return true;
    }

    @RequestMapping("/thd3")
    public boolean thd3() throws InterruptedException {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        CompletableFuture<String> ref1 = CompletableFuture.supplyAsync(() -> {
            try {
                logger.info(Thread.currentThread().getName() + " supplyAsync开始执行任务3.... ");
                TimeUnit.SECONDS.sleep(2);
            } catch (Exception e) {
                logger.error("CompletableFuture exception,", e);
            }
            logger.info(Thread.currentThread().getName() + " supplyAsync: 任务3");
            return null;
        }, pool);

        ref1.thenRunAsync(new MyRunnable(12, ref1), pool);
        //结束子线程,如果子线程已经启动，则不能结束
        Thread.sleep(2000);
        ref1.cancel(true);
        return true;
    }
}
