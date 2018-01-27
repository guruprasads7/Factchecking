package org.unipaderborn.snlp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

import org.unipaderborn.snlp.models.InputFact;
import org.unipaderborn.snlp.models.SearchResults;
import org.unipaderborn.snlp.models.SentenceRelationKeyWordsObject;
import org.unipaderborn.snlp.models.SentenceRelationObject;
import org.unipaderborn.snlp.nlp.WatsonNLPParser;
import org.unipaderborn.snlp.nlp.WordnetParser;
import org.unipaderborn.snlp.search.FactAssesmentMethods;
import org.unipaderborn.snlp.web.ExtractDataFromWeb;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SemanticRolesOptions;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.StanfordCoreNLPClient;
import edu.stanford.nlp.util.CoreMap;

public class FactChecking {

	public static void main(String[] args) throws Exception {

		System.setProperty("javax.xml.bind.JAXBContextFactory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
		System.out.println("Enter your String to Search: ");

		// Check for wordnet synonyms
		WordnetParser wordnetParser = new WordnetParser();
		wordnetParser.synonymExtractor();
		
		System.exit(1);
		
		// Reading the Input Fact statements from a TSV file
		IOHandler ioHandler = new IOHandler();
		List<InputFact> inputFacts = ioHandler.readFactsFromCSV("train.tsv");
        //System.exit(1);
		
		
		StringTokenizer tokens = new StringTokenizer(text);
		System.out.println(tokens.countTokens());
		
		

		//getCorefrenceFromData(inputstatement1);
		getRelationsFromData(text);
		getRelationsFromData(inputstatement1);
		
		System.exit(1);
		
		/*
		String test = "2012 (film) stars Amanda Peet.";
		
		// Extracts the Subject-predicate-Object and Important Keywords for a fact
		// TODO: Return a keywords from the WatsonNLPParser object
		WatsonNLPParser watsonparser = new WatsonNLPParser();
		SentenceRelationKeyWordsObject relations = watsonparser.getRelationsKeywords(test);
		System.out.println(relations.toString());

		System.exit(1);
		*/
		
		// Query Web using google custom search API
		ExtractDataFromWeb webcrawl = new ExtractDataFromWeb();
		HashMap<String, List<String>> queryAndResultsFromWeb = new HashMap<String, List<String>>();
		
		/*
		// Test for single query from google
		ArrayList<SearchResults> results1 = webcrawl
				.getResultsCustomGoogleSearch("Barnwell, South Carolina is Quentin Tarantino's nascence place.");
		
		System.out.println(results1);
		
		System.exit(1);
		*/
		
		int count = 0;
		for (InputFact factstmt : inputFacts) {
			System.out.println("Processing Query : " + factstmt.getFactStatement());
			
			ArrayList<SearchResults> results = webcrawl
					.getResultsCustomGoogleSearch(factstmt.getFactStatement());
			
			List<String> resBody = new ArrayList<String>();
			for (SearchResults res : results) {
				resBody.add(res.getTitle() + "|" + res.getBody());
			}
			queryAndResultsFromWeb.put(factstmt.getFactStatement(),resBody);
			count++;
			if (count > 4) {
				break;
			}
		}
		
		printMap(queryAndResultsFromWeb);
		
		
		System.exit(1);

		

		// ioHandler.writeOutput(inputFacts);

		// let's print all the person read from CSV file
		/*
		 * for (InputFact b : inputFacts) { System.out.println(b); }
		 */

		// Scanner scanner = new Scanner(System.in);
		// String querystring = scanner.nextLine().replace(" ", "+");
		// String querystring

		// System.out.println("Your queryString is " + querystring);
		// scanner.close();


		
		HashMap<String, String> topresult = new HashMap<String, String>();

		// topresult = webcrawl.getTopResults(querystring);
		// System.out.println(topresult.toString());

		ArrayList<SearchResults> extractFromSearchEngine = webcrawl
				.getResultsCustomGoogleSearch("Albert Einstein Born in Ulm");

		System.out.println(extractFromSearchEngine);

		// System.exit(1);

		// Get page rank of a website //GetPageRank obj = new GetPageRank();

		FactAssesmentMethods fc = new FactAssesmentMethods();
		String[] factToAssess = new String[3];
		factToAssess[0] = "Albert Einstein";
		factToAssess[1] = "born";
		factToAssess[2] = "Ulm";

		// testApacheJena();

		for (SearchResults res : extractFromSearchEngine) {
			boolean isStatementTrue = fc.BagOfWordsApproach(factToAssess, res.getBody() + res.getTitle(), 1, 1, 1);

			if (isStatementTrue) {
				System.out.println("Document " + res.getTitle() + "Contains input string");
			}
		}

		// getRelationsFromData();

	}
	
		
		public static void printMap(HashMap<String, List<String>> mp) {
			
			try {
				PrintWriter outfile = new PrintWriter(new FileWriter("query-webresults-new.txt"), true);
				
				Iterator<Entry<String, List<String>>> it = mp.entrySet().iterator();
			    while (it.hasNext()) {
			    	
			        Map.Entry<String, List<String>> pair = (Map.Entry<String, List<String>>)it.next();
			        System.out.println(pair.getKey() + " = " + pair.getValue());
			        
			        outfile.println(pair.getKey() + "\t" + pair.getValue());
			        
			        it.remove(); // avoids a ConcurrentModificationException
			    }
			    outfile.flush();
			    outfile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		    
		}

	public static void getCorefrenceFromData(String inputstatement) throws Exception{
		
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");
		
		Annotation document = new Annotation(inputstatement);

		StanfordCoreNLPClient pipeline = new StanfordCoreNLPClient(props,"http://139.18.2.39", 9000, 2);
	    pipeline.annotate(document);
	    System.out.println("---");
	    System.out.println("coref chains");
	    for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
	      System.out.println("\t" + cc);
	    }
	    for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
	      System.out.println("---");
	      System.out.println("mentions");
	      for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
	        System.out.println("\t" + m);
	       }
	    }
	  }
		
	

	public static void getRelationsFromData(String inputstatement) throws Exception {
		// Create the Stanford CoreNLP pipeline
		

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
		// props.setProperty("annotators",
		// "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");
		StanfordCoreNLPClient pipeline = new StanfordCoreNLPClient(props,"http://139.18.2.39", 9000, 1);
		//StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// Annotate an example document.
		Annotation doc = new Annotation(inputstatement);
		pipeline.annotate(doc);

		List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);

		// Loop over sentences in the document
		for (CoreMap sentence : sentences) {

			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				// this is the NER label of the token
				String ne = token.get(NamedEntityTagAnnotation.class);
				//System.out.println(word + "\t" + pos + "\t" + ne);
			}

			// Get the OpenIE triples for the sentence
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			// Print the triples

			for (RelationTriple triple : triples) {
				System.out.println(triple.confidence + "\t" + triple.subjectLemmaGloss() + "\t"
						+ triple.relationLemmaGloss() + "\t" + triple.objectLemmaGloss());
				//triple.subject.forEach(subject -> System.out.print("Subject = " + subject.get(TextAnnotation.class) + " "));
				//triple.object.forEach(object -> System.out.print("Object = " + object.get(TextAnnotation.class) + " "));
				System.out.println();
			}

		}
	}

}
