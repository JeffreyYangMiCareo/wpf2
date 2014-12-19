package jninethelper.views;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import system.io.Stream;

public class DOMParserDemo {

	  public List<TextReplacement> parseBuildParam(String fileName) throws Exception {
	    //Get the DOM Builder Factory
	    DocumentBuilderFactory factory =    DocumentBuilderFactory.newInstance();

	    //Get the DOM Builder
	    DocumentBuilder builder = factory.newDocumentBuilder();

	    //Load and Parse the XML document
	    //document contains the complete XML as a Tree.
	    Document document =       builder.parse(  new FileInputStream  (fileName )   );

	    List<TextReplacement> empList = new ArrayList<>();

	    //Iterating through the nodes and extracting the data.
	    NodeList nodeList = document.getDocumentElement().getChildNodes();
	    String buildName = "" ;

	    for (int i = 0; i < nodeList.getLength(); i++) {

	      //We have encountered an <employee> tag.
	      Node node = nodeList.item(i);
	      if (node instanceof Element) {

	    	    buildName= node.getAttributes().getNamedItem("Name").getNodeValue();

		        NodeList childNodes = node.getChildNodes();
		        for (int j = 0; j < childNodes.getLength(); j++) {
		          Node cNode = childNodes.item(j);
		          if (cNode instanceof Element)
		          {
		        	  
		        		
			          TextReplacement tr = new TextReplacement () ;
			          tr.BuildName = buildName ;
			          tr.FileName = cNode.getAttributes().getNamedItem("FileName").getNodeValue();
			          //tr.FromText = cNode.getAttributes().getNamedItem("fromText").getNodeValue();
			          tr.ToText = cNode.getAttributes().getNamedItem("toText").getNodeValue();
			          tr.ReplacementID = cNode.getAttributes().getNamedItem("ReplacementID").getNodeValue();
		
			          empList.add(tr);
		        	  
		          }
		      }

	    }

	    //Printing the Employee list populated.
	    for (TextReplacement emp : empList) {
	      System.out.println(emp);
	    }
	    
	    

	  }
	    
	  return empList;
	  
	  
	}

	
	
	
}