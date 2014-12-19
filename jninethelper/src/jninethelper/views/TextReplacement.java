package jninethelper.views;

public class TextReplacement {
	  String BuildName ;
	  String FileName ;
	  //String FromText ;
	  String ToText ;
	  String ReplacementID ;

	  @Override
	  public String toString() {
	    return ReplacementID + BuildName + FileName +  ToText ;
	  }
	  
	  
	}