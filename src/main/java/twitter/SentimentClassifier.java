package twitter;import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.JointClassification;
import com.aliasi.classify.LMClassifier;
import com.aliasi.util.AbstractExternalizable;

import java.io.File;
import java.io.IOException;import java.lang.ClassNotFoundException;import java.lang.String;

public class SentimentClassifier {

	String[] categories;
	LMClassifier class1;

	public SentimentClassifier() {

		try {
			class1 = (LMClassifier) AbstractExternalizable.readObject(new File("classifier.txt"));
			categories = class1.categories();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	public double classify(String text) {
		JointClassification classification = class1.classify(text);
		return classification.score(0);
	}

	public String classifyString(String text) {
		ConditionalClassification classification = class1.classify(text);
		return classification.bestCategory();
//		return classification.toString() + "   " + classification.bestCategory();
	}

}