package jninethelper.views;


import java.util.List;  
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;  
import javax.xml.bind.annotation.XmlElement;  
import javax.xml.bind.annotation.XmlRootElement;  
  
public class TextReplacement 
{
	@XmlAttribute (name="FileName")
	public String FileName ;


	@XmlAttribute (name="toText")
	public String toText ;


	@XmlAttribute (name="ReplacementID")
	public String ReplacementID ;


}
	
