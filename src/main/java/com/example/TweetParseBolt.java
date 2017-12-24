package com.example;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class TweetParseBolt extends BaseRichBolt {

	private OutputCollector collector;

	@Override
	public void execute(Tuple tuple) {
		String tweet = tuple.getString(0);

		String[] words = tweet.split(" ");
		int count = 0;
		int positiveCount = 0;
		int negativeCount = 0;
		for (String word : words) {
			word = word.replaceAll(",", "");
			word = word.replace(".", "");
			if (Dictionary.isPositive(word)) {
				count++;
				positiveCount++;
			} else if (Dictionary.isNegative(word)) {
				count++;
				negativeCount++;
			}
		}

		float negativity = 0.0f;
		float positivity = 0.0f;
		if (count > 0) {
			negativity = negativeCount / count;
			positivity = positiveCount / count;
		}
		if (positivity > negativity) {
			// positive feedback
			collector.emit(new Values(Feedback.POSITIVE.getValue(), positivity));
		} else if (negativity > positivity) {
			// negative feedback
			collector.emit(new Values(Feedback.NEGATIVE.getValue(), negativity));
		} else {
			// neutral feedback
			collector.emit(new Values(Feedback.NEUTRAL.getValue(), 0.0f));
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// feedback is the positive or negative view this tweet displays
		// percent is the percentage of positivity (simple calculation)
		declarer.declare(new Fields("feedback", "percent"));
	}

}
