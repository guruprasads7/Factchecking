package org.unipaderborn.snlp.models;

public class SentenceRelationObject {
	
	String subject = null;
	String predicate = null;
	String object  = null;
	
	
	public SentenceRelationObject(String subject, String predicate, String object) {
		super();
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}
	
	public SentenceRelationObject(){
		
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}

	@Override
	public String toString() {
		String outputString = "Subject = " + this.subject + ", Predicate = " + this.predicate + ", Object = " + this.object;
		return outputString;
	}
}
