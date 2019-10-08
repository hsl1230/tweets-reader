package com.henry.tweetsreader.service;

import com.henry.tweetsreader.TweetsReaderApplication;
import com.henry.tweetsreader.main.ConsoleOperator;
import com.henry.tweetsreader.main.TweetsReader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.ApplicationArguments;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static org.mockito.ArgumentMatchers.argThat;

public class MyRunnerTest {
  @Mock
  private ConsoleOperator consoleOperator;

  @Mock
  private TweetsReader tweetsReader;

  @Mock
  private ContextService contextService;

  @Mock
  private ApplicationArguments arguments;

  @InjectMocks
  private TweetsReaderApplication.MyRunner myRunner;

  /**
   * Setup for each test case.
   */
  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    Mockito.doReturn("football basketball baseball movie fun").when(consoleOperator).readLine();
  }

  @Test
  public void testRun() throws Exception {
    Mockito.doReturn(false).when(arguments).containsOption("topic");
    Mockito.doReturn(true).when(arguments).containsOption("outputPath");
    Mockito.doReturn(Arrays.asList("./tweets-data")).when(arguments).getOptionValues("outputPath");

    myRunner.run(arguments);

    Mockito.verify(tweetsReader).readTopics(argThat((Set<String> topics) ->
        topics.contains("football")
        && topics.contains("basketball")
        && topics.contains("fun")
    ));

    Mockito.verify(contextService).setTweetsFilePath(argThat((String path) ->
        path.equals("./tweets-data")
    ));
  }
}
