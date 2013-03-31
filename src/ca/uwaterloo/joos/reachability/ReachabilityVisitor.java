package ca.uwaterloo.joos.reachability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.FileUnit;
import ca.uwaterloo.joos.ast.decl.ConstructorDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.ast.expr.AssignmentExpression;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.InfixExpression;
import ca.uwaterloo.joos.ast.expr.InfixExpression.InfixOperator;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.ast.expr.primary.ArrayAccess;
import ca.uwaterloo.joos.ast.expr.primary.ExpressionPrimary;
import ca.uwaterloo.joos.ast.expr.primary.LiteralPrimary;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.ast.statement.ForStatement;
import ca.uwaterloo.joos.ast.statement.IfStatement;
import ca.uwaterloo.joos.ast.statement.ReturnStatement;
import ca.uwaterloo.joos.ast.statement.WhileStatement;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;

public class ReachabilityVisitor extends SemanticsVisitor{
	public static final Logger logger = Main.getLogger(ReachabilityVisitor.class);
	
	//Current reachable status. Switches to false once a return
	//statement is found. Set to true at the end of a return statement.
	private List<String> inits = new ArrayList<String>();
	
	private String currentDecl = null;
	public boolean reachable = true;
	public boolean isVoid	=false; //Tracks if the current method should return void
	public int always = 0;

//	static int DEBUG_Numbers = 0; //Trach the number of existing Reachability Visitors
	public ReachabilityVisitor(SymbolTable table) {
		super(table);
//		DEBUG_Numbers ++;
		// TODO Might not need table...
//		logger.setLevel(Level.FINER);
	}
	
	private void setInits(Collection<String> inits) {
		this.inits = new ArrayList<String>(inits);
	}
	
	private void addInit(String var) {
		logger.fine("Adding init " + var);
		this.inits.add(var);
	}
	
	private void removeInit(String var) {
		logger.fine("Removing init " + var);
		this.inits.remove(var);
	}
	
	private void clearInits() {
		logger.fine("Clearing inits");
		this.inits.clear();
	}
	
	@Override
	public void willVisit(ASTNode node) throws Exception{
		
		if (!reachable){
			throw new Exception ("UNREACHABLE CODE");
		}	
	
		//What to do on first encountering a node
		//Consider root method and constructor blocks reachable
		//Initially consider all blocks as terminating normally
		//	Set a block to normal termination and reassess on didVisit

		if (node instanceof ConstructorDeclaration){
			isVoid = true;
		}
		
		else if (node instanceof MethodDeclaration){
			if (((MethodDeclaration)node).getType()==null){
				isVoid = true;
			}
			else isVoid = false;
		}
		
		if (node instanceof FieldDeclaration){
			currentDecl = ((FieldDeclaration)node).getName().getName();
			InitalizedChecker ic = new InitalizedChecker(currentDecl, inits);
			Expression initial = ((FieldDeclaration) node).getInitial();
			if (initial != null){
				initial.accept(ic);
				this.addInit(currentDecl);
			}
			currentDecl = null;
		}
		if (node instanceof ParameterDeclaration){
			currentDecl = ((ParameterDeclaration)node).getName().getName();
			this.addInit(currentDecl);
			currentDecl = null;
		}
		if (node instanceof LocalVariableDeclaration){
			currentDecl = ((LocalVariableDeclaration)node).getName().getName();
			if (inits.contains(currentDecl)) this.removeInit(currentDecl);
			InitalizedChecker ic = new InitalizedChecker(currentDecl, inits);
			Expression initial = ((LocalVariableDeclaration) node).getInitial();
			if (initial != null){
				initial.accept(ic);
				this.addInit(currentDecl);
			}
			currentDecl = null;
		}
		
		
		if (node instanceof AssignmentExpression){
			//Check here if the assignment is valid and so
			AssignmentExpression ANode = (AssignmentExpression) node;
			ASTNode LH = (ASTNode) ANode.getLeftHand();
			if (LH instanceof SimpleName){
				if (!inits.contains(((SimpleName) LH).getName())){
					InitalizedChecker ic = new InitalizedChecker(((SimpleName) LH).getName(), inits);
					ANode.getExpression().accept(ic);
					this.addInit(((SimpleName) LH).getName());
				}
			}
			
		}
		
		if (node instanceof SimpleName){
			
			ASTNode parent = ((SimpleName)node).getParent();
			if (parent instanceof InfixExpression ||
					parent instanceof ArrayAccess ||
					parent instanceof ReturnStatement || 
					parent instanceof AssignmentExpression){
				
				if (!inits.contains(((SimpleName)node).getName())){
					logger.fine("Current init: " + this.inits);
					throw new Exception ("Variable " + ((SimpleName)node).getName() + " used before initalization");
				}
			}
			
			
		}
		
		
		if (node instanceof ReturnStatement){
			//Check that we return nothing if void
			if (isVoid && ((ReturnStatement)node).getExpression()!= null){
				throw new Exception ("Non Null Return statement in void method");
			}
		}
		
		
		
		if (node instanceof IfStatement){
			
			reachable = true;
			always = condCheck(node);
			ReachabilityVisitor innerrv = new ReachabilityVisitor(this.table);
			IfStatement Inode = (IfStatement) node;
			
			innerrv.setInits(this.inits);
			//Run separate checks on the if else subtrees
			Inode.getIfStatement().accept(innerrv);
			//An If statement must end with a return statement
			//Thus, the new visitor has reachable set to false. Reset it
			reachable = innerrv.reachable;
			
			innerrv = new ReachabilityVisitor(this.table);
			innerrv.setInits(this.inits);
			if (Inode.getElseStatement() != null){
				if (!(Inode.getElseStatement() instanceof IfStatement)){
					//In this case, we have an else statement
					innerrv.always = 1;
				}
				//if (always == 1) throw new Exception ("Else block after always returning if");
				Inode.getElseStatement().accept(innerrv);
				reachable = innerrv.reachable;
				always = innerrv.always;
			}
			
			if (always == 0) reachable = true;
//			reachable = true; //...??? Ignoring unreachable if blocks
			
		}
		
		if (node instanceof ForStatement){
			always = condCheck(node);
			if (always == 0) throw new Exception ("Constant false for conditional");
			if (always == 2) {//Evaluate the condition to see if it ALWAYS returns false
				
				
				List<Expression> operands = ((InfixExpression)((ForStatement)node).getForCondition()).getOperands();
				
				Integer RH = eval(operands.get(1));
				Integer LH = eval(operands.get(0));
				if (RH != null && LH != null){
					if(!isTrue(RH,LH, ((InfixExpression)((ForStatement)node).getForCondition()).getOperator())){
						throw new Exception("Constant FALSE for Conditional");
					}
				}
				
			}
			
		}
		
		if (node instanceof WhileStatement){
			always = condCheck(node);
			if (always == 0) throw new Exception ("Unreachable While Block");
			
		}
	}	

