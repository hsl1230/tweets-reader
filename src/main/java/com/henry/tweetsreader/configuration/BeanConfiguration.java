package com.henry.tweetsreader.configuration;

import com.henry.tweetsreader.main.ConsoleOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

@Component
public class BeanConfiguration {

  /**
   * Create an instance of ConsoleOperator.
   * @return An Object of ConsoleOperator
   */
  @Bean
  public ConsoleOperator consoleOperator() {
    Reader reader;
    PrintWriter writer;
    if (System.console() == null) {
      reader = new InputStreamReader(System.in);
      writer = new PrintWriter(System.out);
    } else {
      reader = System.console().reader();
      writer = System.console().writer();
    }

    return new ConsoleOperator(reader, writer);
  }

  /**
   * Tweets Reader thread pool.
   * @return Executor
   */
  @Bean
  public ThreadPoolTaskExecutor tweetsReaderExecutor(
      @Value("${app.tweetsReader.pool.coreSize:5}") int coreSize,
      @Value("${app.tweetsReader.pool.queueSize:10}") int queueSize
  ) {
    ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
    System.out.println("pool size : " + coreSize);
    poolTaskExecutor.setDaemon(true);
    poolTaskExecutor.setMaxPoolSize(coreSize);
    poolTaskExecutor.setCorePoolSize(coreSize);
    poolTaskExecutor.setQueueCapacity(queueSize);
    poolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    poolTaskExecutor.initialize();

    return poolTaskExecutor;
  }
}
