package ca.uwaterloo.joos;

import java.util.*;
import java.util.logging.*;

import ca.uwaterloo.joos.LR1.*;
import ca.uwaterloo.joos.scanner.Token;

/**
 * LR1 Parser
 *
 * which use the given LR1 table to parse a list of tokens
 *
 * @author Wenzhu Man
 *
 */
public class LR1Parser {
	private static final Logger logger = Main.getLogger();
	private final LR1 lr1;

	private class TransitionState {
		private Token token;
		private int state;

		public TransitionState(Token token, int state) {
			this.state = state;
			this.token = token;
		}

		public int getState() {
			return state;
		}

		public String toString() {
			return "<TransitionState> " + String.valueOf(this.state) + " | " + this.token;
		}
	}

	public LR1Parser(LR1 lr1) {
		this.lr1 = lr1;
	}

	/**
	 * Validate a list of tokens against the LR1
	 * 
	 * @param tokens the list of tokens to be validated
	 * @return whether the list of tokens is valid 
	 */
	public boolean checkGrammer(List<Token> tokens) {
		// Initialize startSymbol, tokens iterator and parsing stack
		String startSymbol = lr1.getStartSymbol();
		ListIterator<Token> tokensIterator = tokens.listIterator();
		Stack<TransitionState> parseStack = new Stack<TransitionState>();
		parseStack.push(new TransitionState(null, 0));

		// Initialize parsing state variables
		Token nextToken = tokensIterator.next();
		Action nextAction = null;

		// Execute lr1 parsing until the tokens has been reduced to the startSymbol
		while (!nextToken.getKind().equals(startSymbol)) {

			// Acquire next action, according to the current state and the next token kind
			nextAction = lr1.actionFor(parseStack.peek().getState(), nextToken.getKind());
			LR1Parser.logger.fine("Next:\tState " + parseStack.peek().getState() + " + " + nextToken + " => " + nextAction);

			// If is a shift
			if (nextAction instanceof LR1.ActionShift) {
				ActionShift shift = (ActionShift) nextAction;
				LR1Parser.logger.info("Shift:\t" + shift);

				// Push the transitionState to the stack
				TransitionState transitionState = new TransitionState(nextToken, shift.getToState());
				parseStack.push(transitionState);
				LR1Parser.logger.info("├─ Push:\t" + transitionState);

				// Iterate to next token
				// Note: if there is no next, move cursor back to front first
				if(!tokensIterator.hasNext()) tokensIterator = tokens.listIterator();
				nextToken = tokensIterator.next();
			}
			// If is a reduction
			else if (nextAction instanceof LR1.ActionReduce){
				ActionReduce reduce = (ActionReduce) nextAction;
				LR1Parser.logger.info("Redue:\t" + reduce);

				// Pop the stack according to the length of the production rule's RHS
				for (int i = 0; i < reduce.getInt(); i++) {
					TransitionState poppedState = parseStack.pop();
					LR1Parser.logger.info("├─ Pop:\t" + poppedState);
				}

				// Iterate back one token, since it has not been pushed to the stack
				tokensIterator.previous();

				// Next token is the production rule's LHS
				nextToken = new Token(reduce.getProductionRule().getLefthand(), Arrays.toString(reduce.getProductionRule().getRighthand()));
			}
			// No action found, validation failed
			else {
				LR1Parser.logger.severe("ERROR: No valid action for State " + parseStack.peek().getState() + " + " + nextToken);
				return false;
			}
		}

		// The tokens has been reduced to startSymbol, validation succeed
		LR1Parser.logger.info("Tokens has been validated against LR1");
		return true;
	}
}
