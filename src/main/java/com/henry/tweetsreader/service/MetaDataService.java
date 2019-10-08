package com.henry.tweetsreader.service;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetaDataService {
  private static final Logger LOG = LoggerFactory.getLogger(MetaDataService.class);

  private ContextService contextService;

  @Autowired
  public MetaDataService(ContextService contextService) {
    this.contextService = contextService;
  }

  /**
   * return max tweet id stored in the user defined file attribute.
   * @param topic topic
   * @return max tweet id
   */
  public long getMaxTweetIdOf(String topic) {
    Path path = contextService.getFilePathOf(topic);
    long maxTweetId = -1;
    if (!Files.exists(path)) {
      return maxTweetId;
    }

    try {
      if (path.getFileSystem().supportedFileAttributeViews().contains("user")) {
        UserDefinedFileAttributeView attsView = Files.getFileAttributeView(
            path, UserDefinedFileAttributeView.class);
        if (attsView.list().contains("maxTweetId")) {
          final ByteBuffer buf = ByteBuffer.allocate(
              attsView.size("maxTweetId")
          );
          attsView.read("maxTweetId", buf);
          buf.flip();
          maxTweetId = Long.valueOf(
              StandardCharsets.UTF_8.decode(buf).toString()
          );
        }
      } else {
        LOG.warn("#######UserDefinedFileAttributeView is not supported");
      }
    } catch (Exception ex) {
      LOG.warn("tweet id can not be extracted from the file attribute", ex);
    }

    return maxTweetId;
  }


  /**
   * log max tweet id as a user defined file attribute.
   * @param topic topic name
   * @param value tweet id
   */
  public void logMaxTweetId(String topic, long value) {
    Path path = contextService.getFilePathOf(topic);
    if (!Files.exists(path)) {
      return;
    }

    try {
      if (path.getFileSystem().supportedFileAttributeViews().contains("user")) {
        UserDefinedFileAttributeView attsView = Files.getFileAttributeView(
            path, UserDefinedFileAttributeView.class);
        ByteBuffer buffer = Charset.defaultCharset().encode(String.valueOf(value));
        attsView.write("maxTweetId", buffer);
      } else {
        LOG.warn("UserDefinedFileAttributeView is not supported");
      }
    } catch (Exception ex) {
      LOG.warn("tweet id can not be saved into the file attribute", ex);
    }
  }
}
