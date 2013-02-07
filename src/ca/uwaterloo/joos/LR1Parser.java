package ca.uwaterloo.joos;

import java.util.*;
import java.util.logging.*;

import ca.uwaterloo.joos.LR1.*;

/**
 *
 * @author Wenzhu Man
 *
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

	public boolean checkGrammer(List<Token> tokens) {
		String startSymbol = lr1.getStartSymbol();
		ListIterator<Token> tokensIterator = tokens.listIterator();
		Stack<TransitionState> parseStack = new Stack<TransitionState>();
		parseStack.push(new TransitionState(null, 0));

		int currentState = 0;
		Token nextToken = tokensIterator.next();
		Action nextAction = null;
		while (!nextToken.getKind().equals(startSymbol)) {
			currentState = parseStack.peek().getState();
			nextAction = lr1.actionFor(currentState, nextToken.getKind());
			LR1Parser.logger.fine("Next:\tState " + currentState + " + " + nextToken + " => " + nextAction);

			if (nextAction instanceof LR1.ActionShift) {
				ActionShift shift = (ActionShift) nextAction;
				LR1Parser.logger.info("Shift:\t" + shift);

				TransitionState transitionState = new TransitionState(nextToken, shift.getToState());
				parseStack.push(transitionState);
				LR1Parser.logger.info("├─ Push:\t" + transitionState);

				if(!tokensIterator.hasNext()) tokensIterator = tokens.listIterator();
				nextToken = tokensIterator.next();
			}
			else if (nextAction instanceof LR1.ActionReduce){
				ActionReduce reduce = (ActionReduce) nextAction;
				LR1Parser.logger.info("Redue:\t" + reduce);

				for (int i = 0; i < reduce.getInt(); i++) {
					TransitionState poppedState = parseStack.pop();
					LR1Parser.logger.info("├─ Pop:\t" + poppedState);
				}
				tokensIterator.previous();
				nextToken = new Token(reduce.getProductionRule().getLefthand(), Arrays.toString(reduce.getProductionRule().getRighthand()));
			}
			else {
				LR1Parser.logger.severe("ERROR: No valid action for State " + currentState + " + " + nextToken);
				return false;
			}
		}

		LR1Parser.logger.info("Tokens has been validated against LR1");
		return true;
	}
}
