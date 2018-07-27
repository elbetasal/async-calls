package org.elbetasal.async.asyncdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@SpringBootApplication
@Configuration
@EnableAsync
@Slf4j
public class AsyncDemoApplication {

    @Autowired
    private AsyncClass asyncClass;

    @Autowired
    private ThreadPoolTaskExecutor executor;

    private int maximumPoolSize;


    public static void main(String[] args) {
        SpringApplication.run(AsyncDemoApplication.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner(){
        return strings -> {
            int i = 0;
            while (true){
                int remainingCapacity = executor.getThreadPoolExecutor().getQueue().remainingCapacity();
                for(int j = 0 ; j < remainingCapacity ; j++){
                    asyncClass.asyncCall(i++);
                }
            }
        };
    }

    @Bean
    public ThreadPoolTaskExecutor executor(){
        maximumPoolSize = 2;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(maximumPoolSize);
        executor.setMaxPoolSize(maximumPoolSize);
        executor.setQueueCapacity(20);
        executor.setAllowCoreThreadTimeOut(false);
        return executor;
    }

    @Component
    public static class AsyncClass{

        @Async("executor")
        public void asyncCall(Integer i){
            log.info("Calling async {}" , i);
            try {
                TimeUnit.SECONDS.sleep(12);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("Finishing async {}" , i);
        }
    }
}
