package org.unipaderborn.snlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;



import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.VCARD;
import org.unipaderborn.snlp.models.InputFact;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
//import com.hp.hpl.jena.rdf.model.Literal;



public class IOHandler {

	public static List<InputFact> readFactsFromCSV(String fileName) {

		List<InputFact> inputfacts = new ArrayList<>();
		Path pathToFile = Paths.get(fileName);

		// create an instance of BufferedReader // using try with resource, Java 7
		// feature to close resources
		try (BufferedReader br = Files.newBufferedReader(pathToFile)) {

			String line = null;
			// read the first line from the text file
			br.readLine();

			// loop until all lines are read
			while ((line = br.readLine()) != null) {

				String[] attributes = line.split("\t");

				InputFact inputfact = createfacts(attributes);

				// adding book into ArrayList
				inputfacts.add(inputfact);
			
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return inputfacts;

	}

	private static InputFact createfacts(String[] metadata) {

		int factID = Integer.parseInt(metadata[0]);
		String factStatement = metadata[1];
		double trueFalse = Double.parseDouble(metadata[2]);

		// create and return book of this metadata
		return new InputFact(factID, factStatement, trueFalse);
	}

	public void writeOutput(List<InputFact> output) {
		
			  String factURI = "http://swc2017.aksw.org/task2/dataset/"; 
			  String propURI = "http://swc2017.aksw.org/hasTruthValue";
			  double value	= 1.1;
			  
			  Model model = ModelFactory.createDefaultModel();
			  
			  for (InputFact fact : output) {
				  
				  Resource node = model.createResource(factURI + fact.getFactID());
				  
				  Property prop = model.createProperty( propURI );
				  
				  Literal factexp = model.createTypedLiteral(new Double(fact.getTrueFalse()));

				  model.add(node,prop,factexp);
				}
			  
			  
			  			  			           
			  
			  
			  model.write(System.out,"TURTLE" );
			  
			  

			  
			  
			  }
			
	}

