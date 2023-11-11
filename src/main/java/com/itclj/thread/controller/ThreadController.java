package com.itclj.thread.controller;

import com.itclj.thread.service.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping("/itclj")
public class ThreadController {

    private Logger logger = LoggerFactory.getLogger(ThreadController.class);

    private Map<Integer, Future> threadMap = new HashMap<>();

    @Resource
    ThreadService threadService;

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


    /**
     * 最近项目上遇到个问题，采用多线程处理的时候发现子线程内部处理不够快，主线程生产子线程需要处理的批次数据，
     * 主线程生产数据快，导致主线程产生的数据得不到子线程及时处理，内存快速被撑爆，触发fullgc，又导致大量cpu被用于处理gc，
     * 子线程处理起来更慢了，原来1秒处理一批次，到后来10分钟处理一批次。
     * <p>
     * 写个测试代码复现一下，顺便也验证一下子线程处理完成后，被子线程占有的需要处理的数据对象会自动被jvm垃圾收集器 自动回收，
     * 内存不会持续暴涨。
     * <p>
     * http://127.0.0.1:8080/itclj/thd4
     *
     * @return
     */
    @GetMapping("/thd4")
    public boolean thd4() {
        logger.info("thd4");
        return threadService.thd4();
    }
}
