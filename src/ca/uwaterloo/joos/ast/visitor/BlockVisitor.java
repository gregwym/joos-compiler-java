package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class BlockVisitor extends SemanticsVisitor{
	private int level = -1;
	private int blocks = -1;
	private int blocklevel;
	public BlockVisitor(SymbolTable ist) {
		super(ist);
		
	}
	
	
	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception{

		if (node instanceof LocalVariableDeclaration){
			
			//TODO Check for multiple definitions
			//if name is not already in view
			LocalVariableDeclaration LNode = (LocalVariableDeclaration) node;
			if (st.hasField(LNode.getName().getName())) {
				System.err.println("Overlapping Declarations Exit 42");
				System.exit(42);
			}
			level++;
			st.addDeclaration(st.getName()+"."+LNode.getName().getName(), node, level);			
			return false;
		}
		
		if (node instanceof Block){
			if (blocks == -1) {//Then this is the block we are reading
				blocks++;
			}

			else {//Level is 0 so we found a nested block
				//TODO
				//	Make a new scope
				//	Add it to the Scopes 
				//	
				SymbolTable nst = new SymbolTable();
				blocks++;
				nst.addScope();//Add new symbol table to the global scope hash
				nst.setName(st.getName()+"."+blocks+"Block");
				nst.build(new BlockVisitor(nst), node);
				return false;
				
			}
		}
		
		return true;
	}
	
	
	
	@Override
	public void willVisit(ASTNode node){
		
	}
	
	@Override
	public void didVisit(ASTNode node){
		
	}

}
