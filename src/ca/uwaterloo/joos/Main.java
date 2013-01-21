/**
 * 
 */
package ca.uwaterloo.joos;

import java.io.*;
import java.util.*;

/**
 * @author Greg Wang
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Construct a DFA from file
		DFA wlDFA = null;
		try {
			wlDFA = new DFA(new File("resources/wl.dfa"));
		} catch (Exception e) {
			System.err.println("ERROR: Invalid DFA File format: " + e.getLocalizedMessage());
		}
		wlDFA.tokenKindTransformations.put("wain", "WAIN");
		wlDFA.tokenKindTransformations.put("int", "INT");
		wlDFA.tokenKindTransformations.put("if", "IF");
		wlDFA.tokenKindTransformations.put("else", "ELSE");
		wlDFA.tokenKindTransformations.put("while", "WHILE");
		wlDFA.tokenKindTransformations.put("println", "PRINTLN");
		wlDFA.tokenKindTransformations.put("return", "RETURN");
		
		// Construct a Scanner which use the DFA
		Scanner scanner = new Scanner(wlDFA);
		List<Token> tokens = null;
		
		try {
			tokens = scanner.fileToTokens(new File("resources/gcd.wl"));
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getLocalizedMessage());
			System.exit(-1);
		}
		
		int i = 0;
		for(i = 0; i < tokens.size(); i++) {
			System.out.println(tokens.get(i).toString());
		}
	}

}
