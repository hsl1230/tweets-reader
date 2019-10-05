package com.henry.tweetsreader.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;

@Service
public class MetaDataService {
  private ContextService contextService;

  @Autowired
  public MetaDataService(ContextService contextService) {
    this.contextService = contextService;
  }

  public int getMaxTweetIdOf(String topic) throws IOException {
    Path path = Paths.get(contextService.getTweetsFilePath(), topic + ".txt");
    FileStore fileStore = Files.getFileStore(path);
    int maxTweetId = -1;
    if (fileStore.supportsFileAttributeView(UserDefinedFileAttributeView.class)) {
      UserDefinedFileAttributeView attsView = Files.getFileAttributeView(
          path, UserDefinedFileAttributeView.class);
      if (attsView.list().contains("maxTweetId")) {
        final ByteBuffer buf = ByteBuffer.allocate(
            attsView.size("maxTweetId")
        );
        attsView.read("maxTweetId", buf);
        buf.flip();
        maxTweetId = Integer.valueOf(
            StandardCharsets.UTF_8.decode(buf).toString()
        );
      }
    }

    return maxTweetId;
  }

  public void logMaxTweetId(String topic, int value) throws IOException {
    Path path = Paths.get(contextService.getTweetsFilePath(), topic + ".txt");
    FileStore fileStore = Files.getFileStore(path);
    if (fileStore.supportsFileAttributeView(UserDefinedFileAttributeView.class)) {
      UserDefinedFileAttributeView attsView = Files.getFileAttributeView(
          path, UserDefinedFileAttributeView.class);
      ByteBuffer buffer = Charset.defaultCharset().encode(String.valueOf(value));
      attsView.write("maxTweetId", buffer);
    }

  }
}
