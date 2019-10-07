# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/gradle-plugin/reference/html/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

## Requirement
Continuesly read tweets of up to 5 topics and save them in separated files.

## Investigate
Twitter Api can be used to read tweets.
ref: https://developer.twitter.com/en/docs/tweets/search/overview
Using OAuth to do the authentication
ref: https://developer.twitter.com/en/docs/basics/authentication/overview/oauth 

## Steps to implement.
### 1. Open twitter developer account
### 2. Get access token using customer api keys
ref: https://developer.twitter.com/en/docs/basics/authentication/overview/application-only

## Highlight tech
### Spring boot, microservice/rest, oauth, RestTemplate, multi threads


## Design considerations
### 1. Separate thread should be used to read tweets of each topic.
### 2. ThreadPoolTaskExecutor is used to do thead pooling and support graceful shutdown of the application.
### 3. No Infinit loop is used for every topic reader to support recovering from errors easily.
### 4. UserDefinedFileAttribute is used to save last tweet id saved into the file so that it knows where we should start next time the app starts up.

