package com.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

public class TwitterAggregationBolt extends BaseRichBolt {

	/**
	 * Generated
	 */
	private static final long serialVersionUID = 5962138980860793392L;
	private long count = 0;
	private long positiveCount = 0;
	private long negativeCount = 0;
	private long neutralCount = 0;

	private float positivity = 0.0f;
	private float negativity = 0.0f;
	private float neutrality = 0.0f;
	private OutputCollector collector;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		count++;
		String view = input.getString(0);
		float percent = input.getFloat(1);

		Feedback fb = Feedback.valueOf(view);

		switch (fb) {
		case NEGATIVE:
			negativity = (negativeCount * negativity + percent) / count;
			negativeCount++;
			break;
		case POSITIVE:
			positivity = (positiveCount * positivity + percent) / count;
			positiveCount++;
			break;
		case NEUTRAL:
			neutralCount++;
			neutrality = neutralCount / count;
			break;
		}
		collector.ack(input);
	}

	@Override
	public void cleanup() {
		System.out.println("Tweets Processed: " + count + "\r\n");
		System.out.println(String.format("Positivity: %2.2f %%\r\n", positivity * 100));
		System.out.println(String.format("Negativity: %2.2f %%\r\n", negativity * 100));
		System.out.println(String.format("Neutrality: %2.2f %%\r\n", neutrality * 100));
		
		File file = new File("output.txt");
		try {
			FileWriter writer = new FileWriter(file);
			writer.write("Tweets Processed: " + count);
			writer.write(String.format("Positivity: %2.2f %%", positivity * 100));
			writer.write(String.format("Negativity: %2.2f %%", negativity * 100));
			writer.write(String.format("Neutrality: %2.2f %%", neutrality * 100));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
