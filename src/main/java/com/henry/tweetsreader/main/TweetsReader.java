package com.henry.tweetsreader.main;

import java.util.ArrayList;
import java.util.List;

import com.henry.tweetsreader.AppUtils;

import com.henry.tweetsreader.service.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class TweetsReader {
  private static final Logger LOG = LoggerFactory.getLogger(TopicReader.class);

  @Value("${app.tweetsReader.millisecondsForPoolRecovering:5000}")
  private long millisecondsForPoolRecovering;

  @Value("${app.tweetsReader.millisecondsWaitingForNextRequest:3000}")
  private long millisecondsWaitingForNextRequest;

  @Value("${app.tweetsReader.millisecondsForHttpRecovering:10000}")
  private long millisecondsForHttpRecovering;

  @Autowired
  @Qualifier("tweetsReaderExecutor")
  private ThreadPoolTaskExecutor executor;

  @Autowired
  private TwitterService twitterService;

  /**
   * periodly read and save tweets of specified topics.
   * @param topics topics to be processed.
   */
  public void readTopics(List<String> topics) {
    Assert.notEmpty(topics, "topics must be specified");

    List<TopicReader> topicReaders = new ArrayList<>();
    topics.forEach(
        topic -> topicReaders.add(
            new TopicReader(topic, twitterService, millisecondsForHttpRecovering)));

    while (true) {
      try {
        topicReaders.forEach(topicReader -> {
          if (!topicReader.isRunning()) {
            executor.execute(topicReader);
          }
        });
      } catch (Exception ex) {
        LOG.warn("execute task failed!", ex);
        AppUtils.sleep(millisecondsForPoolRecovering, "waiting for the recovering of launching tasks");
      }

      do {
        AppUtils.sleep(millisecondsWaitingForNextRequest, "waiting for launching tasks");
      } while (executor.getActiveCount() != 0);
    }
  }

}
