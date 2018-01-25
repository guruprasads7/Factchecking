package org.unipaderborn.snlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.unipaderborn.snlp.models.InputFact;
import org.unipaderborn.snlp.models.SearchResults;
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

		String text = "Nobel Prize in Literature is Albert Einstein's honour";

		// Extracts the Subject-predicate-Object and Important Keywords for a fact
		// TODO: Return a keywords from the WatsonNLPParser object
		WatsonNLPParser watsonparser = new WatsonNLPParser();
		SentenceRelationObject relations = watsonparser.getRelationsKeywords(text);
		System.out.println(relations.toString());

		System.exit(1);

		// System.out.println(inputFacts.size());
		int count = 0;
		for (InputFact factstmt : inputFacts) {
			System.out.println("Processing Query : " + factstmt.getFactStatement());
			getRelationsFromData(factstmt.getFactStatement());
			count++;
			if (count > 10) {
				break;
			}
		}

		System.exit(1);

		// getRelationsFromData(inputFacts);

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

		ExtractDataFromWeb webcrawl = new ExtractDataFromWeb();
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

	public static void getRelationsFromData(String inputstatement) throws Exception {
		// Create the Stanford CoreNLP pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
		// props.setProperty("annotators",
		// "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");
		// StanfordCoreNLPClient pipeline = new StanfordCoreNLPClient(props,
		// "http://139.18.2.39", 9000, 1);
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

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
