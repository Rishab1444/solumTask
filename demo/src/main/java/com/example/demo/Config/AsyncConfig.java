package com.example.demo.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@EnableTransactionManagement
public class AsyncConfig {

    @Bean(name = "csvExecutor")
    public Executor csvExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3); // Adjust based on your requirements
        executor.setMaxPoolSize(10); // Adjust based on your requirements
        executor.setQueueCapacity(1000000);
        executor.setThreadNamePrefix("CsvExecutor-");
        executor.initialize();
        return executor.getThreadPoolExecutor();



}
}
