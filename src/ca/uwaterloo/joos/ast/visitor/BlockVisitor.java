package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class BlockVisitor extends SemanticsVisitor {
	private int level = -1;
	private int blocks = -1;

	public BlockVisitor(SymbolTable scope) {
		super();
		this.pushScope(scope);
	}

	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception {
		SymbolTable currentScope = this.getCurrentScope();
		if (node instanceof LocalVariableDeclaration) {
			// if name is not already in view
			LocalVariableDeclaration LNode = (LocalVariableDeclaration) node;

			if (currentScope.getVariableDecl(LNode) != null) {
				throw new Exception("Overlapping Declarations Exit 42");
			}
			level++;
			currentScope.addVariableDecl(LNode, level);
			return false;
		} else if (node instanceof Block) {
			if (blocks == -1) { // Then this is the block we are reading
				blocks++;
			} else {
//				// Level is 0 so we found a nested block
//				// TODO
//				// Make a new scope
//				// Add it to the Scopes
//				SymbolTable nst = new SymbolTable();
//				blocks++;
//				// Add new symbol table to the global scope hash
//				nst.addScope();
//				// Add the LOCAL SCOPE to the nested block
//				List<String> tmp = nst.appendScope(st, blocks, this.level);
//				nst.setName(st.getName() + "." + blocks + "Block");
//				nst.build(new BlockVisitor(nst), node);
//				nst.unAppendScope(blocks, tmp);
				return false;
			}
		}

		return true;
	}

	@Override
	public void willVisit(ASTNode node) {

	}

	@Override
	public void didVisit(ASTNode node) {

	}

}
