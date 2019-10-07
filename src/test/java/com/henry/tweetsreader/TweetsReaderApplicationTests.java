package com.henry.tweetsreader;

import com.henry.tweetsreader.main.ConsoleOperator;
import com.henry.tweetsreader.service.ContextService;
import com.henry.tweetsreader.main.TweetsReader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

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

//  @Test
//  public void contextLoads() {
//   }

}