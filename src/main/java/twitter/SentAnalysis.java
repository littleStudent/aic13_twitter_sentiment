package twitter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import at.ac.tuwien.infosys.cloudscale.annotations.CloudObject;
import at.ac.tuwien.infosys.cloudscale.annotations.DestructCloudObject;
import at.ac.tuwien.infosys.cloudscale.annotations.FileDependency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import twitter.SentimentClassifier;

@CloudObject
@FileDependency(files = {"classifier.txt"})
public class SentAnalysis {

	SentimentClassifier sentClassifier;
	private double sentResult;

	String text;
	String from;
	String to;
	int parts;
	int part;

	public SentAnalysis(String text, String from, String to, int parts, int part) {
		this.sentClassifier = new SentimentClassifier();
		this.text = text;
		this.from = from;
		this.to = to;
		this.parts = parts;
		this.part = part;
	}

	public void run() {
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			this.performQuery(text, from, to, parts, part);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void performQuery(String text, String from, String to, int parts, int part) throws InterruptedException, IOException {
		URL url = new URL(
				"http://54.247.105.211:3001/search?search=" + text + "&from=" + from + "&to=" + to + "&parts=" + parts + "&part=" + part);
		URLConnection connection = url.openConnection();
		System.out.println("http://54.247.105.211:3001/search?search=" + text + "&from=" + from + "&to=" + to + "&parts=" + parts + "&part=" + part);
		String line;
		java.lang.StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while((line = reader.readLine()) != null) {
			builder.append(line);
		}

		JSONTokener tokener = new JSONTokener(builder.toString());
		List<Double> result = new ArrayList<Double>();

		try {
			JSONArray finalResult = new JSONArray(tokener);
			for (int i = 0; i < finalResult.length(); i++) {
				JSONObject tweet = finalResult.getJSONObject(i);
				result.add(getValueForString(sentClassifier.classifyString(tweet.getJSONObject("obj").getString("text"))));
			}
//			double sent = sentClassifier.classify(status.getText());
//			String sentString = sentClassifier.classifyString(status.getText());
//			System.out.println("Sentiment: " + sentString + " " + sent);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		Double sum = 0.0;
		for (Double value : result) {
			sum += value;
		}
		System.out.println("RESULT: " + sum / result.size());
		sentResult =  sum / result.size();
	}

	private Double getValueForString(String value) {
		if (value.equalsIgnoreCase("pos")) {
			return 10.0;
		} else if (value.equalsIgnoreCase("neg")) {
			return 0.0;
		} else {
			return 5.0;
		}
	}

	@DestructCloudObject
	public double getResult() {
		return sentResult;
	}
}
