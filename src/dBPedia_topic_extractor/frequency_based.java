package dBPedia_topic_extractor;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * @author Kiran K 
 */

public class frequency_based {
	public static Map<String,Integer> stanford_based_freq(String i)
	{
		Properties props = new Properties();
		props.put("annotators", ",tokenize,ssplit,pos,lemma");//pos,parse,lemma,ner,dcoref");	
		PrintWriter out;
		out = new PrintWriter(System.out);
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation annotation;
		annotation = new Annotation(i);
		pipeline.annotate(annotation);
	    String phrases="";
	    Map<String,Integer> freq=new HashMap<String,Integer>();
	    List<String> stop_words=Arrays.asList(file_operations.read_from_file("stop_words.txt").split(","));
	    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
	    String text;
	    for(CoreMap sentence:sentences)
	    {
	    	List<CoreLabel>tokens= sentence.get(TokensAnnotation.class);
	    	for(CoreLabel token:tokens)
	    	{
	    		text=token.get(TextAnnotation.class).toLowerCase();
	    		text=text.replaceAll("[^\\w\\s]","" ).toLowerCase();
	    		if(text.length()==0)
	    			continue;
	    		if(stop_words.contains(text))
	    			continue;
	    		if(freq.containsKey(text))
					freq.put(text,Integer.valueOf(String.valueOf(freq.get(text)))+1);
				else
					freq.put(text, 1);
	    	}
	    }
	    Set<String> keys=freq.keySet();
	    Map<String,Integer> freq_results=new HashMap<String,Integer>();
		for(String key:keys)
		{
			if(Integer.valueOf(freq.get(key).toString())>1)
			{
				freq_results.put(key, freq.get(key));
			}
		
		}    
		return freq_results;
	}
	public static void get_frequency_based(String text) //without using Stanford Core NLP
	{
		text=text.replaceAll("[^\\w\\s]"," " ).toLowerCase();
		ArrayList<String> x=new ArrayList<String>(Arrays.asList(text.split("\\s+")));
		Map freq=new HashMap<String,Integer>();
		
		Set<String> stop_words=new HashSet<String>(Arrays.asList(file_operations.read_from_file("stop_words.txt").split(",")));
		HashSet<String> temp1=new HashSet<String>(x);
		
		temp1.removeAll(stop_words);
		x.clear();
		x.addAll(temp1);
		for(int i=0;i<x.size();i++)
		{
			if(freq.containsKey(x.get(i)))
				freq.put(x.get(i),Integer.valueOf(String.valueOf(freq.get(x.get(i))))+1);
			else
				freq.put(x.get(i), 1);				
		}
		Set<String> keys=freq.keySet();
		System.out.println("output");
		for(String key:keys)
		{
			System.out.println(key+" , "+freq.get(key));
		}
	}
	
}
