/**
 *
 */
package ca.uwaterloo.joos;

import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * @author Greg Wang
 *
 */
public class Main {

	public static Logger getLogger() {
		return Logger.getLogger(Scanner.class.toString());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main.getLogger().setLevel(Level.INFO);

		Main.getLogger().fine("DFA constructing");

		// Construct a DFA from file
		DFA dfa = null;
		try {
			dfa = new DFA(new File("resources/joos.dfa"));
		} catch (Exception e) {
			System.err.println("ERROR: Invalid DFA File format: " + e.getLocalizedMessage() + " " + e.getClass().getName());
			System.exit(-1);
		}

		Main.getLogger().fine("DFA constructed: " + dfa);

		// Construct a Scanner which use the DFA
		Scanner scanner = new Scanner(dfa);
		List<Token> tokens = null;

		try {
			tokens = scanner.fileToTokens(new File("resources/sample.in"));
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getLocalizedMessage() + " " + e.getClass().getName());
			e.printStackTrace();
			System.exit(-1);
		}

		Preprocessor preprocessor = new Preprocessor();
		tokens = preprocessor.processTokens(tokens);

		int i = 0;
		for(i = 0; i < tokens.size(); i++) {
			System.out.println(tokens.get(i).toString());
		}

		//MATT ADD
		//Rudimentary transition table test. Once the parser is finished, the table can be declared and
		//accessed there.
		LR1 lr1 = new LR1(new File("resources/sample.lr1"));

		LR1Parser lr1Parser = new LR1Parser(lr1);
		lr1Parser.checkGrammer(tokens);
	}
}
