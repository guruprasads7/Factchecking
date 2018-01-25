package org.unipaderborn.snlp.nlp;

import java.util.HashMap;

import org.unipaderborn.snlp.models.SentenceRelationObject;

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

		
	public static SentenceRelationObject getRelationsKeywords(String inputstatement) {

		SemanticRolesOptions semanticRoles = new SemanticRolesOptions.Builder().build();
		KeywordsOptions keywords = new KeywordsOptions.Builder().build();
		
		Features features = new Features.Builder().semanticRoles(semanticRoles).keywords(keywords).build();

		AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(inputstatement).features(features).build();

		AnalysisResults response = service.analyze(parameters).execute();
		System.out.println(response);

		SentenceRelationObject relations = extractSubjectObjectPredicate(response.toString());
		HashMap<String, Double> keywordWithRevalance = extractKeywordsRelevance(response.toString());
		
		return relations;
		
		
	}

	private static HashMap<String, Double> extractKeywordsRelevance(String jsonString) {

		JsonParser parser = new JsonParser();
		JsonObject rootObj = parser.parse(jsonString).getAsJsonObject();

		JsonArray keyArrayJson = rootObj.get("keywords").getAsJsonArray();

		HashMap<String, Double> keywordRelevance = new HashMap<String, Double>();

		for (JsonElement keywords : keyArrayJson) {

			JsonObject keywordsObJ = keywords.getAsJsonObject();
			String keyword = keywordsObJ.get("text").getAsString();
			double relevance = keywordsObJ.get("relevance").getAsDouble();
			keywordRelevance.put(keyword, relevance);
		}

		System.out.println("Keywords = " + keywordRelevance.toString());
		
		return keywordRelevance;

	}

	@SuppressWarnings("null")
	private static SentenceRelationObject extractSubjectObjectPredicate(String jsonString) {

		SentenceRelationObject relationsObj = new SentenceRelationObject();
		JsonParser parser = new JsonParser();
		JsonObject rootObj = parser.parse(jsonString).getAsJsonObject();

		JsonArray semRelArray = rootObj.get("semantic_roles").getAsJsonArray();

		for (JsonElement pa : semRelArray) {

			JsonObject sematicRolesObj = pa.getAsJsonObject();
			JsonObject subjectObj = sematicRolesObj.get("subject").getAsJsonObject();
			String subject = subjectObj.get("text").getAsString();

			JsonObject predicateObj = sematicRolesObj.get("action").getAsJsonObject();
			String predicate = predicateObj.get("text").getAsString();

			JsonObject objectObj = sematicRolesObj.get("object").getAsJsonObject();
			String object = objectObj.get("text").getAsString();

			System.out.println("Subject = " + subject + " Object = " + object + " Predicate =  " + predicate);
			
			relationsObj.setSubject(subject);
			relationsObj.setObject(object);
			relationsObj.setPredicate(predicate);
		}
		
		return relationsObj;
	}

}
