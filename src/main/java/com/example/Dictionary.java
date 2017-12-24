package com.example;

import java.util.HashSet;
import java.util.Set;

public class Dictionary {
	private static Set<String> positiveWords;
	
	private static Set<String> negativeWords;
	
	static{
		positiveWords = new HashSet<>();
		positiveWords.add("good");
		positiveWords.add("like");
		positiveWords.add("awesome");
		positiveWords.add("love");
		positiveWords.add("great");
		
		negativeWords = new HashSet<>();
		negativeWords.add("bad");
		negativeWords.add("hate");
		negativeWords.add("dislike");
	}
	
	public static boolean isPositive(String word){
		return positiveWords.contains(word);
	}
	
	public static boolean isNegative(String word){
		return negativeWords.contains(word);
	}
}
