package org.unipaderborn.snlp.search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Stopwords {

    public List<String> stopwords;

    public Stopwords() throws IOException {
        stopwords = new ArrayList<String>();
        BufferedReader dataBR = new BufferedReader(new FileReader("src/main/resources/stopwords.txt"));
        String stopword = "";
        while ((stopword = dataBR.readLine()) != null) {
            stopwords.add(stopword);

        }
        dataBR.close();
    }

    public String removeStopwords(String text){
    	String[] words = text.replaceAll("[^\\x00-\\x7F\"']", "").replaceAll("\\\\n", "").replaceAll("\\\\r", "").toLowerCase().split("\\s+");
        List<String> outputList = new ArrayList<String>();
        String output = "";
        for(String word: words) {
        	if(word.isEmpty()) continue;
        	if(isStopword(word)) continue;
        	output += (word+" ");
        }
        System.out.println("Stopword removal = " + output);
        return output;
    }

    public boolean isStopword(String word){
    	if(stopwords.contains(word)) return true;
		else return false;
        
    }

}
