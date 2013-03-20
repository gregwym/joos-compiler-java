package ca.uwaterloo.joos.reachability;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.FileUnit;
import ca.uwaterloo.joos.ast.decl.*;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.ast.statement.ForStatement;
//TODO: Integer Range Check is currently disabled.
//			It was throwing an exception for 1135104544

import ca.uwaterloo.joos.ast.statement.IfStatement;
import ca.uwaterloo.joos.ast.statement.ReturnStatement;
import ca.uwaterloo.joos.ast.statement.Statement;
import ca.uwaterloo.joos.ast.statement.WhileStatement;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
//AST visitor to determine reachability
import ca.uwaterloo.joos.symboltable.SymbolTable;

//TODO: Determine if all code is reachable.
//		This can be done by examining all statement and control blocks.

public class ReachabilityVisitor extends SemanticsVisitor{
	//Current reachable status. Switches to false once a return
	//statement is found. Set to true at the end of a return statement.
	public boolean reachable = true;
//	static int DEBUG_Numbers = 0; //Trach the number of existing Reachability Visitors
	public ReachabilityVisitor(SymbolTable table) {
		super(table);
//		System.out.println("NEW INST!");
//		DEBUG_Numbers ++;
		// TODO Might not need table...
		
	}
	
	@Override
	public void willVisit(ASTNode node) throws Exception{
		//What to do on first encountering a node
		//Consider root method and constructor blocks reachable
		//Initially consider all blocks as terminating normally
		//	Set a block to normal termination and reassess on didVisit
		if (node instanceof FileUnit){
			//TODO DEBUG: REMOVE WHEN FINISHED TESTING
//			System.out.println(((FileUnit) node));
		}
		
		if (node instanceof IfStatement){
			reachable = true;
			ReachabilityVisitor innerrv = new ReachabilityVisitor(this.table);
			IfStatement Inode = (IfStatement) node;
			
			//Run separate checks on the if else subtrees
			Inode.getIfStatement().accept(innerrv);
			//An If statement must end with a return statement
			//Thus, the new visitor has reachable set to false. Reset it
			if (innerrv.reachable == false) reachable = false;
			innerrv.reset(); 
			if (Inode.getElseStatement() != null)Inode.getElseStatement().accept(innerrv);
//			DEBUG_Numbers --;
			if (innerrv.reachable == false) reachable = false;
//			System.out.println("WV: PING " + reachable + " " + node);
			
		}
		//TODO Throw exception on constant false conditions
		
		//Currently, reachable is set to false once a return is hit.
		//We can ignore this by setting reachable to true at the end of each block
		
		if (!reachable){
			throw new Exception ("UNREACHABLE CODE AFTER STATEMENT " + node);
		}		
	}	
	
	public boolean visit(ASTNode node){
//		System.out.println("In Visit");
		if (node instanceof IfStatement){
			return false;
		}
		
		return true;
	}
	
	@Override
	public void didVisit(ASTNode node) throws Exception{
		if (node instanceof IfStatement ||
				node instanceof WhileStatement||
				node instanceof ForStatement) {
			reachable = true;
			
		}
		
		if (node instanceof MethodDeclaration){
			reachable = true;
		}
		
		if (node instanceof ReturnStatement){
			reachable = false;
		}
		
		if (node instanceof Block){
			//reachable = true;// TODO do Return statements get own block?
		}
		
		
		
	}
	
	protected void reset(){
		this.reachable = true;
	}
	
}
