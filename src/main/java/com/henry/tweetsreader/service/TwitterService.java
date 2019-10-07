package com.henry.tweetsreader.service;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.henry.tweetsreader.service.resources.SearchMetadata;
import com.henry.tweetsreader.service.resources.Tweets;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class TwitterService {
  private static final Logger LOG = LoggerFactory.getLogger(TwitterService.class);

  private static final String ENCODED_CONSUMER_KEY_SECRET =
      "RG1sYTIyc0tRYW5pNUJwR2k2R0I1ZlhJaDpEcUhjV0ZuVHpiQjBSQlNDVUpDa2w3emlYeE9oYVpuT051MlhObUVFR0JWOHkzeXVQTw==";

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
      queryParameters.put("q", "%23" + topic);
      queryParameters.put("count", "50");

      long maxId = metaDataService.getMaxTweetIdOf(topic);
      if (maxId == -1 && !contextService.isTopicInitialized(topic)) {
        Files.deleteIfExists(path);
        contextService.setTopicInitialized(topic, true);
      } else {
        queryParameters.put("max_id", String.valueOf(maxId));
      }

      result = client.doGet("/1.1/search/tweets.json", queryParameters, Tweets.class);
    } else {
      result = client.doGet("/1.1/search/tweets.json"
          + metadata.getNextResults(), null, Tweets.class);
    }

    try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
      result.getStatuses().stream().forEach(tweet -> {
        try {
          writer.write(objectMapper.writeValueAsString(tweet));
          writer.write("\n");
        } catch (Exception ex) {
          LOG.error("write tweet failed! tweet id: " + tweet.getIdStr(), ex);
        }
      });

      metaDataService.logMaxTweetId(topic, result.getSearchMetadata().getSinceId());

      contextService.updateSearchMetadata(topic, result.getSearchMetadata());
    } catch (Exception ex) {
      throw new RuntimeException("open file failed! path:" + path, ex);
    }
  }


}
