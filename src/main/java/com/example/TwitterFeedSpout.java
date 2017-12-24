package com.example;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import com.example.twitter.Tweet;
import com.example.twitter.TwitterClient;

public class TwitterFeedSpout extends BaseRichSpout {

	/**
	 * generated
	 */
	private static final long serialVersionUID = 2125663795992676430L;
	private SpoutOutputCollector collector;
	private TwitterClient client;
	private final String[] tags;

	public TwitterFeedSpout(String[] tags) {
		this.tags = tags;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet", "msgId"));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		this.client = TwitterClient.getInstance(tags);
	}

	@Override
	public void nextTuple() {
		Tweet tweet = this.client.getTweet(100, TimeUnit.MILLISECONDS);
		if (tweet != null) {
			this.collector.emit(new Values(tweet.getText(), tweet.getId()));
		}
	}

	@Override
	public void close() {
		this.client.close();
	}

}
