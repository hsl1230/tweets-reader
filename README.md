# tweets-reader
  Scrawling tweets of specified topics to separated files

## Prerequisites
  git 2.6 or above is installed
  
  jdk 1.8 is installed

## How to build it
### 1. create a new directory named "workspace" for example, change into it
    $> mkdir workspace
    $> cd workspace
### 2. clone the project from github using: 
    $> git clone https://github.com/hsl1230/tweets-reader.git
### 3. change directory to tweets-reader and do a clean build
    $> cd tweets-reader
    $> ./gradlew clean build
## How to run it
### method 1
    $> java -jar ./build/libs/tweets-reader-1.0.0.jar
### method 2
    $> java -jar ./build/libs/tweets-reader-1.0.0.jar --topic=football,baseball,baksetball --outputPath=./tweets-data
### method 3
    $> ./gradlew bootRun
    
