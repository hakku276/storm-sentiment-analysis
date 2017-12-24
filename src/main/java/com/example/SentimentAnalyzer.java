package com.example;

import java.util.Arrays;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;

public class SentimentAnalyzer {

	public static void main(String[] args) {
		// [prod | local] [key1,key2,key3] <--debug>
		if (args.length < 2) {
			throw new RuntimeException("No Arguments defined");
		}

		// Create Config instance for cluster configuration
		Config config = new Config();

		if (args.length >= 3 && args[2] == "--debug") {
			config.setDebug(true);
		}

		// get the tags
		String[] tags = args[1].split(",");

		System.out.println(Arrays.toString(tags));

		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout", new TwitterFeedSpout(tags));
		builder.setBolt("parse", new TweetParseBolt()).shuffleGrouping("spout");
		builder.setBolt("aggregation", new TwitterAggregationBolt()).shuffleGrouping("parse");

		if (args[0].equals("local")) {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("SentimentAnalyzer", config, builder.createTopology());

			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Stop the topology
			cluster.shutdown();
		} else {
			try {
				StormSubmitter.submitTopology("SentimentAnalyzer", config, builder.createTopology());
			} catch (AlreadyAliveException | InvalidTopologyException | AuthorizationException e) {
				e.printStackTrace();
			}
		}
	}

}
