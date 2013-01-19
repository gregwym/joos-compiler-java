/**
 * 
 */
package ca.uwaterloo.joos;

import java.io.*;
import java.util.*;

/**
 * @author Greg Wang
 *
 */
public class DFA {
	
	enum DfaStruct {
		BOF,
		Alphabets, 
		States, 
		StartingState, 
		AcceptingStates, 
		Transactions, 
		EOF
	}
	
	public Set<String> alphabets;
	public Set<String> states;
	public String startingState;
	public Set<String> acceptingStates;
	public Map<String, Map<String, String>> transactions;
	public Map<String, String> tokenKindTransformations = null;
	
	/**
	 * Construct a DFA logic representation from a DFA file
	 * 
	 * @param dfaFile 
	 * @throws IOException Fail to read the file
	 * @throws ArrayIndexOutOfBoundsException DFA file in wrong format
	 * @throws NumberFormatException DFA file in wrong format
	 */
	public DFA(File dfaFile) throws IOException, ArrayIndexOutOfBoundsException, NumberFormatException {
		this.alphabets = new HashSet<String>();
		this.states = new HashSet<String>();
		this.acceptingStates = new HashSet<String>();
		this.transactions = new HashMap<String, Map<String, String>>();
		this.tokenKindTransformations = new HashMap<String, String>();
		
		parseDfaFile(dfaFile);
	}
	
	public String nextStateFor(String state, String input) {
		Map<String, String> inputMapNext = this.transactions.get(state);
		if(inputMapNext == null) {
			return null;
		}
		
		return inputMapNext.get(input);
	}
	
	private void parseDfaFile(File dfaFile) throws IOException, ArrayIndexOutOfBoundsException, NumberFormatException {
		FileInputStream inputSteam = new FileInputStream(dfaFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputSteam));
		
		int remainLines = 0;
		DfaStruct stage = DfaStruct.BOF;
		while(reader.ready() && stage != DfaStruct.EOF) {
			String line = reader.readLine();
			String[] split = line.split("\\s");
			
			// If initializing or no more line to read
			if(remainLines == 0) {
				stage = DfaStruct.values()[stage.ordinal() + 1];
//				System.out.println("At stage " + stage.toString());
				
				if(stage == DfaStruct.StartingState) this.startingState = split[0];
				else remainLines = Integer.parseInt(split[0]);
				continue;
			}
			remainLines--;
			
			// Parse the line
			switch(stage) {
			case Alphabets:
				this.alphabets.add(split[0]);
				break;
			case States:
				this.states.add(split[0]);
				break;
			case AcceptingStates:
				this.acceptingStates.add(split[0]);
				break;
			case Transactions:
				Map<String, String> inputMapNext = this.transactions.get(split[0]);
				if(inputMapNext == null){
					inputMapNext = new HashMap<String, String>();
					this.transactions.put(split[0], inputMapNext);
				}
				inputMapNext.put(split[1], split[2]);
				break;
			case BOF:
			case StartingState:
			case EOF:
			default:
				break;
			}
		}
		
		reader.close();
	}
}

