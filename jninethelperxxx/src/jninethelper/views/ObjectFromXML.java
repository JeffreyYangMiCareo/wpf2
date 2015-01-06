package jninethelper.views;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ObjectFromXML {


	public static <T> T objectFromJABX  (String xml, Class<?> cls)
	{
	
		T result = null  ;
	 	JAXBContext context;
		try {
			context = JAXBContext.newInstance( cls );
		    Unmarshaller unMarshaller = context.createUnmarshaller();
		    result  = (T)  unMarshaller.unmarshal(new FileInputStream( xml ));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return result ;
	}
	
	
	  public static BuildVersions objectFromDOM (String fileName) throws Exception {
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document document = builder.parse(  new FileInputStream  (fileName )   );
	
		    return ProcessBuildVersionsNode (document.getDocumentElement() ) ;
		  
		}
	
	
	

	public static BuildVersions ProcessBuildVersionsNode ( Node nodeIn )
		{
			
			BuildVersions result = new BuildVersions () ;



			List<Build> lstBuild = new ArrayList<Build> () ;
			
		    for (int i = 0; i < nodeIn.getChildNodes().getLength(); i++) {
			   	Node node = nodeIn.getChildNodes().item(i) ;
			   	
			    if (node instanceof Element) 
			    	lstBuild.add ( ProcessBuildNode ( node ) ) ;
		      
		    }
				      
			result.lstBuild = lstBuild ;
			
			return result ;
			
			
		}	



	public static Build ProcessBuildNode ( Node nodeIn )
		{
			
			Build result = new Build () ;
			result.Name = nodeIn.getAttributes().getNamedItem("Name").getNodeValue();
			result.BuildOutput = nodeIn.getAttributes().getNamedItem("BuildOutput").getNodeValue();



			List<TextReplacement> lstTextReplacement = new ArrayList<TextReplacement> () ;
			
		    for (int i = 0; i < nodeIn.getChildNodes().getLength(); i++) {
			   	Node node = nodeIn.getChildNodes().item(i) ;
			   	
			    if (node instanceof Element) 
			    	lstTextReplacement.add ( ProcessTextReplacementNode ( node ) ) ;
		      
		    }
				      
			result.lstTextReplacement = lstTextReplacement ;
			
			return result ;
			
			
		}	



	public static TextReplacement ProcessTextReplacementNode ( Node nodeIn )
		{
			
			TextReplacement result = new TextReplacement () ;
			result.FileName = nodeIn.getAttributes().getNamedItem("FileName").getNodeValue();
			result.toText = nodeIn.getAttributes().getNamedItem("toText").getNodeValue();
			result.ReplacementID = nodeIn.getAttributes().getNamedItem("ReplacementID").getNodeValue();
			return result ;
			
		}




}