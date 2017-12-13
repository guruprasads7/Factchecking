package org.unipaderborn.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.xml.bind.SchemaOutputResolver;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.PrintUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.unipaderborn.nlp.SearchResults;

public class FactChecking {

    //We need a real browser user agent or Google will block our request with a 403 - Forbidden
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    public static void main(String[] args) throws Exception {
    	
    	System.out.println("Enter your String to Search: ");
    	Scanner scanner = new Scanner(System.in);
    	String querystring = scanner.nextLine().replace(" ", "+");
    	//String querystring

    	System.out.println("Your queryString is " + querystring);
    	scanner.close();
    	
    	
    	HashMap<String, String> topresult = new HashMap<String, String>();
    	topresult = getTopResults(querystring);
    	//System.out.println(topresult.toString());
    	
    	ArrayList<SearchResults> extractFromSearchEngine = getResultsCustomGoogleSearch(querystring);
    	
    	System.out.println(extractFromSearchEngine);
    	
    	//Get page rank of a website
    	//GetPageRank obj = new GetPageRank();
    	
        BagOfWordsApproach fc = new BagOfWordsApproach();
        String[] factToAssess = new String[3];
        factToAssess[0] = "Albert Einstein";
        factToAssess[1] = "born";
        factToAssess[2] = "Ulm";

        testApacheJena();

        for(SearchResults res : extractFromSearchEngine){
        	boolean isStatementTrue = fc.BagOfWordsApproach(factToAssess, res.getBody() + res.getTitle() , 1, 1, 1);
        	
        	if (isStatementTrue){
        		System.out.println("Document " + res.getTitle() + "Contains input string");
        	}
        }
    }

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

            System.out.print("The predicate is " + predicate.toString() + "\n");
            if (object instanceof Resource) {
                System.out.print("The object is " + object.toString() + "\n");
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }

            System.out.println(" .");
        }
    }
    
    private static ArrayList<SearchResults> getResultsCustomGoogleSearch (String query) throws IOException {

    	JsonParser parser = new JsonParser();
    	
    	ArrayList<SearchResults> customsearchResults= new ArrayList<SearchResults>(); 
    	
    	String URIString = "https://www.googleapis.com/customsearch/v1?key=";
    	String key="AIzaSyBD_ca3syF7pZruUK3RUWrxmYNql1BhBAI";
    	String customSearchEngineID = "009393578226351920957:lyufjpspmwe";
    	String fields = "kind,items(title,link,snippet)";
    	
    	String extactTerms = "&exactTerms=albert+einstein+born+in+ulm";
    	
        URL url = new URL(
                URIString + key + "&cx=" + customSearchEngineID + "&q="+ query + "+-filetype%3Apdf" + "&fields=" + fields );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));
        
        StringBuilder sb = new StringBuilder();
        
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        
        JsonObject rootObj = parser.parse(sb.toString()).getAsJsonObject();       
        System.out.println(rootObj.get("kind").getAsString());
        JsonArray resArray = rootObj.getAsJsonArray("items");
        
        for (JsonElement pa : resArray) {
        	
        	SearchResults res = new SearchResults();
        	
            JsonObject paymentObj = pa.getAsJsonObject();
            String     title     = paymentObj.get("title").getAsString();
            String     link 	 = paymentObj.get("link").getAsString();
            String 	snippet      = paymentObj.get("snippet").getAsString();
            res.setTitle(title);
            res.setBody(snippet);
            res.setLinkURL(link);
            
            customsearchResults.add(res);
            
        }
        conn.disconnect();
        return customsearchResults;
        
    }
    
    private static void scrapeTextFromURL(HashMap<String, String> topresults) {
    	
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
    
    
    private static HashMap<String, String> getTopResults(String query) throws IOException{
    	
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
