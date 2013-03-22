package ca.uwaterloo.joos.reachability;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.FileUnit;
import ca.uwaterloo.joos.ast.Modifiers.Modifier;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.ConstructorDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.ast.expr.AssignmentExpression;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.InfixExpression;
import ca.uwaterloo.joos.ast.expr.InfixExpression.InfixOperator;
import ca.uwaterloo.joos.ast.expr.Lefthand;
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
//TODO: Integer Range Check is currently disabled.
//			It was throwing an exception for 1135104544
//AST visitor to determine reachability

//TODO: Determine if all code is reachable.
//		Ensure that all local variables have an initializer (separate visitor..?)

public class ReachabilityVisitor extends SemanticsVisitor{
	//Current reachable status. Switches to false once a return
	//statement is found. Set to true at the end of a return statement.
	private static List<String> inits = new ArrayList<String>();
	private static List<String> finits = new ArrayList<String>();
	
	private String currentDecl = null;
	public boolean reachable = true;
	public boolean isVoid	=false; //Tracks if the current method should return void
	public int always = 0;
	public Stack<Integer> alwaysStack = new Stack<Integer>();

	private ASTNode lastNode;
//	static int DEBUG_Numbers = 0; //Trach the number of existing Reachability Visitors
	public ReachabilityVisitor(SymbolTable table) {
		super(table);
//		DEBUG_Numbers ++;
		// TODO Might not need table...
		
	}
	
