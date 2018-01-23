package org.unipaderborn.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.StanfordCoreNLPClient;
import edu.stanford.nlp.util.CoreMap;

public class FactChecking {

	public static void main(String[] args) throws Exception {

		System.setProperty("javax.xml.bind.JAXBContextFactory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
		System.out.println("Enter your String to Search: ");

		IOHandler ioHandler = new IOHandler();
		
		List<InputFact> inputFacts = ioHandler.readFactsFromCSV("train.tsv"); 
		
		System.out.println(inputFacts.size());
		
		//ioHandler.writeOutput(inputFacts);
		
		// let's print all the person read from CSV file 
		/*
		for (InputFact b : inputFacts) {
			System.out.println(b);
			}
		*/
		
		
		// Scanner scanner = new Scanner(System.in);
		// String querystring = scanner.nextLine().replace(" ", "+");
		// String querystring

		// System.out.println("Your queryString is " + querystring);
		// scanner.close();

		/*
		 * ExtractDataFromWeb webcrawl = new ExtractDataFromWeb(); HashMap<String,
		 * String> topresult = new HashMap<String, String>();
		 * 
		 * topresult = webcrawl.getTopResults(querystring);
		 * //System.out.println(topresult.toString());
		 * 
		 * ArrayList<SearchResults> extractFromSearchEngine =
		 * webcrawl.getResultsCustomGoogleSearch(querystring);
		 * 
		 * System.out.println(extractFromSearchEngine);
		 * 
		 * //Get page rank of a website //GetPageRank obj = new GetPageRank();
		 * 
		 * BagOfWordsApproach fc = new BagOfWordsApproach(); String[] factToAssess = new
		 * String[3]; factToAssess[0] = "Albert Einstein"; factToAssess[1] = "born";
		 * factToAssess[2] = "Ulm";
		 * 
		 * //testApacheJena();
		 * 
		 * for(SearchResults res : extractFromSearchEngine){ boolean isStatementTrue =
		 * fc.BagOfWordsApproach(factToAssess, res.getBody() + res.getTitle() , 1, 1,
		 * 1);
		 * 
		 * if (isStatementTrue){ System.out.println("Document " + res.getTitle() +
		 * "Contains input string"); } }
		 */

		// getRelationsFromData();

	}

	public static void getRelationsFromData() throws Exception {
		// Create the Stanford CoreNLP pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,ner,natlog,mention,coref");
		// props.setProperty("annotators",
		// "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");
		StanfordCoreNLPClient pipeline = new StanfordCoreNLPClient(props, "localhost", 9000, 2);
		// StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		System.out.println("hello");
		// Annotate an example document.
		Annotation doc = new Annotation("Hong Kong is Team and Concepts' innovation place");
		pipeline.annotate(doc);

		// CorefChain
		for (CorefChain cc : doc.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
			System.out.println("\t" + cc);
		}

		for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
			System.out.println("---");
			System.out.println("mentions");
			for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
				System.out.println("\t" + m);
			}
		}

		// Loop over sentences in the document
		for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
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
