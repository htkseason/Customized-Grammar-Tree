package priv.season.cp.grammar;

import priv.season.cp.reader.WordBean;

public class GramError {
	WordBean errorWb;
	int errorWbPos;
	String expectedGrammarName;
	
	public GramError() {
		
	}
	
	public WordBean getErrorWordBean() {
		return errorWb;
	}
	
	public int getLine() {
		return errorWb.getLine();
	}
	
	public String getContent() {
		return errorWb.getContent();
	}
	
	public String getExpectedGrammarName() {
		return expectedGrammarName;
	}
	
	public void parseError() {
		//TODO : parse particular error
		
	}
	
}
