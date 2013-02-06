package ca.uwaterloo.joos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

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
 * 
 *
 */

//TODO 	Clean the scanning process
//		Remove unused declarations	
public class TransitionTable {
	//Holds a table containing the LR(1) transition rules 
	//for a given CFG.
	
	enum LRStruct {
		BOF,
		Terminals,
		NonTerminals,
		StartState,
		ProductionRules,
		LRStates,
		LRTransitions,
		LRRules,
		EOF
	}

	private String startState;
	private String LRStates;
	private String LRTransitions;
	private Set<String> Terminals;
	private Set<String> NonTerminals;
	private Vector<String[]>ProductionRules;
	
	
	//TODO Have inner map use tokenkind as key values
	private Map <String, Map<String, Action>> TransitionRules;
	
	public String getLRTransitions(){
		//TODO Temporary method for testing remove when finished
		return LRTransitions.toString();
	}
	public TransitionTable(File lr1){
		//Init
		this.Terminals = new HashSet<String>();
		this.NonTerminals = new HashSet<String>();
		this.ProductionRules = new Vector<String[]>();
		this.TransitionRules = new HashMap<String, Map<String, Action>>();
		try {
			parseFile(lr1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void parseFile(File lr1) throws IOException{//TODO May want to merge this with DFA scanner...
		System.out.print("PARSING LR1");
		FileInputStream inputSteam;
		BufferedReader	reader = null;
		int				remainLines = 0;
		
		try {
			inputSteam = new FileInputStream(lr1);
			reader = new BufferedReader(new InputStreamReader(inputSteam));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("TransitionTable.parseFile(): COULD NOT FIND lr1 FILE: " + lr1.toString());
		}
		
		LRStruct stage = LRStruct.BOF;
		while(reader.ready() && stage != LRStruct.EOF){
			String line = reader.readLine();
			String[] split = line.split("\\s");
//			System.out.println(line);
			
			
			if(remainLines == 0) {
				stage = LRStruct.values()[stage.ordinal() + 1];
//				System.out.println("TransitionTable.parseFile(): STATECHANGE " + stage.toString());
				if(stage == LRStruct.StartState) this.startState = split[0];
				else if(stage == LRStruct.LRStates)this.LRStates = split[0];
				//else if(stage == LRStruct.LRTransitions)this.LRTransitions = split[0];
				else remainLines = Integer.parseInt(split[0]);
				continue;
			}
			remainLines--;
			
			// Parse the line
			switch(stage) {
			case Terminals:
				this.Terminals.add(split[0]);
				break;
			case NonTerminals:
				break;
			case StartState:
				this.startState = (split[0]);
				break;
			case ProductionRules:

				this.ProductionRules.add(split);
				break;
			case LRStates:
				this.LRStates = split[0];
				break;
			case LRTransitions:
				this.parseTransition(split);
				break;
			case BOF:
			case EOF:
			default:
				break;
			}
			
			
		}
		for (int i = 0; i < ProductionRules.size(); i++){
			String[] rule = ProductionRules.get(i);
			System.out.print("PRODUCTION RULE: " + i + ": ");
			for (int j = 0; j < rule.length; j++){
				System.out.print(rule[j] + " ");
			}
			System.out.println();
		}
	}

	private void parseTransition(String[] split) {
		//Adds to the parse hash 
		Map<String, Action> transitionRule  = TransitionRules.get(split[0]);
		Action action = null;
		if (transitionRule == null){
			transitionRule = new HashMap<String, Action>();
			this.TransitionRules.put(split[0], transitionRule);
		}
		if (split[2].equals("reduce")){
			Rule nrule = new Rule(this.ProductionRules.get(Integer.parseInt(split[3]))); //TODO no parseInt...?
			action = new Reduce(nrule);
		}
		else{
			action = new Shift(Integer.parseInt(split[3]));
		}
		String[] rule = {split[2], split[3]}; 
		transitionRule.put(split[1], action);
		
		
	}
	
	/**
	 * Used to get the next transition action for the parser.
	 * @param state The current state of the parser
	 * @param Token The lookahead token
	 * @return A string array containing the transition rule and the next state
	 */
	
	public Action getTransition(String state, String Token){
		//TODO replace second param with token type
		Action ret = null;
		ret = this.TransitionRules.get(state).get(Token);
		return ret;
	}
	
	abstract class Action{
		abstract int getInt();
		abstract int getState();		//Test Method. REMOVE
		abstract void printRule(); 	//Test Method. REMOVE
	}
	
	class Reduce extends Action{//TODO change print to log
		Rule rule;
		public int getState(){
			System.err.println("Action<Reduce>: ERROR: getState called on a reduce action");
			System.exit(-2);
			return 0;
		}
		
		public Reduce(Rule inrule){
			rule = inrule;
		}
		public Rule getRule(){
			return rule;
		}
		
		public int getInt(){
			//Returns the number of symbols to pop off the stack.
			return rule.righthand.length;
		}
		
		public void printRule(){
			System.out.print("Action<Reduce>: Rule: " + rule.getLefthand() + " ");
			for (int i = 0; i < rule.righthand.length; i++){
				System.out.print(rule.getRighthand()[i] + " ");
			}
		}
	}

	class Shift extends Action{
		int nstate;
		public Shift(int nextstate){
			this.nstate = nextstate;
		}
		public int getState(){
			return nstate;
		}
		public int getInt(){
			//Returns the state to transition to.
			return nstate;
		}
		public void printRule(){
			System.err.println("Action<Shift>: printRule called on a SHIFT action");
			System.exit(-2);
		}
	}


	class Rule{
		String lefthand;
		String[] righthand;
		
		public Rule(String[] inrule){
			lefthand = inrule[0];
			righthand = new String[inrule.length - 1];
			
			for (int i = 1; i < inrule.length; i++){
				righthand[i - 1] = inrule[i];
			}
		}
		
		public String getLefthand(){
			return lefthand;
		}
		
		public String[] getRighthand(){
			return righthand;
		}
		
	}

	class State{
		private int state;
		State(int instate) {
			state = instate;
		}
		
		public int getNextState() {
			return state;
		}
	}
}
