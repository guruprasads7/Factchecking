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

		// Reading the Input Fact statements from a TSV file
		IOHandler ioHandler = new IOHandler();
		List<InputFact> inputFacts = ioHandler.readFactsFromCSV("train.tsv");

        //System.exit(1);
		
		String text = "Liverpool is George Bernard Shaw's nascence place";
		
		StringTokenizer tokens = new StringTokenizer(text);
		System.out.println(tokens.countTokens());
		
		System.exit(0);
		
		String inputstatement1="Henry Dunant also known as Henri Dunant, was a Swiss businessman and \n" + 
				"social activist, the founder of the Red Cross, and the first recipient of the Nobel \n" + 
				"Peace Prize. The 1864 Geneva Convention was based on Dunant's ideas. In \n" + 
				"1901 he received the first Nobel Peace Prize together ..., Apr 1, 2005 ... While Henry Dunant was one of two laureates for the first Nobel Peace Prize in \n" + 
				"1901, the ICRC itself has been awarded this honour - in 1917, 1944 and, with the \n" + 
				"International Federation of Red Cross and Red Crescent Societies, in 1963., The Nobel Peace Prize 1901 ... The Geneva household into which Henry Dunant \n" + 
				"was born was religious, humanitarian, and civic-minded. In the first part of his life \n" + 
				"Dunant engaged quite seriously in religious activities and for a while in full-time \n" + 
				"work as a representative of the Young Men's Christian Association, traveling in ..., Jun 4, 1998 ... The man whose vision led to the creation of the worldwide Red Cross and Red \n" + 
				"Crescent movement; he went from riches to rags but became joint recipient of the \n" + 
				"first Nobel peace prize. A Memory of Solferino. Henry Dunant, who was born in \n" + 
				"Geneva on 8 May 1828, came from a devout and charitable ..., The Norwegian Nobel Committee has decided to award the Nobel Peace Prize \n" + 
				"for 2017 to the International Campaign to Abolish Nuclear Weapons (ICAN) \"for \n" + 
				"its work to draw attention to the catastrophic humanitarian ... Henry Dunant, the \n" + 
				"founder of the Red Cross in 1863, was awarded the first Nobel Peace Prize in \n" + 
				"1901., Dec 8, 2017 ... The very first prize, for instance, was awarded jointly to Frederic Passy (an active \n" + 
				"campaigner for peace) and Henry Dunant (one of the co-founders of the \n" + 
				"International Committee of the Red Cross). But since the end of World War II, the \n" + 
				"committee has been sharply criticised for awarding prizes to Henry ..., Oct 30, 2003 ... The International Committee of the Red Cross was awarded the Nobel Peace \n" + 
				"Prize in 1917, 1944 and 1963 – on the third occasion jointly with the League of ... \n" + 
				"It is intriguing then to trace how Henry Dunant came to be awarded the very first \n" + 
				"Peace Prize in 1901 (an honour he shared with Frédéric Passy, ..., The Nobel Peace Prize was first awarded in 1901 to the founder of the Red Cross\n" + 
				", Henry Dunant, and the economist Frédéric Passy. There have been a wide ... \n" + 
				"Mahatma Gandhi was nominated for the Nobel Peace Prize on five occasions (\n" + 
				"1937, 1938, 1939, 1947, 1948), but was never awarded the honour. When he \n" + 
				"was ..., “There is no man who more deserves this honor, for it was you, 40 years ago, \n" + 
				"who set on foot the international organization for the relief of the wounded on the \n" + 
				"battlefield. Without .... Later on, in 1901, he was awarded the very first Nobel \n" + 
				"Peace Prize (with Frederic Passy), becoming Switzerland's first Nobel Laureate. \n" + 
				"Despite ..., 1862, In November an edition of 1'600 books of “A memory of Solferino” is being \n" + 
				"published, paid by Henri Dunant. 1863, 9. ... Aim: Political and social peace, \n" + 
				"international arbitration tribunal; betterment for war prisoners. Dunant ... 1901, 10. \n" + 
				"December: Dunant receives the first Nobel Prize together with Frédéric Passy.";

		getRelationsFromData(text);
		//getRelationsFromData(inputstatement1);
		
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
		
		ArrayList<SearchResults> results1 = webcrawl
				.getResultsCustomGoogleSearch("Barnwell, South Carolina is Quentin Tarantino's nascence place.");
		
		System.out.println(results1);
		System.exit(1);
		int count = 0;
		for (InputFact factstmt : inputFacts) {
			System.out.println("Processing Query : " + factstmt.getFactStatement());
			
			ArrayList<SearchResults> results = webcrawl
					.getResultsCustomGoogleSearch(factstmt.getFactStatement());
			List<String> resBody = new ArrayList<String>();
			for (SearchResults res : results) {
				resBody.add(res.getBody());
			}
			queryAndResultsFromWeb.put(factstmt.getFactStatement(),resBody);
			count++;
			if (count > 101) {
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
				PrintWriter outfile = new PrintWriter(new FileWriter("query-webresults.tsv"), true);
				
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
				System.out.println(word + "\t" + pos + "\t" + ne);
			}

			// Get the OpenIE triples for the sentence
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			// Print the triples

			for (RelationTriple triple : triples) {
				System.out.println(triple.confidence + "\t" + triple.subjectLemmaGloss() + "\t"
						+ triple.relationLemmaGloss() + "\t" + triple.objectLemmaGloss());
			}

		}
	}

}
