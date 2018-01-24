package org.unipaderborn.nlp;

public class InputFact {

	int factID = 0;
	String factStatement = null;
	double trueFalse = 0;
	
	public InputFact(int factID, String factStatement, double trueFalse) {
		super();
		this.factID = factID;
		this.factStatement = factStatement;
		this.trueFalse = trueFalse;
	}

	public int getFactID() {
		return factID;
	}

	public void setFactID(int factID) {
		this.factID = factID;
	}

	public String getFactStatement() {
		return factStatement;
	}

	public void setFactStatement(String factStatement) {
		this.factStatement = factStatement;
	}

	public double getTrueFalse() {
		return trueFalse;
	}

	public void setTrueFalse(double trueFalse) {
		this.trueFalse = trueFalse;
	}
	
	@Override
	public String toString() {
		String outputString = "FactID = " + this.factID + " factStatement = " + this.factStatement + " TrueFalse = " + this.trueFalse;
		return outputString;
	}
	
}
