package com.henry.tweetsreader.service;

import com.henry.tweetsreader.service.resources.Tweets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;

public class TwitterApiClientTest {
  @Mock
  private RestTemplate restTemplate;

  private TwitterApiClient twitterApiClient;

  /**
   * Setup for each test case.
   */
  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    twitterApiClient = new TwitterApiClient(1000, 1000);
    ReflectionTestUtils.setField(twitterApiClient,
        "restTemplate", restTemplate);
    ReflectionTestUtils.setField(twitterApiClient,
        "baseUrl", "https://api.twitter.com");
    ReflectionTestUtils.setField(twitterApiClient, "accessToken",
        "AAAAAAAAAAAAAAAAAAAAAEf4AAEAAAAAitZNmH24%2FqilGRjU2Bqul9SV5QI%3D6Qiqa4oQ7vg"
            + "DMFc2LNl1gIUrIE7bC7wUetivCOAy6We8n747gc");
  }

  @Test
  public void testDoExchange() {
    Tweets result = new Tweets();
    ResponseEntity<Tweets> response = new ResponseEntity<>(result, HttpStatus.OK);
    Mockito.when(restTemplate.exchange(any(RequestEntity.class), any(Class.class))).thenReturn(response);

    Map<String, String> params = new HashMap<>();
    params.put("q", "football");

    twitterApiClient.doExchange("/tweets.json", params,
        Tweets.class, HttpMethod.GET, null, null);

    Mockito.verify(restTemplate).exchange(argThat((RequestEntity request) ->
        request.getUrl().toString().equals("https://api.twitter.com/tweets.json?q=football")
          && request.getMethod().equals(HttpMethod.GET)
    ), argThat((Class<Tweets> responseType) ->
        responseType.equals(Tweets.class)
    ));
  }

  @Test
  public void testDoGet() {
    Tweets result = new Tweets();
    ResponseEntity<Tweets> response = new ResponseEntity<>(result, HttpStatus.OK);
    Mockito.when(restTemplate.exchange(any(RequestEntity.class), any(Class.class))).thenReturn(response);

    Map<String, String> params = new HashMap<>();
    params.put("q", "football");

    twitterApiClient.doGet("/tweets.json", params,
        Tweets.class, null);

    Mockito.verify(restTemplate).exchange(argThat((RequestEntity request) ->
        request.getUrl().toString().equals("https://api.twitter.com/tweets.json?q=football")
            && request.getMethod().equals(HttpMethod.GET)
    ), argThat((Class<Tweets> responseType) ->
        responseType.equals(Tweets.class)
    ));

  }

}
