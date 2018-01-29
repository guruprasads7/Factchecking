package org.unipaderborn.snlp.web;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.PrintUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.unipaderborn.snlp.models.SearchResults;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ExtractDataFromWeb {

	//We need a real browser user agent or Google will block our request with a 403 - Forbidden
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
	
	private static void testApacheJena(){
        Model model = ModelFactory.createDefaultModel();
        model.read("src/main/resources/turtleTest.ttl");

        // list the statements in the Model
        StmtIterator iter = model.listStatements();

        // print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();  // get next statement
            Resource  subject   = stmt.getSubject();     // get the subject
            Property  predicate = stmt.getPredicate();   // get the predicate
            RDFNode   object    = stmt.getObject();      // get the object

            PrintUtil pUtil = new PrintUtil();
            pUtil.removePrefix(subject.toString());

            //System.out.print("The subject is " + PrintUtil.removePrefix(subject.toString()) + "\n");

            //System.out.print("The predicate is " + predicate.toString() + "\n");
            if (object instanceof Resource) {
                //System.out.print("The object is " + object.toString() + "\n");
            } else {
                // object is a literal
                //System.out.print(" \"" + object.toString() + "\"");
            }

            //System.out.println(" .");
        }
    }
    
	private SearchResults setSearchResultsObj(String title,String body, String linkURL){
		
		SearchResults searchResult = new SearchResults();
		
		searchResult.setTitle(title);
		searchResult.setBody(body);
		searchResult.setLinkURL(linkURL);
		
		return searchResult;
	}
	
    public ArrayList<SearchResults> getResultsCustomGoogleSearch (String query) throws IOException {

    	JsonParser parser = new JsonParser();
    	
    	ArrayList<SearchResults> customsearchResults= new ArrayList<SearchResults>(); 
    	
    	String URIString = "https://www.googleapis.com/customsearch/v1?key=";
    	String key="AIzaSyDkT7Ztmip-tgxAwYSkr9zmQO1i5ZJsp4o";
    	String customSearchEngineID = "009393578226351920957:t1ghgtufjhi";
    	String fields = "queries,items(title,link,snippet)";
    	URLEncoder.encode(query, "UTF-8");
    	//String extactTerms = "&exactTerms=albert+einstein+born+in+ulm";
    	
        URL url = new URL(URIString + key + "&cx=" + customSearchEngineID + "&q="+ URLEncoder.encode(query, "UTF-8") + "+-filetype%3Apdf" + "&fields=" + fields);
        
        //GET https://www.googleapis.com/customsearch/v1?key=INSERT_YOUR_API_KEY&cx=017576662512468239146:omuauf_lfve&q=lectures

        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        
        StringBuilder sb = new StringBuilder("");
        
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        
        String searchResult = sb.toString();
        searchResult = Normalizer.normalize(searchResult, Normalizer.Form.NFD);
        String normalizedString = searchResult.replaceAll("[^\\x00-\\x7F\"\']", "").replaceAll("\\\\n", "").replaceAll("\\\\r", "");
        
        //System.out.println(normalizedString);
        
        
        if (sb == null || sb.toString().equals("")) {
        	
        	SearchResults res = setSearchResultsObj("No result", "No result", "No result");
         	customsearchResults.add(res);
        	conn.disconnect();
        	return customsearchResults;
        }
        
        JsonObject rootObj = parser.parse(normalizedString).getAsJsonObject();
       
        // Checks in retreived json result, the count of the results returned,
        /*
        "queries": {
        	  "request": [
        	   {
        	    "title": "Google Custom Search - hello",
        	    "totalResults": "104000000",
        	    "searchTerms": "hello",
        	    "count": 10,
        	    "startIndex": 1,
        	    "inputEncoding": "utf8",
        	    "outputEncoding": "utf8",
        	    "safe": "off",
        	    "cx": "009393578226351920957:lyufjpspmwe"
        	   }
        */
        JsonObject queriesJson = rootObj.getAsJsonObject("queries");
        JsonArray request = queriesJson.getAsJsonArray("request");
        JsonObject requestJson = (JsonObject) request.get(0);
        int totalResults = requestJson.get("totalResults").getAsInt();
        //System.out.println("Total Results = " + totalResults);
        
        // Fetch the details from the webpages only if the results returned are greater than 0
        if(totalResults  > 0) {
        	
        	JsonArray resArray = rootObj.getAsJsonArray("items");
            for (JsonElement pa : resArray) {
            	
            	SearchResults res = new SearchResults();
            	
                JsonObject paymentObj = pa.getAsJsonObject();
                String     title     = paymentObj.get("title").getAsString();
                String     link 	 = paymentObj.get("link").getAsString();
                String 	snippet      = paymentObj.get("snippet").getAsString();
                
                res = setSearchResultsObj(title, snippet, link);
                customsearchResults.add(res);
                
            }
        	
        }  else {
        	//System.out.println("No results returned from google search");
        	SearchResults res = setSearchResultsObj("No result", "No result", "No result");
        	customsearchResults.add(res);
        }
        
        conn.disconnect();
        return customsearchResults;
        
    }
    
    public static void scrapeTextFromURL(HashMap<String, String> topresults) {
    	
		topresults.forEach((k, v) -> {
			try {
				
				System.out.println("Extracting webpage" + v);
				Document doc = Jsoup.connect(v)
				                    .get();

				// select title of the webpage
				String title = doc.title();
				System.out.println(title);

				// select body of the webpage
				String body = doc.getElementsByTag("body")
				                 .text();
				System.out.println(body);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
   	
    	
    }
    
    
    public static HashMap<String, String> getTopResults(String query) throws IOException{
    	
    	HashMap<String, String> results = new HashMap<String,String>();
    	
    	//Fetch the page
        final Document doc = Jsoup.connect("https://google.com/search?q=" + query + "+\"").userAgent(USER_AGENT).get();
        //-filetype%3Apdf
        //Traverse the results
        for (Element result : doc.select("h3.r a")){

            final String title = result.text();
            final String url = result.attr("href");
            
            results.put(title,url);

            //System.out.println(title + " -> " + url);
        }
        
        return results;
    	
    }

}
