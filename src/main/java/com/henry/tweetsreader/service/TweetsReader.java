package com.henry.tweetsreader.service;

import com.henry.tweetsreader.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Service
public class TweetsReader {
  private static final Logger LOG = LoggerFactory.getLogger(TopicReader.class);

  @Autowired
  @Qualifier("tweetsReaderExecutor")
  private ThreadPoolTaskExecutor executor;

  @Autowired
  private TwitterService twitterService;

  public void readTopics(List<String> topics) {
    Assert.notEmpty(topics, "topics must be specified");

    List<TopicReader> topicReaders = new ArrayList<>();
    topics.stream().forEach(
        topic -> topicReaders.add(new TopicReader(topic, twitterService)));
    while (true) {
      try {
        topicReaders.stream().forEach(topicReader -> {
          if (!topicReader.isRunning()) {
            executor.execute(topicReader);
          }
        });
      } catch (Exception ex) {
        LOG.error("execute task failed!", ex);
        AppUtils.sleep(1000, "waiting for the recovering of launching tasks");
      }

      do {
        AppUtils.sleep(1000, "waiting for launching tasks");
      } while (executor.getActiveCount() != 0);
    }
  }

}
