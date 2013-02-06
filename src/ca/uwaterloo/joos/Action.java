package ca.uwaterloo.joos;


/**
 * Represents a single action of the LR(1) parser
 * @author mdbaker
 *
 */
public abstract class Action {

}

class Reduce extends Action{
	Rule rule;
}

class Shift extends Action{
	State nstate;
	public Shift(State nextstate){
		this.nstate = nextstate;
	}
	public State getState(){
		return nstate;
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
