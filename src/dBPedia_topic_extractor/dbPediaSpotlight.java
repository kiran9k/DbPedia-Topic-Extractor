package dBPedia_topic_extractor;



import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.Text;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;











import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
/**
 * @author Kiran K 
 */

public class dbPediaSpotlight   
{
	 private static HttpClient client = new HttpClient();
	 private final static String API_URL = "http://spotlight.dbpedia.org/";
	 private static final double CONFIDENCE = 0.0;
	 private static final int SUPPORT = 0;
	 public Logger log = Logger.getLogger(this.getClass());
	 
	 public static String request(HttpMethod method)  
	 {

	        String response = null;
	        // Provide custom retry handler is necessary
	        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
	                new DefaultHttpMethodRetryHandler(3, false));
	        try {
	            // Execute the method.
	            int statusCode = client.executeMethod(method);
	            if (statusCode != HttpStatus.SC_OK) {	              
	            }	            
	            // Read the response body.
	            response=getStringFromInputStream(method.getResponseBodyAsStream());
	            
	            /*byte[] responseBody = method.getResponseBody(); //TODO Going to buffer response body of large or unknown size. Using getResponseBodyAsStream instead is recommended.
	            // Deal with the response.
	            // Use caution: ensure correct character encoding and is not binary data
	            response = new String(responseBody);*/
	        } catch (HttpException e) {   	            
	        } catch (IOException e) {          	            	            
	        } finally {
	            // Release the connection.
	            method.releaseConnection();
	        }
	        return response;
	 }
	 
	 private static String getStringFromInputStream(InputStream is) {
		 
			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();
	 
			String line;
			try {
	 
				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
	 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	 
			return sb.toString();
	 
		}
	 
	 public static Map<String,Map<String,String>> extract(Text text) 
	 {
		 Map<String,Map<String,String>> results=new HashMap<String,Map<String,String>>();
		 HashSet<Map<String,String>> unique=new HashSet<Map<String,String>> ();
		 Map<String,String> map=new HashMap<String,String>();
		 String spotlightResponse = null;
		 try {
			 GetMethod getMethod = new GetMethod(API_URL + "rest/annotate/?" +
		 "confidence=" + CONFIDENCE
		 + "&support=" + SUPPORT
		 + "&text=" + URLEncoder.encode(text.text(), "utf-8"));
			 getMethod.addRequestHeader(new Header("Accept", "application/json"));
			 spotlightResponse = request(getMethod);
		 } catch (UnsupportedEncodingException e) {
			
		 }
		 assert spotlightResponse != null;
		 JSONObject resultJSON = null;
		 JSONArray entities = null;
		 try {
			 resultJSON = new JSONObject(spotlightResponse);
			 entities = resultJSON.getJSONArray("Resources");
		} catch (JSONException e) {
			
		}
		 LinkedList<DBpediaResource> resources = new LinkedList<DBpediaResource>();
		 String word="";
		 double cut_off_score;
		 cut_off_score=0.1;
		 float similarity_score;
		 for(int i = 0; i < entities.length(); i++) 
		 {	 
			 try 
			 {
				 map=new HashMap<String,String>();
				 JSONObject entity = entities.getJSONObject(i);
				 //System.out.println(entity.toString());
				 similarity_score=Float.valueOf(entity.get("@similarityScore").toString());				 
				 if(similarity_score<cut_off_score)
				 {
					 continue;
				 }
				 word=entity.getString("@surfaceForm").toLowerCase();
				 map.put("surfaceForm", word);
				 try {
					map.put("URI", java.net.URLDecoder.decode(entity.getString("@URI"), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
				 map.put("offset",entity.getString("@offset"));
				 try {
					map.put("concept", new File(java.net.URLDecoder.decode(entity.getString("@URI"), "UTF-8")).getName());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 map.put("similarityScore",entity.getString("@similarityScore"));
				 map.put("types", entity.getString("@types").replaceAll("Freebase:", ""));
				 
				 //System.out.println(map.toString());
				 if(!results.containsKey(word))
				 {
					 results.put(word, map);
				 }
				 // results.put(entity.getString("@surfaceForm"), map);
				 //resources.add(new DBpediaResource(entity.getString("@URI"),Integer.parseInt(entity.getString("@support"))));
			 }
			 catch (JSONException e) {
				 
			 }
		 }
		 
		 Set<String> keys = results.keySet();
		 for(String key:keys)
		 {
			 //printing entities
			 //System.out.println(key+"\t"+results.get(key).toString());
			 
		 }
		 //	System.out.println(results.size());
		 //System.out.println(results.toString());
		 //return resources;
		 return results;
	 }
	 
	 public static void save_extracted_entities (String input,Map<String,Integer> word_freq) throws Exception 
	 {
		 /*
		  * given set of most repetitive words , get the concepts from them .Also relate a score value.
		  */
		 input=input.replaceAll("\n","");//replace all new lines if any	
		 
		 Map<String,Map<String,String>> results;	
		 Map<String,Map<String,String>> related_entities=new HashMap<String,Map<String,String>>();
		 Map<String,String> entities_info=new HashMap<String,String>();
		 //	gets enities from dbpedia for given article
		 //results= extract(new Text(input));		
		 //TODO set max 5 attempts
		 int attempts=0;
		 do
		 {			 
			 results=extract(new Text(input));
			 Thread.sleep(1000);
			 attempts++;
			 System.out.println("DbPedia sets contains totally :"+results.size()+" entities");
		 }while(results.size()==0 && attempts<3 );
		 //	gets entites from Dbpeia for only selected word from word_freq
		 //results= extract(new Text(word_freq.keySet().toString()));
		 Set<String> keyset_results = results.keySet();
		 System.out.println(keyset_results);
		 System.out.println("#############################################################");
		 System.out.println("Word freq based method contain :"+word_freq.size()+" entities ");
		 Set<String> word_freq_keys = word_freq.keySet();
		 System.out.println(word_freq_keys);
		 for(String keyset:keyset_results)
		 {
			 /*
			  * if keyset is there in the word_freq map ,. .add it !
			  */
			 keyset=keyset.toLowerCase();
			 
			 for(String word_freq_key:word_freq_keys)
			 {
				 if(keyset.contains(word_freq_key)||word_freq_key.contains(keyset))
				 {
					 entities_info=new HashMap<String,String>();
					 entities_info=results.get(keyset);
					 entities_info.put("word_count", word_freq.get(word_freq_key).toString());
					 //present add it !!
					 related_entities.put(keyset, entities_info);
					 //System.out.println(keyset+" "+word_freq.get(word_freq_key)+" " +results.get(keyset).get("URI"));
					 break;
				 }
			 }
		 }		 
		 Set<String> key_words = related_entities.keySet();		 
//		 file_operations.write_to_file(results.toString(), "dbpedia_entities.txt");
		 System.out.println("#############################################################");
		 System.out.println("Related entities contains totally : "+related_entities.size()+ " objects");
		 System.out.println(key_words);
		 String concept;
		 ArrayList<String> final_entities=new ArrayList<String>();
		 MultiMap map_topics=new MultiValueMap();
		 Set<String> concepts=new HashSet<String>();
		 for(String word:key_words)
		 {
			 concept=related_entities.get(word).get("concept");
			 concepts.add(concept.replaceAll("_", " "));
			 final_entities.add(concept+","+word);
			 //System.out.println(word+"\t"+related_entities.get(word).toString());
			 //for each entity get related topics.		
		 }		 
		 System.out.println("#############################################################");
		/* for(String x:final_entities)
			 System.out.println(x);
		*/
		 System.out.println("Following :"+concepts.size()+" topics are spoken about :");
		 file_operations.write_to_file(concepts.toString(), "extracted-topics-output.txt");
		 System.out.println(concepts.toString());

	 }
	 
	 public static void main(String[] args) throws Exception {
		 		 
		 String input_file ="article_input.txt";
		 String input_text=file_operations.read_from_file(input_file);
		 Map<String,Integer> word_frequency=frequency_based.stanford_based_freq(input_text);
		 
		 //	remove all quotes & special char before passing to Dbpedia
		 input_text=input_text.replaceAll("[^A-Za-z0-9., -]", "");
		
		 //run Dbpedia Entity extractor on obtained text !!		 
		 save_extracted_entities(input_text,word_frequency);
		 
	 }
}
