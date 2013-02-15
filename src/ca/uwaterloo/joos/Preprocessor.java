package ca.uwaterloo.joos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uwaterloo.joos.scanner.Token;
import ca.uwaterloo.joos.scanner.Scanner.ScanException;

public class Preprocessor {

	private final Set<String> undesiredTokenKinds = new HashSet<String>();

	public Preprocessor() {
		this.undesiredTokenKinds.add("COMMENT");
		this.undesiredTokenKinds.add("BLOCKCOMMENT");
		this.undesiredTokenKinds.add("JAVADOC");
	}

	public List<Token> processTokens(List<Token> rawTokens) throws ScanException {
		List<Token> tokens = new ArrayList<Token>();

		for(Token token: rawTokens) {
			if(!this.undesiredTokenKinds.contains(token.getKind())) tokens.add(token);
			if(token.getKind().equals("INTLIT"))
			{
				checkIntRange(token.getLexeme());
			}
		}

		tokens.add(0, new Token("BOF","BOF"));
		tokens.add(new Token("EOF","EOF"));
		return tokens;
	}
	
	private static void checkIntRange(String intString)throws ScanException{
		String intergerThreshold = "2147483647";
		
		if(intString.length() == intergerThreshold.length())
		{
		   for(int i = 0;i<intergerThreshold.length();i++)
		   {
			   if((int)intString.charAt(i)>(int)intergerThreshold.charAt(i)){
				  
					throw new ScanException("Interger out of Range");
			   }
		   }
		}
		if(intString.length() > intergerThreshold.length())
		{
			throw new ScanException("Interger out of Range");	
		}
	}
}
