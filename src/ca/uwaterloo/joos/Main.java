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
			System.err.println("ERROR: Invalid DFA File format: " + e.getLocalizedMessage() + " " + e.getClass().getName());
			System.exit(-1);
		}
		wlDFA.addTokenKindTransformation("wain", "WAIN");
		wlDFA.addTokenKindTransformation("int", "INT");
		wlDFA.addTokenKindTransformation("if", "IF");
		wlDFA.addTokenKindTransformation("else", "ELSE");
		wlDFA.addTokenKindTransformation("while", "WHILE");
		wlDFA.addTokenKindTransformation("println", "PRINTLN");
		wlDFA.addTokenKindTransformation("return", "RETURN");

		// Construct a Scanner which use the DFA
		Scanner scanner = new Scanner(wlDFA);
		List<Token> tokens = null;

		try {
			tokens = scanner.fileToTokens(new File("resources/gcd.wl"));
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getLocalizedMessage() + " " + e.getClass().getName());
			System.exit(-1);
		}

		int i = 0;
		for(i = 0; i < tokens.size(); i++) {
			System.out.println(tokens.get(i).toString());
		}
	}

}
