package com;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.7
 */
public class Tweets {

	private static String[] tokens = { "9HwmRbVlPtFkit5ijoHw9A",
			"LJFcWnOxhdScxo9DieKIp4007YpSYOZaLfyNvXT6vRQ",
			"542891917-amZW3joJ0KnkHedwLjE4OOSCQiZ1ofIVXUXkNoWq",
			"IFXkAcvQ5oXOxYmoBq8GWN0ekmeebHLHCPTdgRN6F4" };

	public static void main(String[] args) throws IOException {
		TwitterFactory tf = new TwitterFactory(getConfiguration());

		// gets Twitter instance with default credentials
		Twitter twitter = tf.getInstance();

		if (args.length < 1) {
			System.err.println("Usage: command [username]");
			System.exit(-1);
		}
		String user = args[0];

		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient("localhost", 27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		// get the db - create if not exist
		DB db = mongoClient.getDB("xceeddb");
		// get tweets collection
		DBCollection table = db.getCollection("tweets");
		BasicDBObject document = new BasicDBObject();

		// limit to 100 tweets
		Paging pagingOption = new Paging(1, 100);
		try {
			List<Status> statuses;
			statuses = twitter.getUserTimeline(user, pagingOption);

			// put tweets into array
			List<String> allTweets = new ArrayList<String>();
			// top 5 retweeted
			Map<Long, String> topTweets = new TreeMap<Long, String>();
			// top 5 Min Lenght Tweets
			Map<Integer, String> topMinLenghtTweets = new TreeMap<Integer, String>();
			// top 5 Max Lenght Tweets
			Map<Integer, String> topMaxLenghtTweets = new TreeMap<Integer, String>();
			
			for (Status status : statuses) {
				//todo get max retweeted - use status.getRetweetCount()
				//todo get min length tweet - use status.getText().length()
				//todo get max length tweet - use status.getText().length()
				allTweets.add(status.getText());
				// System.out.println("@" + status.getUser().getScreenName()
				// + " - " + status.getText());
			}
			document.put("username", user);
			document.put("tweets", allTweets);
			document.put("captureDate", new Date());
			// insert the document
			table.insert(document);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.exit(-1);
		}
	}

	public static Configuration getConfiguration() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(tokens[0])
				.setOAuthConsumerSecret(tokens[1])
				.setOAuthAccessToken(tokens[2])
				.setOAuthAccessTokenSecret(tokens[3]);
		return cb.build();
	}
}
