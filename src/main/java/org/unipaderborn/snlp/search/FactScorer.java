package org.unipaderborn.snlp.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.unipaderborn.snlp.models.SearchResults;
import org.unipaderborn.snlp.models.SentenceRelationKeyWordsObject;
import org.unipaderborn.snlp.models.SentenceRelationObject;
import org.unipaderborn.snlp.nlp.StanfordNLPParser;
import org.unipaderborn.snlp.nlp.WordnetParser;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.StringUtils;
import info.debatty.java.stringsimilarity.*;

public class FactScorer {

	public double factvalidator(String factStatement, SentenceRelationKeyWordsObject relationKeywords,
			ArrayList<SearchResults> extractFromSearchEngine) throws IOException, Exception {

		double factscore = 0.0;
		double keywordsMatchSimilarityScorer = 0.0;

		/*
		 * for (SearchResults res : extractFromSearchEngine) { boolean isStatementTrue =
		 * BagOfWordsApproach(relationKeywords.getRelationsObject(), res.getBody() +
		 * res.getTitle(), 1, 1, 1);
		 * 
		 * if (isStatementTrue) { System.out.println("Document " + res.getTitle() +
		 * "Contains input string"); } }
		 */

		List<String> keywordsWithSynonyms = WordnetParser.synonymExtractor(relationKeywords.getKeywords());

		// Lower casing the list as the list as the String it will be compared to is
		// already lower cased
		List<String> keywordsWithSynonymsLowercased = keywordsWithSynonyms.stream().map(String::toLowerCase)
				.collect(Collectors.toList());

		//keywordsMatchSimilarityScorer = keywordsBasedSimilarity(keywordsWithSynonymsLowercased,extractFromSearchEngine);

		double keywordsBasedValidator = keywordsBasedValidator(keywordsWithSynonymsLowercased,
				extractFromSearchEngine);

		factscore = keywordsBasedValidator;

		return factscore;
	}

	public double keywordsBasedValidator(List<String> keywords, ArrayList<SearchResults> extractFromSearchEngine)
			throws IOException, Exception {

		double finalKeyWordValidator = 0.0;
		// Keyword match scores
		// The match is performed across title and body of the returned result
		// If a match is found in the title it is given twice the weightage compared to
		// the weightage in the body
		// Calculating the match score for title

		StanfordNLPParser stanParser = new StanfordNLPParser();

		if (null == keywords || keywords.isEmpty()) {
			System.out.println("There are no keywords in the list, hence keyword Match Result is 0");
			return finalKeyWordValidator;
		}

		for (SearchResults res : extractFromSearchEngine) {
			double titleMatch = keywordsBasedValidator(keywords, stanParser.sentencePreprocess(res.getTitle()));
			double bodyMatch = keywordsBasedValidator(keywords, stanParser.sentencePreprocess(res.getBody()));

			double keywordMatchScore = titleMatch + bodyMatch;
			finalKeyWordValidator += keywordMatchScore;

		}

		return finalKeyWordValidator;

	}

	public double keywordsBasedSimilarity(List<String> keywords, ArrayList<SearchResults> extractFromSearchEngine)
			throws IOException, Exception {

		double finalKeywordMatchScore = 0.0;
		// Keyword match scores
		// The match is performed across title and body of the returned result
		// If a match is found in the title it is given twice the weightage compared to
		// the weightage in the body
		// Calculating the match score for title

		StanfordNLPParser stanParser = new StanfordNLPParser();

		for (SearchResults res : extractFromSearchEngine) {
			double titleMatch = findKeywordsSimilarity(keywords, stanParser.sentencePreprocess(res.getTitle()));
			double bodyMatch = findKeywordsSimilarity(keywords, stanParser.sentencePreprocess(res.getBody()));

			double keywordMatchScore = (3 * titleMatch) + bodyMatch;
			finalKeywordMatchScore += keywordMatchScore;

		}

		// Normalizing the result across result set
		finalKeywordMatchScore = finalKeywordMatchScore / extractFromSearchEngine.size();

		return finalKeywordMatchScore;

	}

