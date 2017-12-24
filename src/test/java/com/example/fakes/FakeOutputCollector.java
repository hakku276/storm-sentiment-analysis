package com.example.fakes;

import java.util.List;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Values;

import lombok.Getter;

public class FakeOutputCollector extends OutputCollector{

	@Getter
	private Values values;
	
	public FakeOutputCollector() {
		super(null);
	}

	@Override
	public List<Integer> emit(List<Object> tuple) {
		this.values = (Values) tuple;
		return null;
	}

}
