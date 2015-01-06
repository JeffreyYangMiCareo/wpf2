package jninethelper.views;


import java.util.List;  
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;  
import javax.xml.bind.annotation.XmlElement;  
import javax.xml.bind.annotation.XmlRootElement;  
  
@XmlRootElement(name = "BuildVersions")

public class BuildVersions 
{

	
	@XmlElement(name="Build")
	public List<Build> lstBuild ;


}
	
