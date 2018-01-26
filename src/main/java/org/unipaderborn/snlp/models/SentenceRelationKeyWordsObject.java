package org.unipaderborn.snlp.models;

import java.util.ArrayList;
import java.util.List;

public class SentenceRelationKeyWordsObject {
	
	SentenceRelationObject relationsObject = new SentenceRelationObject();
	List<String> keywords = new ArrayList<String>();
	
	public SentenceRelationKeyWordsObject(SentenceRelationObject relationsObject, List<String> keywords) {
		super();
		this.relationsObject = relationsObject;
		this.keywords = keywords;
	}
	
	public SentenceRelationKeyWordsObject() {

	}

	public SentenceRelationObject getRelationsObject() {
		return relationsObject;
	}

	public void setRelationsObject(SentenceRelationObject relationsObject) {
		this.relationsObject = relationsObject;
	}
	
	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	
	@Override
	public String toString() {
		String outputString = "Relations : " + this.relationsObject.toString() + ",\t Keywords = " + this.keywords;
		return outputString;
	}
}
