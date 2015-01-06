package jninethelper.views;


import java.util.List;  
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;  
import javax.xml.bind.annotation.XmlElement;  
import javax.xml.bind.annotation.XmlRootElement;  
  
public class Build 
{
	@XmlAttribute (name="Name")
	public String Name ;


	@XmlAttribute (name="BuildOutput")
	public String BuildOutput ;



	
	@XmlElement(name="TextReplacement")
	public List<TextReplacement> lstTextReplacement ;


}
	
