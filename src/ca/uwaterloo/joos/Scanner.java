package ca.uwaterloo.joos;

import java.util.*;
import java.io.*;

public class Scanner {
	private DFA dfa;
	private static Set<String> whitespaces = null;

	@SuppressWarnings("serial")
	public class ScanException extends Exception {
		public ScanException(String string) {
			super(string);
		}
	}

	public Scanner(DFA dfa) {
		this.dfa = dfa;
	}

	/**
	 * Turn a string into a list of tokens
	 * @param inStr The input string
	 * @return A list of Token
	 * @throws ScanException
	 */
	public List<Token> stringToTokens(final String inStr) throws ScanException {
		int i = 0;
		List<Token> tokens = new ArrayList<Token>();
		for(i = 0; i < inStr.length(); i = extractToken(inStr, i, tokens));

		return tokens;
	}

	/**
	 * Turn an input file into a list of tokens
	 * @param inputFile The input file
	 * @return A list of Token
	 * @throws ScanException
	 * @throws IOException The input file is unreadable
	 */
	public List<Token> fileToTokens(File inputFile) throws ScanException, IOException {
		FileInputStream inputSteam = new FileInputStream(inputFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputSteam));
		List<Token> tokens = new ArrayList<Token>();

		while(reader.ready()) {
			String line = reader.readLine();
			line = line + "\n"; //Added newline char to input
			int i = 0;
			for(i = 0; i < line.length(); i = extractToken(line, i, tokens));
		}
		reader.close();

		return tokens;
	}

	private boolean isWhitespace(String string) {
		if(Scanner.whitespaces == null) {
			Scanner.whitespaces = new HashSet<String>();
			Scanner.whitespaces.add(" ");
			Scanner.whitespaces.add("\n");
			Scanner.whitespaces.add("\r");
			Scanner.whitespaces.add("\t");
		}
		return Scanner.whitespaces.contains(string);
	}

	/**
	 * Extract a token from the string into the tokens list
	 * Note: it will skip all leading whitespace
	 * @param inStr Raw input string
	 * @param begin The beginning index
	 * @param tokens The token list
	 * @return The ending index for the extracted token
	 * @throws ScanException
	 */
	private int extractToken(final String inStr, int begin, List<Token> tokens) throws ScanException {
		String state = this.dfa.getStartingState();
		String lexeme = "";
		for(; begin < inStr.length(); begin++) {
			String inChar = inStr.substring(begin, begin + 1);
			if(lexeme.length() == 0 && this.isWhitespace(inChar)) {
				continue;
			}

			String next = this.dfa.nextStateFor(state, inChar);
			if(next == null){
				if(this.dfa.isAcceptingState(state)){
					String transformedKind = this.dfa.getTokenKindTransformation(lexeme);
					Token token = new Token(transformedKind == null ? state : transformedKind, lexeme);
					tokens.add(token);
					break;
				}
				else {
					throw new ScanException("Token ended at non-accepting state");
				}
			}
			state = next;
			lexeme = lexeme + inChar;
		}

		return begin;
	}
}
