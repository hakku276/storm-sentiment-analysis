package com.example.twitter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Data;

@Data
public class Tweet {
	private static final DateFormat utcFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
	private long id;
	private Date createdAt;
	private String text;
	
	public static Tweet fromJSONString(String json){
		if(json == null || json.isEmpty()){
			return null;
		}
		try{
			JSONObject rawTweet = new JSONObject(json);
			Tweet tweet = new Tweet();
			tweet.setId(rawTweet.getLong("id"));
			tweet.setCreatedAt(utcFormat.parse(rawTweet.getString("created_at")));
			tweet.setText(rawTweet.getString("text"));
			return tweet;
		}catch(JSONException e){
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
