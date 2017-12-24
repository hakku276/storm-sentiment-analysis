package com.example;

import lombok.Getter;

public enum Feedback {
	POSITIVE("POSITIVE"), NEGATIVE("NEGATIVE"), NEUTRAL("NEUTRAL");

	@Getter
	private final String value;

	private Feedback(String value) {
		this.value = value;
	}
}
