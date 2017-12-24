package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.example.fakes.FakeOutputCollector;

public class TweetParseBoltTest {
	
	private FakeOutputCollector collector;
	private TweetParseBolt bolt;
	
	@Before
	public void init(){
		collector = new FakeOutputCollector();
		bolt = new TweetParseBolt();
		bolt.prepare(null, null, collector);
	}
	
	@Test
	public void testHundredPositiveBoltParse(){
		Tuple fakeTuple = Mockito.mock(Tuple.class);
		when(fakeTuple.getString(0)).thenReturn("Iphone is very good");
		
		bolt.execute(fakeTuple);
		
		verify(fakeTuple, times(1)).getString(0);
		
		//assert that something was emitted
		assertNotNull(collector.getValues());
		
		//verify the value that was emitted
		Values values = collector.getValues();
		assertEquals(values.get(0), Feedback.POSITIVE.getValue());
		assertEquals(values.get(1), 1.0f);
	}
	
	@Test
	public void testHundredNegativeBoltParse(){
		Tuple fakeTuple = Mockito.mock(Tuple.class);
		when(fakeTuple.getString(0)).thenReturn("Iphone is very bad");
		
		bolt.execute(fakeTuple);
		
		verify(fakeTuple, times(1)).getString(0);
		
		//assert that something was emitted
		assertNotNull(collector.getValues());
		
		//verify the value that was emitted
		Values values = collector.getValues();
		assertEquals(values.get(0), Feedback.NEGATIVE.getValue());
		assertEquals(values.get(1), 1.0f);
	}
	
	@Test
	public void testFiftyFifty(){
		Tuple fakeTuple = Mockito.mock(Tuple.class);
		when(fakeTuple.getString(0)).thenReturn("Iphone is very good, but the camera is bad");
		
		bolt.execute(fakeTuple);
		
		verify(fakeTuple, times(1)).getString(0);
		
		//assert that something was emitted
		assertNotNull(collector.getValues());
		
		//verify the value that was emitted
		Values values = collector.getValues();
		assertEquals(values.get(0), Feedback.NEUTRAL.getValue());
		assertEquals(values.get(1), 0.0f);
	}
}
