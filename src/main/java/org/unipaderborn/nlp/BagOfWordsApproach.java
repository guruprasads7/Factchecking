package org.unipaderborn.nlp;

import java.util.List;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class BagOfWordsApproach {


    public boolean BagOfWordsApproach(String[] factToAssess, String corpus,
                          int minSubjectAppearances, int minPredicateAppearances, int minObjectAppearances){
        String[] subject = lemmasOf(factToAssess[0]);
        String[] predicate = lemmasOf(factToAssess[1]);
        String[] object = lemmasOf(factToAssess[2]);

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

    String[] lemmasOf(String text){
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
}
