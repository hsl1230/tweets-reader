# Getting Started

## Requirement
  Continuously read tweets of up to 5 topics and save them into separated files.

## Investigate
### 1. Twitter Api can be used to read tweets.
ref: https://developer.twitter.com/en/docs/tweets/search/overview
### 2. Using OAuth to do the authentication
ref: https://developer.twitter.com/en/docs/basics/authentication/overview/oauth 

## Steps to implement.
### 1. Open a twitter developer account
ref: https://help.twitter.com/en/using-twitter/create-twitter-account

### 2. Get access token using customer api keys
ref: https://developer.twitter.com/en/docs/basics/authentication/overview/application-only

## Highlight tech
  Spring boot, microservice/rest, oauth, RestTemplate, multi threads, gradle wrapper

## Design considerations
  1. Separate thread should be used to read tweets of each topic.
  2. ThreadPoolTaskExecutor is used to do thead pooling and support graceful shutdown of the application.
  3. No Infinite loop is used for every topic reader to support recovering from errors easily.
  4. UserDefinedFileAttribute is used to save last tweet id saved into the file so that it knows where we should start next time the app starts up.
  5. gradlew instead of maven, no need to install gradle.
