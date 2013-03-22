//TODO

//	Takes a conditional and determines if it is a constant 
//	Idea is to call this from within reachability visitor
//	This class then returns an int which the visitor responds accordingly to

//ConditionalChecker.condCheck() can be called either from the Reachability visitor upon finding a
//control loop OR recursively from within condCheck() when the control statement contains multiple
//statements. The meaning of the return value is different for each case.

//RETURNS: 

//			0 - Conditional uses a variable
//			1 - Condition means the block is always run

package ca.uwaterloo.joos.reachability;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.InfixExpression;
import ca.uwaterloo.joos.ast.expr.primary.LiteralPrimary;
import ca.uwaterloo.joos.ast.statement.ForStatement;
import ca.uwaterloo.joos.ast.statement.IfStatement;
import ca.uwaterloo.joos.ast.statement.WhileStatement;

public class ConditionalChecker {
	
	
	public static int condCheck(ASTNode node) throws Exception{
		//TODO: Check the condition for each control loop
//				If the condition comes from a variable, ensure it is initalized...
//				If the condition is a literal, it needs to be BOOLEAN from A3.
//				If the constant is FALSE, throw exception
//				If the constant is TRUE
//					FOR/WHILE: no other code must exist within the same block
//					IF: Dead code exists AFTER the if block if it contains a return
		
		
		/*If the node parameter is not a control statement, then it 
		 * is an operand of an infix expression and this method was called
		 * recursively.
		 */
		if (node instanceof LiteralPrimary){
			if (((LiteralPrimary)node).getValue().equals("true")){
				return 1;
			}
			
			else if (((LiteralPrimary)node).getValue().equals("false")){
				return 0;
			}
		}
		
		if (node instanceof Expression){
			//Need to add another check for infix expression...
			return 2;
		}
		/*
		 * Otherwise, the method was called from the visitor and was passed a control statement
		 */
		/* Extract Conditional */
		ASTNode CNode = null;
		
		if (node instanceof WhileStatement){
			
			CNode = ((WhileStatement)node).getWhileCondition();
		}
		
		if (node instanceof IfStatement){
			CNode = ((IfStatement)node).getIfCondition();
		}
		
		if (node instanceof ForStatement){
			CNode = ((ForStatement)node).getForCondition();
			
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
//				throw new Exception("Constant FALSE used for if conditional");
				return 0;
			}
		}
		
		if (CNode instanceof InfixExpression){
			//We have multiple conditionals
			
			List<Expression> operands = (((InfixExpression)CNode).getOperands());
			//Run condCheck on both sides of the infix expression
			int LH = condCheck(operands.get(0)); 
			int RH = condCheck(operands.get(1));
			//Get the operator of the infix expression
			InfixExpression.InfixOperator operator = ((InfixExpression)CNode).getOperator();
			
			if (LH == 2 || RH == 2) return 2;
			
			if (operator == InfixExpression.InfixOperator.AND){
				return LH & RH;
			}
			
			else if (operator == InfixExpression.InfixOperator.OR){
				return LH | RH;
			}
		}
		
		if (CNode instanceof Expression){
				//We can ignore the expression as long as the variable is definitely 
				//assigned at this point (check elsewhere)
			
				//TODO This MAY not be true if the condition contains an assignment
				//		In such a case, we already know a boolean is assigned, so 
				//		we treat it as a constant.
				//		(j = false || i = true) => a constant of TRUE...?
				//IF InfxExpression, get result of CondChecker on both sides...
		}
		return 0;
	}
}
