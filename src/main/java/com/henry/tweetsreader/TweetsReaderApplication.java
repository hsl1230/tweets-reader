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

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class TweetsReaderApplication {

  public static void main(String[] args) {
    SpringApplication.run(TweetsReaderApplication.class, args);
  }

  @Component
  public class MyRunner implements ApplicationRunner {
    private ConsoleOperator consoleOperator;
    private TweetsReader tweetsReader;
    private ContextService contextService;

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
      List<String> topics = null;
      if (args.containsOption("topic")) {
        topics = args.getOptionValues("topic");
      }

      if (topics == null) {
        topics = new ArrayList<>();
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
        for (int i=topics.size() + 1; i < 6;) {
          consoleOperator.print("Please input topic " + i + ": ");
          String topic = consoleOperator.readLine().trim();
          if (topic != null && !topic.isEmpty()) {
            topics.add(topic);
            i++;
          }
        }
      }

      tweetsReader.readTopics(topics);
    }
  }
}
