package com.itclj.thread.service.impl;

import com.itclj.dto.ItcljDTO;
import com.itclj.thread.service.ThreadService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class ThreadServiceImpl implements ThreadService {

    private Logger logger = LoggerFactory.getLogger(ThreadServiceImpl.class);

    @Resource(name = "itcljTaskExecutor")
    AsyncTaskExecutor itcljTaskExecutor;

    private final Integer maxPoolSize = 6;

    @Override
    public boolean thd4() {
        List<Future> futureList = new ArrayList<>();
        List<ItcljDTO> list1 = new ArrayList<>();
        long batchNum = 0;
        for (long n = 0; n < 1000000000; n++) {
            for (int m = 0; m < 10000; m++) {
                list1.add(new ItcljDTO("=========" + RandomStringUtils.random(50)));
            }

            /**
             * 验证点一
             * 子线程处理完成后，持有的需要处理的数据对象会被jvm自动回收。
             */
            //thd4c1(list1);
            //list1 = new ArrayList<>();

            /**
             * 验证点二
             *
             * 子线程处理慢，处理速度小于主线程产生数据的速度，内存积压，内存持续上涨，最终把内存干满，频繁触发fullgc，子线程处理速度持续降低，持续恶化。
             * 模拟方式，子线程休眠1s后处理。
             */
            //thd4c2(list1);
            //list1 = new ArrayList<>();

            /**
             * 验证点三
             *
             * 子线程处理慢，处理速度小于主线程产生数据的速度，启用反压机制，等待子线程一批次处理完成后，主线程再继续产生数据，进入下一批次处理，
             * 已经处理完成的数据对象jvm会自行回收，新产生的数据对象能够得到处理，不会持续积压。
             */


            Future future = thd4c(list1);
            list1 = new ArrayList<>();
            futureList.add(future);

            if (futureList.size() >= maxPoolSize) {
                for (Future fr : futureList) {
                    try {
                        fr.get();//同步等待子线程处理完成
                    } catch (Exception e) {
                        logger.warn("future get error ,", e);
                    }
                }
                logger.info("batch exec num: {}", ++batchNum);
            }
        }
        return true;
    }

    private Future thd4c(List<ItcljDTO> list) {
        Future future = itcljTaskExecutor.submit(() -> {
            try {
                logger.info("{},thd4c,list size={}", Thread.currentThread().getName(), list.size());
                Thread.sleep(1000);
            } catch (Exception ex) {
                logger.error("thd4c error,", ex);
            }
        });
        return future;
    }


    private void thd4c1(List<ItcljDTO> list) {
        itcljTaskExecutor.execute(() -> {
            try {
                logger.info("{},thd4c1,list size={}", Thread.currentThread().getName(), list.size());
            } catch (Exception ex) {
                logger.error("thd4c error,", ex);
            }
        });
    }

    private void thd4c2(List<ItcljDTO> list) {
        itcljTaskExecutor.execute(() -> {
            try {
                logger.info("{},thd4c2,list size={}", Thread.currentThread().getName(), list.size());
                Thread.sleep(1000);
            } catch (Exception ex) {
                logger.error("thd4c error,", ex);
            }
        });
    }
}
