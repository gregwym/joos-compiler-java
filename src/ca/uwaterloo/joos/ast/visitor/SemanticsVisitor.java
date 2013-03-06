package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.body.ClassBody;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public abstract class SemanticsVisitor extends ASTVisitor {
	// SYMBOLTABLE - Link to the global SymbolTable
	protected SymbolTable st = null;

	public SemanticsVisitor(SymbolTable st) {
		this.st = st;
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
