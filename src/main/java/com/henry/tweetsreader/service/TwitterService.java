package com.henry.tweetsreader.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.henry.tweetsreader.service.resources.SearchMetadata;
import com.henry.tweetsreader.service.resources.Tweets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class TwitterService {
  private static final Logger LOG = LoggerFactory.getLogger(TwitterService.class);

  private TwitterApiClient client;
  private ContextService contextService;
  private MetaDataService metaDataService;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  public TwitterService(TwitterApiClient client, ContextService contextService, MetaDataService metaDataService) {
    this.client = client;
    this.contextService = contextService;
    this.metaDataService = metaDataService;
  }

  /**
   * read and save tweets of specified topic.
   * @param topic topic of tweets
   */
  public void readTweetsOf(String topic) {
    Assert.hasText(topic, "topic must to be specified");

    Path path = Paths.get(contextService.getTweetsFilePath());
    if (!Files.isDirectory(path)) {
      path.toFile().mkdirs();
    }

    Tweets result;

    SearchMetadata metadata = contextService.findSearchMetadataOf(topic);
    path = contextService.getFilePathOf(topic);
    if (metadata == null || metadata.getNextResults() == null) {
      Map<String, String> queryParameters = new HashMap<>();
      queryParameters.put("q", encodeQuery("#" + topic));
      queryParameters.put("count", "50");

      long maxId = metaDataService.getMaxTweetIdOf(topic);
      if (maxId == -1 && !contextService.isTopicInitialized(topic)) {
        // user defined file attribute is not supported, will do it from very beginning
        try {
          Files.deleteIfExists(path);
        } catch (IOException e) {
          throw new RuntimeException("delete file failed! path:" + path, e);
        }
        contextService.setTopicInitialized(topic, true);
      } else {
        // continue from previous reading.
        queryParameters.put("max_id", String.valueOf(maxId));
      }

      result = client.doGet("/1.1/search/tweets.json", queryParameters, Tweets.class);
    } else {
      result = client.doGet("/1.1/search/tweets.json"
          + metadata.getNextResults(), null, Tweets.class);
    }

    try (BufferedWriter writer = Files.newBufferedWriter(
        path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
      result.getStatuses().stream().forEach(tweet -> {
        try {
          writer.write(objectMapper.writeValueAsString(tweet));
          writer.write("\n");
        } catch (Exception ex) {
          LOG.error("write tweet failed! tweet id: " + tweet.getIdStr(), ex);
        }
      });

      // save for recovering from previous reading when restart the app.
      metaDataService.logMaxTweetId(topic, result.getSearchMetadata().getSinceId());

      contextService.updateSearchMetadata(topic, result.getSearchMetadata());
    } catch (Exception ex) {
      throw new RuntimeException("open file failed! path:" + path, ex);
    }
  }

  private String encodeQuery(String query) {
    try {
      return URLEncoder.encode(query, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return query.replace("#", "%23");
    }
  }
}
