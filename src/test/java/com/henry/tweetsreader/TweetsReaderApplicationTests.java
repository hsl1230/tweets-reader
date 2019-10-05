package com.henry.tweetsreader;

import com.henry.tweetsreader.service.TweetsReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class TweetsReaderApplicationTests {
//  @MockBean
//  private TweetsReader tweetsReader;
//
  @MockBean
  private ConsoleOperator consoleOperator;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    Mockito.doReturn("topic1").when(consoleOperator).readLine();
//    Mockito.doNothing().when(tweetsReader).readTopics(Mockito.anyList());
  }

//  @Test
  public void contextLoads() {
  }

}
