package ca.uwaterloo.joos;

import java.util.List;
import java.util.Stack;

import ca.uwaterloo.joos.LR1.Action;

/**
 *
 * @author Wenzhu Man
 *
 *
 */

public class LR1Parser {

	private Stack<TransitionState> parseStack = new Stack<TransitionState>();
	private List<Token> tokenList;
	private int currentState = 0;
//	private Token currentToken = null;
	private int currentIndex = 0;
	private LR1 lr1;

	class TransitionState {
		private Token token;
		private int state;

		public TransitionState(Token token, int state) {
			this.state = state;
			this.token = token;
		}

		public int getState() {
			return state;
		}

		public Token getToken() {
			return token;
		}
	}

	public LR1Parser(LR1 lr1) {
		this.lr1 = lr1;
	}

	public boolean checkGrammer(List<Token> tokens) {

		tokenList = tokens;
		parseStack.push(new TransitionState(new Token("DollarSign", "$"), 0));
		Token nextToken = nextToken();

		while (true) {
			currentState = parseStack.peek().getState();
//			currentToken = parseStack.peek().getToken();
			System.out.println("@@@nexttoken" + nextToken.getKind()
					+ "currentState" + currentState);
			Action nextAction = lr1.actionFor(currentState, nextToken.getKind());

			if (nextAction instanceof LR1.ActionShift) {
				parseStack.push(new TransitionState(nextToken, nextAction
						.getInt()));
				System.out.println("shift push " + nextToken.toString()
						+ "currentState" + nextAction.getInt());
				nextToken = nextToken();
				if (nextToken == null) {
					continue;
				}
			}
			else if (nextAction instanceof LR1.ActionReduce){
				for (int i = 0; i < nextAction.getInt(); i++) {
					System.out.println("pop");
					parseStack.pop();
				}
				currentIndex--;

				// currentState = parseStack.peek().getState();
				String leftHand = ((LR1.ActionReduce) nextAction)
						.getProductionRule().getLefthand();
				// parseStack.push(new TransitionState(new
				// Token(leftHand,"NT"),currentState));
				System.out.println("reduce to" + leftHand);
				System.out.println(nextAction.toString());
				nextToken = new Token(leftHand, "NonTerminal");
				if (nextToken.getKind().equals("S")) {
					System.out.println("@@@accept");
					return true;
				}
				// nextToken = parseStack.peek().getToken();

				/*
				 * currentToken = parseStack.peek().getToken();
				 * System.out.println
				 * ("currentState"+currentState+"currentToken"+
				 * currentToken+"nextToken"+nextToken);
				 */

				// break;
			}
			else {
				System.out.println("Internal error: unknown action");
				break;
			}
		}
		return true;
	}

	private Token nextToken() {
		if (currentIndex == tokenList.size()) {
			currentIndex = 0;
		}
		Token nextToken = tokenList.get(currentIndex);
		currentIndex++;
		return nextToken;
	}
}
