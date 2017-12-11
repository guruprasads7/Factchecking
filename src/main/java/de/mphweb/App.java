package de.mphweb;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Scanner;
import edu.stanford.nlp.simple.*;

public class App {

    //We need a real browser user agent or Google will block our request with a 403 - Forbidden
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    public static void main(String[] args) throws Exception {
        /*
        //Fetch the page
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter your fact to query:");
        String fact = reader.nextLine(); // Scans the next token of the input as an int.
        reader.close();
        String query = fact.replace(' ', '+');
        final Document doc = Jsoup.connect("https://google.com/search?q=" + query).userAgent(USER_AGENT).get();

        //Traverse the results
        for (Element result : doc.select("h3.r a")){

            final String title = result.text();
            final String url = result.attr("href");

            //Now do something with the results (maybe something more useful than just printing to console)

            System.out.println(title + " -> " + url);
        }
*/
        FactChecker fc = new FactChecker();
        String[] factToAssess = new String[3];
        factToAssess[0] = "Albert Einstein";
        factToAssess[1] = "born";
        factToAssess[2] = "Ulm";
        boolean isStatementTrue = fc.BagOfWordsApproach(factToAssess, "Albert Einstein was born at Ulm, in Württemberg, Germany, on March 14, 1879. Six weeks later the family moved to Munich, where he later on began his schooling at the Luitpold Gymnasium. Later, they moved to Italy and Albert continued his education at Aarau, Switzerland and in 1896 he entered the Swiss Federal Polytechnic School in Zurich to be trained as a teacher in physics and mathematics. In 1901, the year he gained his diploma, he acquired Swiss citizenship and, as he was unable to find a teaching post, he accepted a position as technical assistant in the Swiss Patent Office. In 1905 he obtained his doctor's degree.",
                1, 1, 1);
    }
}
