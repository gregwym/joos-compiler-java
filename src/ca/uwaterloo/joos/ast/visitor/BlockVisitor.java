package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class BlockVisitor extends SemanticsVisitor {
	private int level = 0;
	private int blocks = 0;

	public BlockVisitor(SymbolTable scope) {
		super();
		this.pushScope(scope);
	}

	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception {
		SymbolTable currentScope = this.getCurrentScope();
		if (node instanceof VariableDeclaration) {
			// if name is not already in view
			VariableDeclaration LNode = (VariableDeclaration) node;

			if (currentScope.getVariableDecl(LNode) != null) {
				throw new Exception("Overlapping Declarations Exit 42");
			}
			currentScope.addVariableDecl(LNode, level);
			this.level++;

			return false;
		} else if (node instanceof Block) {
			if (blocks == 0)  {
				this.blocks++;
			}
			else {
				ASTVisitor blockVisitor = new BlockVisitor(this.getCurrentScope());
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
			this.pushScope(scope);
			this.blocks++;
		}
	}

	@Override
	public void didVisit(ASTNode node) {
		if (node instanceof Block) {
			this.popScope();
		}
	}

}