	public boolean BagOfWordsApproach(SentenceRelationObject relations, String corpus, int minSubjectAppearances,
			int minPredicateAppearances, int minObjectAppearances) throws IOException {

		Stopwords stpwords = new Stopwords();

		String[] subject = lemmasOf(stpwords.removeStopwords(relations.getSubject()));
		String[] predicate = lemmasOf(stpwords.removeStopwords(relations.getPredicate()));
		String[] object = lemmasOf(stpwords.removeStopwords(relations.getObject()));

		int subjectCount = 0;
		int predicateCount = 0;
		int objectCount = 0;

		String preprocessedCorpus = stpwords.removeStopwords(corpus);

		Document doc = new Document(preprocessedCorpus);
		for (Sentence s : doc.sentences()) {
			List<String> sentence = s.lemmas();
			for (int i = 0; i < sentence.size(); i++) {

				int count = 0;
				if (0 == subject[0].compareToIgnoreCase(sentence.get(i))) {
					if ((i < sentence.size() - subject.length + 1)) {
						while (count < subject.length) {
							if (0 == subject[count].compareToIgnoreCase(sentence.get(i + count)))
								count++;
							else
								break;
						}
						if (count == subject.length)
							subjectCount++;
					}
				}

				count = 0;
				if (0 == predicate[0].compareToIgnoreCase(sentence.get(i))) {
					if ((i < sentence.size() - predicate.length + 1)) {
						while (count < predicate.length) {
							if (0 == predicate[count].compareToIgnoreCase(sentence.get(i + count)))
								count++;
							else
								break;
						}
						if (count == predicate.length)
							predicateCount++;
					}
				}

				count = 0;
				if (0 == object[0].compareToIgnoreCase(sentence.get(i))) {
					if ((i < sentence.size() - object.length + 1)) {
						while (count < object.length) {
							if (0 == object[count].compareToIgnoreCase(sentence.get(i + count)))
								count++;
							else
								break;
						}
						if (count == predicate.length)
							objectCount++;
					}
				}

			}
		}

		return (subjectCount >= minSubjectAppearances && objectCount >= minObjectAppearances
				&& predicateCount >= minPredicateAppearances);
	}

	private static String[] lemmasOf(String text) {
		int length = text.split(" ").length;
		String[] output = new String[length];
		Document doc = new Document(text);
		int count = 0;
		for (Sentence sent : doc.sentences()) {
			for (String lemma : sent.lemmas()) {
				output[count] = lemma;
				count++;
			}
		}
		return output;
	}

	public void calculateSentenceSimilarity(String inputStatement, String stmt2) {

		String s1 = "My first string";
		String s2 = "My first string";

		Cosine cos = new Cosine();
		Jaccard jacc = new Jaccard(3);

		Map<String, Integer> profile1 = cos.getProfile(inputStatement);
		Map<String, Integer> profile2 = cos.getProfile(stmt2);

		double jaccardSimilarity = jacc.similarity(inputStatement, stmt2);

		System.out.println("Cosine Similarity = " + cos.similarity(profile1, profile2));
		System.out.println("Jaccard Similarity = " + jacc.similarity(inputStatement, stmt2));

	}

	public double keywordsBasedValidator(List<String> keywords, String searchResult) {

		double keywordMatchResult = 0.0;

		int matchCount = 0;
		for (String keyword : keywords) {
			String patternString = "\\b(" + keyword + ")\\b";
			Pattern pattern = Pattern.compile(patternString);
			Matcher matcher = pattern.matcher(searchResult);

			if (matcher.find()) {
				matchCount++;
			}

		}

		if (matchCount == keywords.size()) {
			//System.out.println("Result string = " + searchResult + " contains all the keywords");
			keywordMatchResult = 1.0;
		} else {
			//System.out.println("Its a partial or no match");
			keywordMatchResult = 0.0;
		}

		return keywordMatchResult;

	}

	public double findKeywordsSimilarity(List<String> keywords, String searchResult) {

		double keywordMatchResult = 0.0;


		int matchCount = 0;
		for (String keyword : keywords) {
			String patternString = "\\b(" + keyword + ")\\b";
			Pattern pattern = Pattern.compile(patternString);
			Matcher matcher = pattern.matcher(searchResult);

			if (matcher.find()) {
				matchCount++;
			}

		}

		keywordMatchResult = matchCount / (keywords.size());


		if (matchCount == keywords.size()) {
			System.out.println("Result string = " + searchResult + " contains all the keywords");

		} else {
			System.out.println("Its a partial or no match");
		}

		return keywordMatchResult;

	}

}
