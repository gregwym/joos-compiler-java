package ca.uwaterloo.joos.scanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;

public class Scanner {
	private static final Logger logger = Main.getLogger(Scanner.class);
	private DFA dfa = null;

	@SuppressWarnings("serial")
	public static class ScanException extends Exception {
		public ScanException(String string) {
			super(string);
		}
	}

	public Scanner(DFA dfa) {
		logger.setLevel(Level.WARNING);
		this.dfa = dfa;
	}

	/**
	 * Turn a string into a list of tokens
	 * @param inStr The input string
	 * @return A list of Token
	 * @throws ScanException
	 */
	public List<Token> stringToTokens(final String inStr) throws ScanException {
		List<Token> tokens = new ArrayList<Token>();
		char[] inChars = inStr.toCharArray();
		for(int i = 0, n = inStr.length(); i < n; i = extractToken(inChars, i, n, tokens));

		logger.info("In total, scanned " + tokens.size() + " tokens");

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
		FileInputStream inputStream = new FileInputStream(inputFile);
		int inLen = inputStream.available();
		byte inBytes[] = new byte[inLen + 1];
		inputStream.read(inBytes);
		inputStream.close();
		inBytes[inLen] = '\n';

		String inStr = new String(inBytes);
		return this.stringToTokens(inStr);
	}

	/**
	 * Extract a token from the string into the tokens list
	 * Note: it will skip all leading whitespace
	 * @param inChars Raw input char array
	 * @param begin The beginning index
	 * @param end The ending index
	 * @param tokens Where new token will be export to
	 * @return The ending index for the extracted token
	 * @throws ScanException
	 */
	private int extractToken(final char[] inChars, int begin, int end, List<Token> tokens) throws ScanException {
		String state = this.dfa.getStartingState();
		String lexeme = "";

		// Go through each char, from begin
		for(; begin < end; begin++) {
			String inChar = String.valueOf(inChars[begin]);

			// Skip white spaces
			if(lexeme.length() == 0 && this.isWhitespace(inChar)) {
				continue;
			}

			// Find next state
			String next = this.dfa.nextStateFor(state, inChar);
			logger.finer("New char: " + inChar + (next == null ? "" : " next: " + next));

			// If don't have next state, and
			if(next == null){
				// is on an accepting state, export token and break
				if(this.dfa.isAcceptingState(state)){
					String transformedKind = this.dfa.getTokenKindTransformation(lexeme);
					Token token = new Token(transformedKind == null ? state : transformedKind, lexeme);
					tokens.add(token);
					logger.fine("Token added: " + token);
					break;
				}
				// not on an accepting state, throw exception (using simplified max munch)
				else {
					logger.severe("Token ended at non-accepting state: " + lexeme);
					throw new ScanException("Token ended at non-accepting state");
				}
			}

			// Shift state and append new char
			state = next;
			lexeme = lexeme + inChar;
		}

		return begin;
	}

	private boolean isWhitespace(String string) {
		return string.matches("\\s");
	}
}
