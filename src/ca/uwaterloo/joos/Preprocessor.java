package ca.uwaterloo.joos;

import java.util.*;

import ca.uwaterloo.joos.scanner.Token;

public class Preprocessor {
	
	private final Set<String> undesiredTokenKinds = new HashSet<String>();
	
	public Preprocessor() {
		this.undesiredTokenKinds.add("COMMENT");
		this.undesiredTokenKinds.add("BLOCKCOMMENT");
		this.undesiredTokenKinds.add("JAVADOC");
	}
	
	public List<Token> processTokens(List<Token> rawTokens) {   
		List<Token> tokens = new ArrayList<Token>();
		
		for(Token token: rawTokens) {
			if(!this.undesiredTokenKinds.contains(token.getKind())) tokens.add(token);
		}
		
		tokens.add(0, new Token("BOF","BOF"));
		tokens.add(new Token("EOF","EOF"));
		return tokens;
	}
}
