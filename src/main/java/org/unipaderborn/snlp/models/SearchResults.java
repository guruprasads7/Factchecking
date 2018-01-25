package org.unipaderborn.snlp.models;

public class SearchResults {

	private String linkURL;
	private String title;
	private String body;
	
	public String getLinkURL() {
		return linkURL;
	}
	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "SearchResults [linkURL=" + linkURL + ", title=" + title + ", body=" + body + "]";
	}
	
	
}
