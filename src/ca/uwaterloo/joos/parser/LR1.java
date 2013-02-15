package ca.uwaterloo.joos.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;

/**
 *
 * In progress.
 *
 * Holds a transition table constructed after reading an lr1
 * transition file. Rules are extracted as string arrays
 * through the method getTransition()
 *
 * @author Matt Baker
 *
 */

public class LR1 {
	private static final Logger logger = Main.getLogger(LR1.class);

	enum LR1Struct {
		BOF,
		TerminalSymbols,
		NonTerminalSymbols,
		StartSymbol,
		ProductionRules,
		NumOfStates,
		TransitionRules,
		EOF
	}

	private Set<String> terminalSymbols;
	private Set<String> nonTerminalSymbols;
	private String startSymbol;
	private List<ProductionRule> productionRules;
	private int numOfStates;
	private Map <Integer, Map<String, Action>> transitionRules;

	public LR1(File lr1File) throws Exception {
		this.terminalSymbols = new HashSet<String>();
		this.nonTerminalSymbols = new HashSet<String>();
		this.startSymbol = null;
		this.productionRules = new ArrayList<ProductionRule>();
		this.numOfStates = 0;
		this.transitionRules = new HashMap<Integer, Map<String, Action>>();

		parseFile(lr1File);
	}

	private void parseFile(File lr1File) throws Exception {
		FileInputStream inputSteam = new FileInputStream(lr1File);
		BufferedReader	reader = new BufferedReader(new InputStreamReader(inputSteam));

		int	remainLines = 0;
		LR1Struct stage = LR1Struct.BOF;
		while(reader.ready() && stage != LR1Struct.EOF){
			String line = reader.readLine();
			String[] split = line.split("\\s");
			logger.finer("Read in line: " + line);

			if(remainLines == 0) {
				stage = LR1Struct.values()[stage.ordinal() + 1];
				logger.fine("Stage changes to " + stage.toString());

				// Deal with special cases, or read new remainLines
				if(stage == LR1Struct.StartSymbol) this.startSymbol = split[0];
				else if(stage == LR1Struct.NumOfStates) this.numOfStates = Integer.parseInt(split[0]);
				else remainLines = Integer.parseInt(split[0]);
				continue;
			}
			remainLines--;

			// Parse the line
			switch(stage) {
			case TerminalSymbols:
				this.terminalSymbols.add(split[0]);
				break;
			case NonTerminalSymbols:
				this.nonTerminalSymbols.add(split[0]);
				break;
			case ProductionRules:
				this.productionRules.add(new ProductionRule(split));
				break;
			case TransitionRules:
				this.parseTransition(split);
				break;
			case BOF:
			case StartSymbol:
			case NumOfStates:
			case EOF:
			default:
				break;
			}
		}
		logger.info("LR1 Table constructed with:\n" +
					this.terminalSymbols.size() + " terminal symbols\n" +
					this.nonTerminalSymbols.size() + " non terminal symbols\n" +
					this.startSymbol + " as the start symbol\n" +
					this.productionRules.size() + " production rules\n" +
					this.numOfStates + " number of states\n" +
					this.transitionRules.size() + " transition rules");

		reader.close();
	}

	private void parseTransition(String[] split) {
		assert split.length == 4: "A LR1 transition must have 4 components";

		//NOTE: split[0] was being passed as a key for transitionRules without parsing it first
		logger.finer("Parsing: " + split[0] + " " + split[1] + " " + split[2] + " " + split[3]);

		// Find all rules for the given state, if nothing, create and add a new Map
		int state = Integer.parseInt(split[0]);
		Map<String, Action> rulesForState = transitionRules.get(state);
		if (rulesForState == null){
			rulesForState = new HashMap<String, Action>();
			this.transitionRules.put(state, rulesForState);
		}

		// Parse and construct Action accordingly
		Action action = null;
		if (split[2].equals("reduce")){
			ProductionRule productionRule = this.productionRules.get(Integer.parseInt(split[3]));
			action = new ActionReduce(productionRule);
		}
		else if (split[2].equals("shift")){
			action = new ActionShift(Integer.parseInt(split[3]));
		}
		else {
			logger.severe("Reach unknown action type");
		}
		rulesForState.put(split[1], action);
		logger.fine("TransitionRule added: " + state + " " + split[1] + " -> " + action.toString());
	}

	/**
	 * Get the next transition action of the given state and tokenKind
	 * @param state The current state of the parser
	 * @param Token The lookahead token kind
	 * @return An Action that is either shift or reduce
	 */
	public Action actionFor(int state, String tokenKind){
		Action action = null;
		Map<String, Action> rulesForState = this.transitionRules.get(state);
		if (rulesForState == null){
			logger.info("NULL Transition Rule map returned for state: " + state);
			return null;
		}
		action = rulesForState.get(tokenKind);
		return action;
	}

	public String getStartSymbol() {
		return startSymbol;
	}

	public int getNumOfStates() {
		return numOfStates;
	}

	public boolean isTerminalSymbol(String symbol) {
		return this.terminalSymbols.contains(symbol);
	}

	public boolean isNonTerminalSymbol(String symbol) {
		return this.nonTerminalSymbols.contains(symbol);
	}

	public abstract class Action{

		/**
		 * A generic method for all actions.
		 *
		 * If is ReduceAction, return the length of the production rule's RHS.
		 * If is ShiftAction, return the id of next state
		 *
		 * @return An int that has different meaning for different action
		 */
		abstract public int getInt();
	}

	public class ActionReduce extends Action{
		private ProductionRule productionRule;

		public ActionReduce(ProductionRule productionRule){
			this.productionRule = productionRule;
		}
		public ProductionRule getProductionRule(){
			return this.productionRule;
		}

		/* (non-Javadoc)
		 * @see ca.uwaterloo.joos.TransitionTable.Action#getInt()
		 */
		@Override
		public int getInt(){
			//Returns the number of symbols to pop off the stack.
			return this.productionRule.getRighthand().length;
		}

		@Override
		public String toString() {
			return "<Action> Reduce by " + this.productionRule.toString();
		}
	}

	public class ActionShift extends Action{
		private int toState;

		public ActionShift(int toState){
			this.toState = toState;
		}

		public int getToState(){
			return this.toState;
		}

		/* (non-Javadoc)
		 * @see ca.uwaterloo.joos.TransitionTable.Action#getInt()
		 */
		@Override
		public int getInt() {
			return this.toState;
		}

		@Override
		public String toString() {
			return "<Action> Shift to " + String.valueOf(this.toState);
		}
	}


	public class ProductionRule{
		private String lefthand;
		private String[] righthand;

		public ProductionRule(String[] rule){
			this.lefthand = rule[0];
			this.righthand = new String[rule.length - 1];
			for(int i = 1; i < rule.length; i++) {
				this.righthand[i - 1] = rule[i];
			}
		}

		public String getLefthand(){
			return this.lefthand;
		}

		public String[] getRighthand(){
			return this.righthand;
		}

		@Override
		public String toString() {
			String string = "<ProductionRule> " + this.lefthand + " ->";
			for(String r: this.righthand) {
				string = string + " " + r;
			}
			return string;
		}
	}
}
