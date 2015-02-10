package dBPedia_topic_extractor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MultiMap;
import org.dbpedia.spotlight.annotate.DefaultParagraphAnnotator;
import org.dbpedia.spotlight.disambiguate.ParagraphDisambiguatorJ;
import org.dbpedia.spotlight.disambiguate.TwoStepDisambiguator;
import org.dbpedia.spotlight.exceptions.ConfigurationException;
import org.dbpedia.spotlight.exceptions.InputException;
import org.dbpedia.spotlight.model.SpotlightConfiguration;
import org.dbpedia.spotlight.model.SpotlightFactory;
import org.dbpedia.spotlight.spot.Spotter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sun.jersey.core.util.Base64;


public class subject_extractor {
	
	public static MultiMap dbPedia_subject_extractor(String name,String url,MultiMap map_topics)
	{
		
		Document doc = null;
		try {
			url=java.net.URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		String output ="";		
		try {
			doc = Jsoup.parse(new URL(url), 0);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Elements links = doc.getElementsByTag("a");
		links=doc.getElementsByAttributeValue("rel","dcterms:subject");			
		ArrayList<String> topics=new ArrayList<String>();
		for(Element i :links)
		{
			//topics.add(i.text().replaceAll("category:", ""));			
			//System.out.print(i.text().replaceAll("category:", ""));
			map_topics.put(i.text().replaceAll("category:", ""), name);
		}		
		return map_topics;		
	}
}
