package de.mphweb;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class App {

    //We need a real browser user agent or Google will block our request with a 403 - Forbidden
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    public static void main(String[] args) throws Exception {

        //Fetch the page
        final Document doc = Jsoup.connect("https://google.com/search?q=test").userAgent(USER_AGENT).get();

        //Traverse the results
        for (Element result : doc.select("div#res div.g h3 a")){

            final String title = result.text();
            final String url = result.attr("href");

            //Now do something with the results (maybe something more useful than just printing to console)

            System.out.println(title + " -> " + url);
        }
    }
}
