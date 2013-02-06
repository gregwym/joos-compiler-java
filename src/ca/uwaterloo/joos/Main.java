/**
 *
 */
package ca.uwaterloo.joos;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import ca.uwaterloo.joos.TransitionTable.Action;
import ca.uwaterloo.joos.TransitionTable.Reduce;

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
			tokens = scanner.fileToTokens(new File("resources/testcases/a1/Je_16_Throws_This.java"));
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getLocalizedMessage() + " " + e.getClass().getName());
			e.printStackTrace();
			System.exit(-1);
		}

		int i = 0;
		for(i = 0; i < tokens.size(); i++) {
			System.out.println(tokens.get(i).toString());
		}
		
		//MATT ADD
		//Rudimentary transition table test. Once the parser is finished, the table can be declared and
		//accessed there.
		TransitionTable tt = new TransitionTable(new File("resources/joos.lr1"));
		Action tst = tt.getTransition("78", "CLASS");
		System.out.println("Action Int: " + tst.getInt());
		tst = tt.getTransition("88", "RBRACE");
		System.out.println("Action Int: " + tst.getInt());
		tst.printRule();
	}

}
