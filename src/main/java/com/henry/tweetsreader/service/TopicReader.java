package com.henry.tweetsreader.service;

import com.henry.tweetsreader.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;

public class TopicReader implements Runnable {
  private static final Logger LOG = LoggerFactory.getLogger(TopicReader.class);

  private String topic;
  private TwitterService twitterService;
  private static final Hashtable<String, AtomicBoolean> workingStatus = new Hashtable<>();

  /**
   * When an object implementing interface <code>Runnable</code> is used
   * to create a thread, starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method <code>run</code> is that it may
   * take any action whatsoever.
   *
   * @see Thread#run()
   */

  public TopicReader(String topic, TwitterService twitterService) {
    Assert.hasText(topic, "topic must to be specified");
    Assert.notNull(twitterService, "twitterService must be provided!");
    this.topic = topic;
    this.twitterService = twitterService;
  }

  public boolean isRunning() {
    AtomicBoolean topicRunning = workingStatus.get(topic);
    if (topicRunning == null) {
      topicRunning = new AtomicBoolean(false);
      workingStatus.put(topic, topicRunning);
    }
    return topicRunning.get();
  }

  @Override
  public void run() {
    AtomicBoolean topicRunning = workingStatus.get(topic);
    if (topicRunning == null) {
      topicRunning = new AtomicBoolean(false);
      workingStatus.put(topic, topicRunning);
    }

    while (!topicRunning.compareAndSet(false, true)) {
      AppUtils.sleep(1000, "waiting for end of the task of topic " + topic);
    }

    try {
      twitterService.readTweetsOf(topic);
    } catch (Exception ex) {
      LOG.error("read tweets failed, topic: " + topic, ex);
      AppUtils.sleep(1000, "waiting for the recovering of reading tweets on topic " + topic);
    }

    topicRunning.set(false);
  }
}
