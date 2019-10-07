package com.henry.tweetsreader.main;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;

import com.henry.tweetsreader.AppUtils;

import com.henry.tweetsreader.service.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class TopicReader implements Runnable {
  private static final Logger LOG = LoggerFactory.getLogger(TopicReader.class);

  private String topic;
  private TwitterService twitterService;
  private long millisecondsForHttpRecovering;

  // * AtomicBoolean is used here as an replacement of lock to minimize contention. 
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
  public TopicReader(
      String topic, TwitterService twitterService, long millisecondsForHttpRecovering) {
    Assert.hasText(topic, "topic must to be specified");
    Assert.notNull(twitterService, "twitterService must be provided!");
    this.topic = topic;
    this.twitterService = twitterService;
    this.millisecondsForHttpRecovering = millisecondsForHttpRecovering;
  }


  /**
   * return the status of the task.
   * true: is running
   * false: not started or finished.
   * multi task on the same topic should be prevented.
   */
  public boolean isRunning() {
    AtomicBoolean topicRunning = workingStatus.get(topic);
    if (topicRunning == null) {
      topicRunning = new AtomicBoolean(false);
      workingStatus.put(topic, topicRunning);
    }
    return topicRunning.get();
  }

  /**
   * read tweets by sending one rest request.
   */
  @Override
  public void run() {
    AtomicBoolean topicRunning = workingStatus.get(topic);
    if (topicRunning == null) {
      topicRunning = new AtomicBoolean(false);
      workingStatus.put(topic, topicRunning);
    }

    // check if any other task is doing the same thing.
    while (!topicRunning.compareAndSet(false, true)) {
      AppUtils.sleep(1000, "waiting for end of the task of topic " + topic);
    }

    try {
      twitterService.readTweetsOf(topic);
    } catch (Exception ex) {
      LOG.error("read tweets failed, topic: " + topic, ex);
      AppUtils.sleep(millisecondsForHttpRecovering,
          "waiting for the recovering of reading tweets on topic " + topic);
    }

    topicRunning.set(false);
  }
}
