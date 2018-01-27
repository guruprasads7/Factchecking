package org.unipaderborn.snlp.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.unipaderborn.snlp.models.SearchResults;
import org.unipaderborn.snlp.models.WordnetResults;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WordnetParser {

	public static void synonymExtractor() throws IOException, Exception {

		ArrayList<String> keywords = new ArrayList<String>(
				Arrays.asList("Nobel Prize", "Albert Einstein", "honour", "Literature"));
		String query = "Nobel Prize";

		ArrayList<String> resultKeywords = new ArrayList<String>();
		
		for (String keyword : keywords) {

			String URIString = "http://131.234.29.16:5679/synonyms/1/";
			URL url = new URL(URIString + URLEncoder.encode(keyword, "UTF-8"));

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			StringBuilder sb = new StringBuilder("");

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			conn.disconnect();

			String searchResult = sb.toString();
			searchResult = Normalizer.normalize(searchResult, Normalizer.Form.NFD);
			String normalizedString = searchResult.replaceAll("[^\\x00-\\x7F\"\']", "").replaceAll("\\\\n", "")
					.replaceAll("\\\\r", "");

			char[] elloh = normalizedString.toCharArray();

			Set<String> res = new HashSet<String>();
			res.add(keyword);
			
			JSONArray jsonarray = new JSONArray(normalizedString);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				String pos = jsonobject.getString("POS");

				if (pos.equalsIgnoreCase("noun")) {
					String word = jsonobject.getString("word");
					res.add(word);
					}
				}

			String synonmys = String.join("|",res);
			
			resultKeywords.add(synonmys);
			}
		
		System.out.println(resultKeywords);
			
		}


}
