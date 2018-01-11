# Sentiment Analysis for Storm

Simple Sentiment Analysis storm topology, written as a demo for Seminar (Advanced Data Management) in University of Stuttgart.  

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

Required software:

```
Maven for compiling the code
Storm and Zookeeper for deployment of code
Twitter storm app and access to consumer key, consumer secret, token and token secret 
```

### Installation and Deployment

Follow setup of storm and zookeeper as provided in https://www.tutorialspoint.com/apache_storm/apache_storm_installation.htm

Following environment variables set:
```
consumerkey=<YOUR CONSUMER KEY>
secretkey=<YOUR CONSUMER SECRET>
token=<YOUR TOKEN>
tokensecret=<YOUR TOKEN SECRET>
```

Compile the code into the jar artifact
```
mvn package
```

Run zookeeper and storm and finally deploy the jar

```
storm jar </path/to/jar/jarfile> com.example.SentimentAnalyzer live "<comma separated search queries>" 
```

The comma separated search queries are utilized for filtering the statuses
