package kh.com.kshrd.docengine.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

/*    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("AsyncEmailSender-");
        executor.initialize();
        return executor;
    }*/
  @Bean(name = "requestExecutor")
  public TaskExecutor requestThreadPoolTaskExecutor() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(25);
      executor.setQueueCapacity(100);
      executor.setMaxPoolSize(25);
      executor.setThreadNamePrefix("response_executor_thread");
      executor.initialize();
      return executor;
  }
}

