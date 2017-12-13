package org.unipaderborn.nlp;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Stopwords {

    public HashSet<String> stopwords;

    public Stopwords() throws IOException {
        stopwords = new HashSet<String>();
        BufferedReader dataBR = new BufferedReader(new FileReader("src/main/resources/stopwords.txt"));
        String stopword = "";
        while ((stopword = dataBR.readLine()) != null) {
            stopwords.add(stopword);

        }
    }

    public String removeStopwords(String text){
        String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        String output = "";
        for(int i=0; i < words.length; i++){
            if(!isStopword(words[i]))
                output += " " + words[i];
        }
        return output.substring(1);
    }

    public boolean isStopword(String word){
        return stopwords.contains(word);
    }

}
