package com.itclj.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    private final Integer maxPoolSize = 6;

    @Bean(name = "itcljTaskExecutor")
    public AsyncTaskExecutor itcljTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("itclj-thread-");
        executor.setMaxPoolSize(maxPoolSize);
        executor.setCorePoolSize(maxPoolSize);
        return executor;
    }
}
