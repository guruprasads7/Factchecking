package org.unipaderborn.snlp.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.unipaderborn.snlp.models.SearchResults;
import org.unipaderborn.snlp.models.SentenceRelationKeyWordsObject;
import org.unipaderborn.snlp.models.SentenceRelationObject;
import org.unipaderborn.snlp.nlp.WordnetParser;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.StringUtils;
import info.debatty.java.stringsimilarity.*;


public class FactScorer {

	public double factvalidator(String factStatement,SentenceRelationKeyWordsObject relationKeywords, ArrayList<SearchResults> extractFromSearchEngine) throws IOException, Exception {
		double factscore = 0.0;
		
		List<String> keywordsWithSynonyms = WordnetParser.synonymExtractor(relationKeywords.getKeywords());
		
		for (SearchResults res : extractFromSearchEngine) {
			boolean isStatementTrue = BagOfWordsApproach(relationKeywords.getRelationsObject(), res.getBody() + res.getTitle(), 1, 1, 1);

			if (isStatementTrue) {
				System.out.println("Document " + res.getTitle() + "Contains input string");
			}
		}
		
			
		return factscore;
	}
	
    public boolean BagOfWordsApproach(SentenceRelationObject relations, String corpus,
                          int minSubjectAppearances, int minPredicateAppearances, int minObjectAppearances){
        String[] subject = lemmasOf(relations.getSubject());
        String[] predicate = lemmasOf(relations.getPredicate());
        String[] object = lemmasOf(relations.getObject());

        int subjectCount = 0;
        int predicateCount = 0;
        int objectCount = 0;

        Document doc = new Document(corpus);
        for(Sentence s : doc.sentences()){
            List<String> sentence = s.lemmas();
            for(int i=0; i<sentence.size(); i++){

                int count = 0;
                if(0 == subject[0].compareToIgnoreCase(sentence.get(i))){
                    if((i < sentence.size() - subject.length + 1)){
                        while(count < subject.length){
                            if(0 == subject[count].compareToIgnoreCase(sentence.get(i + count)))
                                count++;
                            else
                                break;
                        }
                        if(count == subject.length)
                            subjectCount++;
                    }
                }

                count = 0;
                if(0 == predicate[0].compareToIgnoreCase(sentence.get(i))){
                    if((i < sentence.size() - predicate.length + 1)){
                        while(count < predicate.length){
                            if(0 == predicate[count].compareToIgnoreCase(sentence.get(i + count)))
                                count++;
                            else
                                break;
                        }
                        if(count == predicate.length)
                            predicateCount++;
                    }
                }

                count = 0;
                if(0 == object[0].compareToIgnoreCase(sentence.get(i))){
                    if((i < sentence.size() - object.length + 1)){
                        while(count < object.length){
                            if(0 == object[count].compareToIgnoreCase(sentence.get(i + count)))
                                count++;
                            else
                                break;
                        }
                        if(count == predicate.length)
                            objectCount++;
                    }
                }

            }
        }

        return (subjectCount >= minSubjectAppearances &&
                objectCount >= minObjectAppearances && predicateCount >= minPredicateAppearances);
    }

    private static String[] lemmasOf(String text){
        int length = text.split(" ").length;
        String[] output = new String[length];
        Document doc = new Document(text);
        int count = 0;
        for(Sentence sent : doc.sentences()){
            for(String lemma : sent.lemmas()){
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
        
        Map<String,Integer> profile1 = cos.getProfile(inputStatement);
        Map<String,Integer> profile2 = cos.getProfile(stmt2);
        
        System.out.println("Cosine Similarity = " + cos.similarity(profile1,profile2));
        System.out.println("Jaccard Similarity = " + jacc.similarity(inputStatement,stmt2));
    	
    }
    
    public void findKeywordsSimilarity() {
    	String text = "I will come and meet you at the woods 123woods and all the woods";

    	List<String> tokens = new ArrayList<String>();
    	tokens.add("12");
    	tokens.add("asdas");

    	String patternString = "\\b(" + StringUtils.join(tokens, "|") + ")\\b";
    	Pattern pattern = Pattern.compile(patternString);
    	Matcher matcher = pattern.matcher(text);

    	System.out.println(matcher.find());
    	while (matcher.find()) {
    	    System.out.println(matcher.group(1));
    	}
    }
    
    
}
