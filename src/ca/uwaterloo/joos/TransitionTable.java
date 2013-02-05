package ca.uwaterloo.joos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	private Set<String> ProductionRules; //Probably not
	
	
	//TODO Have inner map use tokenkind as key values
	private Map <String, Map<String, String[]>> TransitionRules;
	
	public String getLRTransitions(){
		//TODO Temporary method for testing remove when finished
		return LRTransitions.toString();
	}
	public TransitionTable(File lr1){
		//Init
		this.Terminals = new HashSet<String>();
		this.NonTerminals = new HashSet<String>();
		this.ProductionRules = new HashSet<String>();
		this.TransitionRules = new HashMap<String, Map<String, String[]>>();
		try {
			parseFile(lr1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void parseFile(File lr1) throws IOException{//TODO May want to merge this with DFA scanner...
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
		
	}

	private void parseTransition(String[] split) {
		//Adds to the parse hash 
		Map<String,String[]> transitionRule  = TransitionRules.get(split[0]);
		
		if (transitionRule == null){
			transitionRule = new HashMap<String, String[]>();
			this.TransitionRules.put(split[0], transitionRule);
		}
		String[] rule = {split[2], split[3]}; 
		transitionRule.put(split[1], rule);
		
		
	}
	
	/**
	 * Used to get the next transition action for the parser.
	 * @param state The current state of the parser
	 * @param Token The lookahead token
	 * @return A string array containing the transition rule and the next state
	 */
	
	public String[] getTransition(String state, String Token){
		//TODO replace second param with token type
		String[] ret = null;
		ret = this.TransitionRules.get(state).get(Token);
		return ret;
	}
}
