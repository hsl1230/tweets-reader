package com.henry.tweetsreader;

import com.henry.tweetsreader.main.ConsoleOperator;
import com.henry.tweetsreader.service.ContextService;
import com.henry.tweetsreader.main.TweetsReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class TweetsReaderApplication {

  public static void main(String[] args) {
    SpringApplication.run(TweetsReaderApplication.class, args);
  }

  @Component
  public static class MyRunner implements ApplicationRunner {
    private ConsoleOperator consoleOperator;
    private TweetsReader tweetsReader;
    private ContextService contextService;

    /**
     * constructor of runner.
     * @param consoleOperator console operator
     * @param tweetsReader tweets reader
     * @param contextService context service
     */
    @Autowired
    public MyRunner(
        ConsoleOperator consoleOperator,
        TweetsReader tweetsReader,
        ContextService contextService
    ) {
      this.consoleOperator = consoleOperator;
      this.tweetsReader = tweetsReader;
      this.contextService = contextService;
    }
    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
      Set<String> topics = new HashSet<>();
      if (args.containsOption("topic")) {
        List<String> topicsFromParams = args.getOptionValues("topic");
        if (topicsFromParams != null) {
          topics.addAll(topicsFromParams);
        }
      }

      String outputPath = null;
      if (args.containsOption("outputPath")) {
        outputPath = args.getOptionValues("outputPath").stream().findFirst().orElse(null);
      }

      if (outputPath == null) {
        outputPath = ".";
      }

      contextService.setTweetsFilePath(outputPath);

      if (topics.size() < 5) {
        for (int i = topics.size() + 1; i < 6;) {
          consoleOperator.printf("Please input more topics (%d-5): ", i);
          String inputLine = consoleOperator.readLine();
          for (String topic : inputLine.split("[^\\w]")) {
            if (topic.isEmpty()) {
              continue;
            } else if (topics.contains(topic)) {
              consoleOperator.printf("topic [%s] is already in the list!\n", topic);
            } else {
              topics.add(topic);
              i = topics.size() + 1;
            }
          }
        }
      }

      tweetsReader.readTopics(topics);
    }
  }
}