	@Override
	public void willVisit(ASTNode node) throws Exception{
		if (!reachable){
			throw new Exception ("UNREACHABLE CODE" + node);
		}	
	
		//What to do on first encountering a node
		//Consider root method and constructor blocks reachable
		//Initially consider all blocks as terminating normally
		//	Set a block to normal termination and reassess on didVisit

		if (node instanceof ConstructorDeclaration){
			isVoid = true;
			
			List<ParameterDeclaration> params = ((MethodDeclaration)node).getParameters();
			for (ParameterDeclaration param : params){
				inits.add(param.getName().getName());
			}
		}
		
		else if (node instanceof MethodDeclaration){
			for (String init : finits){
				inits.add(init);//Carry field declarations into next method
			}
			System.out.println(node);
			if (((MethodDeclaration)node).getType()==null){
				isVoid = true;
			}
			else if (((MethodDeclaration)node).getModifiers().containModifier(Modifier.ABSTRACT)) {
				isVoid = true;
			}
			
			else isVoid = false;
			
			List<ParameterDeclaration> params = ((MethodDeclaration)node).getParameters();
			for (ParameterDeclaration param : params){
				System.out.println(param.getName().getName());
				inits.add(param.getName().getName());
			}
		}
		
		if (node instanceof FieldDeclaration){
			currentDecl = ((FieldDeclaration)node).getName().getName();
			InitalizedChecker ic = new InitalizedChecker(table, currentDecl, inits);
			Expression initial = ((FieldDeclaration) node).getInitial();
			if (initial != null){
				initial.accept(ic);
			}
			finits.add(currentDecl);
			currentDecl = null;
		}
		
		if (node instanceof LocalVariableDeclaration){
//			System.out.println(((LocalVariableDeclaration) node).getInitial());
			currentDecl = ((LocalVariableDeclaration)node).getName().getName();
//			System.out.println(currentDecl);
			if (inits.contains(currentDecl)) inits.remove(currentDecl);
			InitalizedChecker ic = new InitalizedChecker(table, currentDecl, inits);
			Expression initial = ((LocalVariableDeclaration) node).getInitial();
			if (initial != null){
				initial.accept(ic);
				inits.add(currentDecl);
				System.out.println(currentDecl);
				System.out.println(inits.contains(currentDecl));
			}
			currentDecl = null;
		}
		
		
		if (node instanceof AssignmentExpression){
			//Check here if the assignment is valid and so
			
			AssignmentExpression ANode = (AssignmentExpression) node;
			Lefthand LH = ANode.getLeftHand();
			if (LH instanceof SimpleName){
				if (!inits.contains(((SimpleName) LH).getName())){
					InitalizedChecker ic = new InitalizedChecker(table, ((SimpleName) LH).getName(), inits);
					ANode.getExpression().accept(ic);
					inits.add(((SimpleName) LH).getName());
				}
			}
			
		}
		
		if (node instanceof SimpleName){
			
			ASTNode parent = ((SimpleName)node).getParent();
			if (parent instanceof InfixExpression||
					parent instanceof ArrayAccess){
				
				if (!inits.contains(((SimpleName)node).getName())){
					throw new Exception ("Variable used before initalization" + ((SimpleName)node).getName());
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
			always = ConditionalChecker.condCheck(node);
			this.alwaysStack.push(always);
			ReachabilityVisitor innerrv = new ReachabilityVisitor(this.table);
			IfStatement Inode = (IfStatement) node;
			
			//Run separate checks on the if else subtrees
			Inode.getIfStatement().accept(innerrv);
			//An If statement must end with a return statement
			//Thus, the new visitor has reachable set to false. Reset it
			reachable = innerrv.reachable;
			innerrv.reset(); 
			if (Inode.getElseStatement() != null){
				if (!(Inode.getElseStatement() instanceof IfStatement)){
					//In this case, we have an else statement
					innerrv.always = 1;
				}
				//if (always == 1) throw new Exception ("Else block after always returning if");
				Inode.getElseStatement().accept(innerrv);
				reachable = innerrv.reachable;
				always = innerrv.always;
				this.alwaysStack.pop();
				this.alwaysStack.push(always);
			}
			
			if (always == 0) reachable = true;
//			reachable = true; //...??? Ignoring unreachable if blocks
			
		}
		
		if (node instanceof ForStatement){
			always = ConditionalChecker.condCheck(node);
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
			this.alwaysStack.push(always);
		}
		
		if (node instanceof WhileStatement){
			always = ConditionalChecker.condCheck(node);
			this.alwaysStack.push(always);
			System.out.println("RV.willVisit.WhileStatement: " + always);
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
		}
		if (expr instanceof LiteralPrimary){
			return Integer.parseInt(((LiteralPrimary)expr).getValue());
		}
		else if (expr instanceof ExpressionPrimary){
			ExpressionPrimary ENode = (ExpressionPrimary)expr;
			return eval(ENode.getExpression());
		}
		
		
		else if (expr instanceof InfixExpression){
			
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
			System.out.println(FNode.getIdentifier());
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
			always = this.alwaysStack.pop();
//			if (always == 1 && reachable == true) throw new Exception ("Non Terminating constant true condition block"); 
			if (always == 0) reachable = true;
			if (always == 2) reachable = true;
			if (always == 1 && reachable == false) {
				isVoid=true;
				reachable = true;
			}
//			reachable = true;
			
		}
		
//		if (node instanceof IfStatement){
//			if (always != 1)reachable = true;
//		}
//		
		
		if (node instanceof WhileStatement){
			always = this.alwaysStack.pop();
			if (always == 1) {//While is always true, no code below is reachable
				reachable = false;
			}
			else if (always == 2 || always == 0) {
				reachable = true;
			}
		}
		if (node instanceof MethodDeclaration){
			System.out.println(node);
			if ((!isVoid )&& reachable) throw new Exception ("Non-Void method with no return value");
			reachable = true;
		}
		
		if (node instanceof ReturnStatement){
			System.out.println(node);
			System.out.println(((ReturnStatement) node).getExpression());
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
			inits = new ArrayList<String>();
			
		}
		
		if (node instanceof ClassDeclaration){
			finits = new ArrayList<String>();
		}
		
	}
	
	protected void reset(){
		this.reachable = true;
	}
	
}
