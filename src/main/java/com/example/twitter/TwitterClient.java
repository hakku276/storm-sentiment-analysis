package com.example.twitter;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterClient {
	private static final String ENV_KEY_CONSUMERKEY = "consumerkey";
	private static final String ENV_KEY_CONSUMERSECRET = "secretkey";
	private static final String ENV_KEY_TOKEN = "token";
	private static final String ENV_KEY_TOKENSECRET = "tokensecret";

	private static TwitterClient client;
	private final String consumerKey;
	private final String secretKey;
	private final String token;
	private final String tokenSecret;
	private final Client twitterClient;
	
	private BlockingQueue<String> msgQueue;
	private BlockingQueue<Event> eventQueue;

	private TwitterClient() {
		consumerKey = System.getenv(ENV_KEY_CONSUMERKEY);
		secretKey = System.getenv(ENV_KEY_CONSUMERSECRET);
		token = System.getenv(ENV_KEY_TOKEN);
		tokenSecret = System.getenv(ENV_KEY_TOKENSECRET);
		final Authentication twitterAuth = new OAuth1(consumerKey, secretKey, token, tokenSecret);

		/**
		 * Set up your blocking queues: Be sure to size these properly based on
		 * expected TPS of your stream
		 */
		msgQueue = new LinkedBlockingQueue<>(100000);
		eventQueue = new LinkedBlockingQueue<>(1000);

		/**
		 * Declare the host you want to connect to, the endpoint, and
		 * authentication (basic auth or oauth)
		 */
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		List<String> terms = Lists.newArrayList("#iphone");
		hosebirdEndpoint.trackTerms(terms);
		hosebirdEndpoint.languages(Lists.newArrayList("en"));

		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01").hosts(hosebirdHosts)
				.authentication(twitterAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue)).eventMessageQueue(eventQueue);

		twitterClient = builder.build();
		// Attempts to establish a connection.
		twitterClient.connect();
	}

	public static TwitterClient getInstance() {
		if (client == null) {
			client = new TwitterClient();
		}
		return client;
	}
	
	public Tweet getTweet() throws InterruptedException{
		return Tweet.fromJSONString(msgQueue.take());
	}
	
	public Tweet getTweet(long timeout, TimeUnit unit){
		try{
			return Tweet.fromJSONString(msgQueue.poll(timeout, unit));
		}catch(InterruptedException e){
			System.out.println("Interrupted while polling for entries in the queue");
		}
		return null;
	}
	
	public void close(){
		twitterClient.stop();
	}
}
