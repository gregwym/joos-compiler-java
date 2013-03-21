//TODO TEMPORARY: Reads the conditionals of condition loops

//	Takes a conditional and determines if it is a constant 
//	Idea is to call this from within reachability visitor
//	This class then returns an int which the visitor responds accordingly to

//RETURNS: 

//			1 - Condition means the block is always run
package ca.uwaterloo.joos.reachability;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.primary.LiteralPrimary;
import ca.uwaterloo.joos.ast.statement.ForStatement;
import ca.uwaterloo.joos.ast.statement.IfStatement;
import ca.uwaterloo.joos.ast.statement.WhileStatement;

public class ConditionalChecker {
	
	
	public static int condCheck(ASTNode node) throws Exception{
		//Parameters?
		//TODO: Check the condition for each control loop
//				If the condition comes from a variable, ensure it is initalized...
//				If the condition is a literal, it needs to be BOOLEAN from A3.
//				If the constant is FALSE, throw exception
//				If the constant is TRUE
//					FOR/WHILE: no other code must exist within the same block
//					IF: Dead code exists AFTER the if block if it contains a return
		
		
		/* Extract Conditional */
		ASTNode CNode = null;
		
		if (node instanceof WhileStatement){
			
			CNode = ((WhileStatement)node).getWhileCondition();
		}
		
		if (node instanceof IfStatement){
			CNode = ((IfStatement)node).getIfCondition();
		}
		
		if (node instanceof ForStatement){		
			CNode = ((ForStatement)node).getForTest();
		}
		
		/* Now check extracted conditional */
		if(CNode instanceof LiteralPrimary){
				//Conditional is a constant.
				//Get the value of the constant as a string and compare
			String value = ((LiteralPrimary) CNode).getValue();
			if (value.equals("true")){
				return 1; 
			}
			else {
				//An if block with a constant false conditional is an automatic exception
				throw new Exception("Constant FALSE used for if conditional");
			}
//			System.out.println(((LiteralPrimary) CNode).getValue());
		}
			
		if (CNode instanceof Expression){
				//We can ignore the expression as long as the variable is definitely 
				//assigned at this point (check elsewhere)
			
				//TODO This MAY not be true if the condition contains an assignment
				//		In such a case, we already know a boolean is assigned, so 
				//		we treat it as a constant.
				//		(j = false || i = true) => a constant of TRUE...?
		}
			
		
		
		
		return 0;
	}
}
