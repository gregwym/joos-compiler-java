package ca.uwaterloo.joos.ast.visitor;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.body.ClassBody;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public abstract class SemanticsVisitor extends ASTVisitor {
	// SYMBOLTABLE - Link to the global SymbolTable
	
	protected static Logger logger = Main.getLogger(SemanticsVisitor.class);
	private Stack<SymbolTable> viewStack;

	public SemanticsVisitor() {
		this.viewStack = new Stack<SymbolTable>();
		logger.setLevel(Level.FINER);
	}
	
	protected SymbolTable getCurrentScope() {
		return this.viewStack.peek();
	}
	
	protected void pushScope(SymbolTable table) {
		logger.finer("Pushing scope " + table.toString());
		this.viewStack.push(table);
	}
	
	protected SymbolTable popScope() {
		SymbolTable scope = this.viewStack.pop();
		logger.finer("Popping scope " + scope.toString());
		return scope;
	}

	@Override
	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception {
		// Processes a single node in the AST tree

		if (node instanceof ClassBody) {
			System.out.println("SemanticsVisitor.visit: Class Body found!");
		} else if (node instanceof VariableDeclaration) {
			System.out.println("SemanticsVisitor.visit: VarDecl Found!");
		} else if (node instanceof Block) {
			System.out.println("SemanticsVisitor.visit(): Block: " + node.toString());
		}

		return true;
	}
}
