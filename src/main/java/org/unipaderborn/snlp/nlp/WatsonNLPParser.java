package org.unipaderborn.snlp.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unipaderborn.snlp.models.SentenceRelationKeyWordsObject;
import org.unipaderborn.snlp.models.SentenceRelationObject;
import org.unipaderborn.snlp.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.RelationArgument;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SemanticRolesOptions;

public class WatsonNLPParser {
	
	static NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
			NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
			"61eba8c7-e990-42b0-8595-07b371212a23",
			 "WgmA2ClMpiDm"
			);

		
	public SentenceRelationKeyWordsObject getRelationsKeywords(String inputstatement) {

		System.out.println("hello");
		
		SentenceRelationKeyWordsObject relationsKeywords = new SentenceRelationKeyWordsObject();
		
		SentenceRelationObject extracttedRelations = new SentenceRelationObject();
		List<String> extractedKeywords = new ArrayList<String>();
		
		SemanticRolesOptions semanticRoles = new SemanticRolesOptions.Builder().build();
		KeywordsOptions keywords = new KeywordsOptions.Builder().build();
		
		Features features = new Features.Builder().semanticRoles(semanticRoles).keywords(keywords).build();

		AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(inputstatement).features(features).build();

		AnalysisResults response = null;
		try {
			response = service.analyze(parameters).execute();
		} catch(Exception e) {
			System.out.println("Error occured while calling the watson API" + e.getMessage());
			extracttedRelations = setSentenceRelationObject("error", "error", "error");
			relationsKeywords.setRelationsObject(extracttedRelations);
			return relationsKeywords;
		}
		
		System.out.println("hello");
		System.out.println(response);

		extracttedRelations = extractSubjectObjectPredicate(response.toString());
		extractedKeywords = extractKeywordsRelevance(response.toString());
		
		relationsKeywords.setRelationsObject(extracttedRelations);
		relationsKeywords.setKeywords(extractedKeywords);
		
		
		return relationsKeywords;

	}

	private static List<String> extractKeywordsRelevance(String jsonString) {

		List<String> keywordsArr = new ArrayList<String>();
		JsonParser parser = new JsonParser();
		JsonObject rootObj = parser.parse(jsonString).getAsJsonObject();

		JsonArray keyArrayJson = rootObj.get("keywords").getAsJsonArray();

		if (keyArrayJson == null || keyArrayJson.size() == 0) {
			System.out.println("No Keywords returned from the WatsonAPI");
			return keywordsArr;
		}
		

		for (JsonElement keywords : keyArrayJson) {

			JsonObject keywordsObJ = keywords.getAsJsonObject();
			String keyword = keywordsObJ.get("text").getAsString();
			double relevance = keywordsObJ.get("relevance").getAsDouble();
			keywordsArr.add(keyword);
			
		}

		System.out.println("Keywords = " + keywordsArr.toString());
		
		return keywordsArr;

	}

	private  SentenceRelationObject setSentenceRelationObject(String subject, String object, String predicate) {
		
		SentenceRelationObject relationsObj = new SentenceRelationObject();
		relationsObj.setSubject(subject);
		relationsObj.setObject(object);
		relationsObj.setPredicate(predicate);
		
		return relationsObj;
		
	}
	
	private SentenceRelationObject extractSubjectObjectPredicate(String jsonString) {

		SentenceRelationObject relationsObj = new SentenceRelationObject();
		JsonParser parser = new JsonParser();
		JsonObject rootObj = parser.parse(jsonString).getAsJsonObject();

		JsonArray semRelArray = rootObj.get("semantic_roles").getAsJsonArray();
		
		if (semRelArray == null || semRelArray.size() == 0) {
			System.out.println("No relations returned from the WatsonAPI");
			relationsObj = setSentenceRelationObject("No result", "No result", "No result");
			return relationsObj;
		}
			

		for (JsonElement pa : semRelArray) {

			JsonObject sematicRolesObj = pa.getAsJsonObject();
			JsonObject subjectObj = sematicRolesObj.get("subject").getAsJsonObject();
			String subject = subjectObj.get("text").getAsString();

			JsonObject predicateObj = sematicRolesObj.get("action").getAsJsonObject();
			String predicate = predicateObj.get("text").getAsString();

			JsonObject objectObj = sematicRolesObj.get("object").getAsJsonObject();
			String object = objectObj.get("text").getAsString();

			System.out.println("Subject = " + subject + " Object = " + object + " Predicate =  " + predicate);
			
			relationsObj = setSentenceRelationObject(subject,object,predicate);
		}
		
		return relationsObj;
	}

}
