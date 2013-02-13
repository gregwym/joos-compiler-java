/**
 *
 */
package ca.uwaterloo.joos;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.uwaterloo.joos.parser.LR1;
import ca.uwaterloo.joos.parser.LR1Parser;
import ca.uwaterloo.joos.parser.ParseTree;
import ca.uwaterloo.joos.scanner.DFA;
import ca.uwaterloo.joos.scanner.Scanner;
import ca.uwaterloo.joos.scanner.Token;

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

		/* Scanning */
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

		// Scan the source codes into tokens
		try {
			tokens = scanner.fileToTokens(new File("resources/testcases/a1/J1_01.java"));
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getLocalizedMessage() + " " + e.getClass().getName());
			e.printStackTrace();
			System.exit(42);
		}

		// Preprocess the tokens
		Preprocessor preprocessor = new Preprocessor();
		tokens = preprocessor.processTokens(tokens);

		/* Parsing */
		// Construct a LR1 from file
		LR1 lr1 = null;

		try {
			lr1 = new LR1(new File("resources/joos.lr1"));
		} catch (Exception e) {
			System.err.println("ERROR: Invalid LR1 File format: " + e.getLocalizedMessage() + " " + e.getClass().getName());
			e.printStackTrace();
			System.exit(-2);
		}

		LR1Parser lr1Parser = new LR1Parser(lr1);
		ParseTree parseTree = null;
		try {
			parseTree = lr1Parser.parseTokens(tokens);
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getLocalizedMessage() + " " + e.getClass().getName());
			e.printStackTrace();
			System.exit(42);
		}

		System.out.println(parseTree);
	}
}
