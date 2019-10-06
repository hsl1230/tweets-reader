package com.henry.tweetsreader;

import com.henry.tweetsreader.service.ConsoleOperator;
import com.henry.tweetsreader.service.ContextService;
import com.henry.tweetsreader.service.TweetsReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

//@RunWith(MockitoJUnitRunner.class)
//@SpringBootTest(classes=TweetsReaderApplication.class)
public class TweetsReaderApplicationTests {
  @Mock
  private TweetsReader tweetsReader;

  @Mock
  private ConsoleOperator consoleOperator;

  @Mock
  private ContextService contextService;

  @Before
  public void setup() throws IOException {
//    MockitoAnnotations.initMocks(this);
    Mockito.when(consoleOperator.readLine()).thenReturn("topic1");
    Mockito.doNothing().when(tweetsReader).readTopics(Mockito.anyList());
    Mockito.doReturn(".").when(contextService).getTweetsFilePath();
  }

  @Test
  public void contextLoads() {
   }

}