package com.henry.tweetsreader.service;

import com.henry.tweetsreader.service.resources.SearchMetadata;
import org.springframework.stereotype.Service;

import java.util.Hashtable;

@Service
public class ContextService {
  private String tweetsFilePath;
  private Hashtable<String, SearchMetadata> searchMetadataHashtable = new Hashtable<>();

  public String getTweetsFilePath() {
    return tweetsFilePath;
  }

  public void setTweetsFilePath(String tweetsFilePath) {
    this.tweetsFilePath = tweetsFilePath;
  }

  public void updateSearchMetadata(String topic, SearchMetadata searchMetadata) {
    searchMetadataHashtable.put(topic, searchMetadata);
  }

  public SearchMetadata findSearchMetadataOf(String topic) {
    return searchMetadataHashtable.get(topic);
  }
}