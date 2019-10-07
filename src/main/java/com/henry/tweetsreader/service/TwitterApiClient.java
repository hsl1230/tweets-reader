package com.henry.tweetsreader.service;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class TwitterApiClient {
  private static final Logger LOG = LoggerFactory.getLogger(TwitterApiClient.class);
  private RestTemplate restTemplate;

  @Value("${app.twitterApi.baseUrl:https://api.twitter.com}")
  private String baseUrl;
  @Value("${app.twitterApi.accessToken:AAAAAAAAAAAAAAAAAAAAAEf4AAEAAAAAitZNmH24%2F"
      + "qilGRjU2Bqul9SV5QI%3D6Qiqa4oQ7vgDMFc2LNl1gIUrIE7bC7wUetivCOAy6We8n747gc}")
  private String accessToken;
  
  /**
   * constructor of Twitter api client, RestTemplate instance will be created.
   * @param readTimeout request read timeout
   * @param connectTimeout connection timeout
   */
  public TwitterApiClient(
      @Value("${app.twitterApi.readTimeout:5000}") int readTimeout,
      @Value("${app.twitterApi.connectTimeout:1000}") int connectTimeout) {
    final HttpComponentsClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory();
    requestFactory.setConnectTimeout(connectTimeout);
    requestFactory.setReadTimeout(readTimeout);

    restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
    restTemplate.setInterceptors(singletonList((request, body, execution) -> {
      traceRequest(request, body);
      final StopWatch watch = new StopWatch();
      watch.start();
      final ClientHttpResponse response = execution.execute(request, body);
      watch.stop();
      traceResponse(response, watch.getTotalTimeMillis());
      return response;
    }));
  }

  /**
   * forward rest request to rest template.
   */
  public <RequestBody, ResponseBody> ResponseBody doExchange(
      String path,
      Map<String, String> queryParameters,
      ParameterizedTypeReference<ResponseBody> responseType,
      HttpMethod method,
      RequestBody requestBody,
      MultiValueMap<String, String> headers
  ) {
    RequestEntity request = createRequestEntity(
        path, queryParameters, method, requestBody, headers);
    try {
      ResponseEntity<ResponseBody> response = getRestTemplate().exchange(request, responseType);
      return response.getBody();
    } catch (Exception e) {
      LOG.error("call endpoint error", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * forward rest request to rest template.
   */
  public <RequestBody, ResponseBody> ResponseBody doExchange(
      String path,
      Map<String, String> queryParameters,
      Class<ResponseBody> responseType,
      HttpMethod method,
      RequestBody requestBody,
      MultiValueMap<String, String> headers
  ) {
    RequestEntity request = createRequestEntity(
        path, queryParameters, method, requestBody, headers);
    try {
      ResponseEntity<ResponseBody> response = getRestTemplate().exchange(request, responseType);
      return response.getBody();
    } catch (HttpClientErrorException e) {
      LOG.error("call endpoint error: " + e.getResponseBodyAsString(), e);
      throw new RuntimeException(e);
    } catch (Exception e) {
      LOG.error("call endpoint error", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * convenient method for POST request.
   */
  public <RequestBody, ResponseBody> ResponseBody doPost(
      String path,
      RequestBody requestBody,
      ParameterizedTypeReference<ResponseBody> responseType
  ) {
    return doExchange(path, null, responseType, HttpMethod.POST, requestBody, null);
  }

  /**
   * convenient method for POST request.
   */  
  public <RequestBody, ResponseBody> ResponseBody doPost(
      String path,
      RequestBody requestBody,
      Class<ResponseBody> responseType
  ) {
    return doExchange(path, null, responseType, HttpMethod.POST, requestBody, null);
  }

  /**
   * convenient method for POST request.
   */  
  public <RequestBody, ResponseBody> ResponseBody doPost(
      String path,
      RequestBody requestBody,
      Class<ResponseBody> responseType,
      MultiValueMap<String, String> headers
  ) {
    return doExchange(path, null, responseType, HttpMethod.POST, requestBody, headers);
  }

  /**
   * convenient method for POST request.
   */
  public <RequestBody, ResponseBody> ResponseBody doPost(
      String path,
      RequestBody requestBody,
      ParameterizedTypeReference<ResponseBody> responseType,
      MultiValueMap<String, String> headers
  ) {
    return doExchange(path, null, responseType, HttpMethod.POST, requestBody, headers);
  }

  /**
   * convenient method for GET request.
   */
  public <T> T doGet(
      String path,
      Map<String, String> queryParameters,
      Class<T> responseType,
      MultiValueMap<String, String> headers
  ) {
    return doExchange(path, queryParameters, responseType, GET, null, headers);
  }

  /**
   * convenient method for GET request.
   */
  public <T> T doGet(
      String path,
      Map<String, String> queryParameters,
      ParameterizedTypeReference<T> responseType,
      MultiValueMap<String, String> headers
  ) {
    return doExchange(path, queryParameters, responseType, GET, null, headers);
  }

  /**
   * convenient method for GET request.
   */  
  public<T> T doGet(
      String path,
      Map<String, String> queryParameters,
      Class<T> responseType
  ) {
    return doGet(path, queryParameters, responseType, null);
  }
  
  /**
   * convenient method for GET request.
   */  
  public<T> T doGet(
      String path,
      Map<String, String> queryParameters,
      ParameterizedTypeReference<T> responseType
  ) {
    return doGet(path, queryParameters, responseType, null);
  }

  private HttpHeaders createDefaultHeaders() {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON_UTF8);
    String accessToken = getAccessToken();
    if (accessToken != null) {
      headers.set("Authorization", "Bearer " + accessToken);
    }
    return headers;
  }
  
  private <T> RequestEntity<T> createRequestEntity(
      String path,
      Map<String, String> queryParameters,
      HttpMethod method,
      T requestBody,
      MultiValueMap<String, String> headers
  ) {
    Assert.notNull(path, "'path' must not be null");

    StringBuilder requestParam = new StringBuilder();
    if (queryParameters != null) {
      queryParameters.entrySet().stream().forEach(entry -> {
        if (requestParam.length() == 0) {
          requestParam.append("?");
        } else {
          requestParam.append("&");
        }
        requestParam.append(entry.getKey()).append("=").append(entry.getValue());
      });
    }

    HttpHeaders theHeaders = createDefaultHeaders();
    if (headers != null) {
      headers.entrySet().forEach(
          entry -> entry.getValue().forEach(
              value -> theHeaders.add(entry.getKey(), value)));
    }

    try {
      URI uri;
      if (getBaseUrl().endsWith("/") && path.startsWith("/")) {
        uri = new URI(getBaseUrl() + path.substring(1) + requestParam.toString());
      } else {
        uri = new URI(getBaseUrl() + path + requestParam.toString());
      }

      return new RequestEntity(requestBody, theHeaders, method, uri);
    } catch (Exception e) {
      LOG.error("create uri error", e);
      throw new RuntimeException(e);
    }
  }


  private String getAccessToken() {
    return accessToken;
  }

  private RestTemplate getRestTemplate() {
    return restTemplate;
  }


  private String getBaseUrl() {
    return baseUrl;
  }

  private static void traceRequest(HttpRequest request, byte[] body) {
    LOG.trace("===========================request begin===========================");
    LOG.trace("URI         : {}", request.getURI());
    LOG.trace("Method      : {}", request.getMethod());
    LOG.trace("Headers     : {}", request.getHeaders());
    LOG.trace("Request body: {}", new String(body));
    LOG.trace("=========================== request end ===========================");
  }

  private static void traceResponse(ClientHttpResponse response, long duration) throws IOException {
    try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody()))) {
      final StringBuilder inputStringBuilder = new StringBuilder();
      String line = bufferedReader.readLine();
      while (line != null) {
        inputStringBuilder.append(line);
        inputStringBuilder.append(System.lineSeparator());
        line = bufferedReader.readLine();
      }
      LOG.trace("===========================response begin===========================");
      LOG.trace("Respond time : {}", duration);
      LOG.trace("Status code  : {}", response.getStatusCode());
      LOG.trace("Status text  : {}", response.getStatusText());
      LOG.trace("Headers      : {}", response.getHeaders());
      LOG.trace("Response body: {}", inputStringBuilder.toString());
    } finally {
      LOG.trace("=========================== response end ===========================");
    }
  }

}