	private boolean isTrue(Integer rh, Integer lh, InfixOperator operator) {
		//Returns true if the condition evaluates to true
		if (operator == InfixOperator.EQ){
			return (rh == lh);
		}
		
		else if (operator == InfixOperator.LT){
			return (rh < lh);
		}
		
		else if (operator == InfixOperator.LEQ){
			return (rh <= lh);
		}
		else if (operator == InfixOperator.GT){
			return (rh > lh);
		}
		else if (operator == InfixOperator.GEQ){
			return (rh >= lh);
		}
		
		return false;
	}

	private Integer eval(ASTNode expr) throws ChildTypeUnmatchException {
//		Returns the boolean evaluation of the for test if it is constant
		//Returns the value of an operation..........
		//The node represents an equation to be evaled. Either the operand is a literal
		//or it is not...
		if (expr instanceof SimpleName){
			return null;
		} else if (expr instanceof LiteralPrimary){
			return Integer.parseInt(((LiteralPrimary)expr).getValue());
		} else if (expr instanceof ExpressionPrimary){
			ExpressionPrimary ENode = (ExpressionPrimary)expr;
			return eval(ENode.getExpression());
		} else if (expr instanceof AssignmentExpression) {
			return eval(((AssignmentExpression) expr).getExpression());
		} else if (expr instanceof InfixExpression){
			
			InfixExpression ENode = (InfixExpression)expr;
			InfixOperator operator = ENode.getOperator();
			List<Expression> operands = ENode.getOperands();
			Integer LH = eval(operands.get(0));
			Integer RH = eval(operands.get(1));
			if (LH == null || RH == null){
				return null;
			}
			
			if (operator == InfixExpression.InfixOperator.PLUS){
				return LH + RH;
			}
			if (operator == InfixExpression.InfixOperator.MINUS){
				return LH - RH;
			}
			if (operator == InfixExpression.InfixOperator.STAR){
				return LH * RH;
			}
			if (operator == InfixExpression.InfixOperator.SLASH){
				return LH / RH;
			}
		}
		return 0;
	}
	
	public boolean visit(ASTNode node) throws Exception{
		if (node instanceof FileUnit){
			FileUnit FNode = (FileUnit) node;
//			System.out.println(FNode.getIdentifier());
			if (FNode.getIdentifier().equals("Byte.java")) return false;
			if (FNode.getIdentifier().equals("Character.java")) return false;
			if (FNode.getIdentifier().equals("Integer.java")) return false;
			if (FNode.getIdentifier().equals("Number.java")) return false;
			if (FNode.getIdentifier().equals("Object.java")) return false;
			if (FNode.getIdentifier().equals("Short.java")) return false;
			if (FNode.getIdentifier().equals("String.java")) return false;
			if (FNode.getIdentifier().equals("OutputStream.java")) return false;
		}
		
		if (node instanceof IfStatement){
			
			return false;
		}
		
		if (node instanceof LocalVariableDeclaration){
			return false;
		}
		return true;
	}
	
	@Override
	public void didVisit(ASTNode node) throws Exception{
		if (node instanceof IfStatement ||
				node instanceof ForStatement) {
//			if (always == 1 && reachable == true) throw new Exception ("Non Terminating constant true condition block"); 
			if (always == 0) reachable = true;
			if (always == 2) reachable = true;
//			reachable = true;
			
		}
		
//		if (node instanceof IfStatement){
//			if (always != 1)reachable = true;
//		}
//		
		
		if (node instanceof WhileStatement){
			if (always == 1) {//While is always true, no code below is reachable
				reachable = false;
			}
			else if (always == 2 || always == 0) reachable = true;
		}
		if (node instanceof MethodDeclaration){
			if ((!isVoid )&& reachable) throw new Exception ("Non-Void method with no return value");
			reachable = true;
		}
		
		if (node instanceof ReturnStatement){
			reachable = false;
		}
		
		
		if (node instanceof Block){
			//reachable = true;// TODO do Return statements get own block?
		}
		
		if (node instanceof MethodDeclaration ||
				node instanceof ConstructorDeclaration){
			//Don't need per block recording as we cannot have overlapping scope
			//and use before declaration is covered
			//Refresh the init list at the end of each method and constructor
			this.clearInits();
		}
		
	}
	
	protected void reset(){
		this.reachable = true;
	}
	
	public int condCheck(ASTNode node) throws Exception{
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

