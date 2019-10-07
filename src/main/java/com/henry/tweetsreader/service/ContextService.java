package com.henry.tweetsreader.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

import com.henry.tweetsreader.service.resources.SearchMetadata;

import org.springframework.stereotype.Service;

/**
 * this is a service to provide context info like tweets output file path, tweets search metadata applied.
 */
@Service
public class ContextService {
  private String tweetsFilePath;
  private Hashtable<String, SearchMetadata> searchMetadataHashtable = new Hashtable<>();
  private Hashtable<String, Boolean> topicIntializedFlags = new Hashtable<>();

  /**
   * out put file directory of tweets.
   */
  public String getTweetsFilePath() {
    return tweetsFilePath;
  }

  public void setTweetsFilePath(String tweetsFilePath) {
    this.tweetsFilePath = tweetsFilePath;
  }

  public Path getFilePathOf(String topic) {
    return Paths.get(getTweetsFilePath(), topic + ".txt");
  }
  
  public void updateSearchMetadata(String topic, SearchMetadata searchMetadata) {
    searchMetadataHashtable.put(topic, searchMetadata);
  }

  /**
   * return search metadata applied on a topic.
   */
  public SearchMetadata findSearchMetadataOf(String topic) {
    return searchMetadataHashtable.get(topic);
  }

  /**
   * the file related to the topic is initialized or not.
   * @param topic the topic
   * @return boolean
   */
  public boolean isTopicInitialized(String topic) {
    Boolean flag = topicIntializedFlags.get(topic);
    if (flag == null) {
      return false;
    } else {
      return flag;
    }
  }

  public void setTopicInitialized(String topic, boolean value) {
    topicIntializedFlags.put(topic, value);
  }
}