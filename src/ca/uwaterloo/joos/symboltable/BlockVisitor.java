package ca.uwaterloo.joos.symboltable;

import java.util.Stack;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;

public class BlockVisitor extends SemanticsVisitor {
	private int level = 0;
	private int blocks = 0;
	private int stackMin = 0;

	public BlockVisitor(Stack<SymbolTable> viewStack) {
		super();
		this.viewStack = viewStack;
		this.stackMin = viewStack.size(); 
	}
	
	public BlockVisitor(Stack<SymbolTable> viewStack, int level) {
		this(viewStack);
		this.level = level;
	}

	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception {
		SymbolTable currentScope = this.getCurrentScope();
		if (node instanceof VariableDeclaration) {
			// if name is not already in view
			VariableDeclaration LNode = (VariableDeclaration) node;

			if (currentScope.containVariableName(LNode)) {
				throw new Exception("Duplicate local variable declaration within overlapping scope");
			}
			currentScope.addVariableDecl(LNode, level);
			this.level++;

			return false;
		} else if (node instanceof Block) {
			if (blocks == 0)  {
				this.blocks++;
			}
			else {
				ASTVisitor blockVisitor = new BlockVisitor(this.viewStack, this.level);
				node.accept(blockVisitor);
				
				return false;
			}
		}

		return true;
	}

	@Override
	public void willVisit(ASTNode node) {
		if (blocks > 0 && node instanceof Block) {
			// Make a new symbol table which builds
			String name = this.getCurrentScope().getName() + "." + this.blocks + "Block";
			SymbolTable scope = SymbolTable.getScope(name);
			
			scope.appendScope(this.getCurrentScope());
			
			this.pushScope(scope);
			this.blocks++;
		}
	}

	@Override
	public void didVisit(ASTNode node) {
		if (node instanceof Block && this.viewStack.size() > this.stackMin) {
			this.popScope();
		}
	}

}
