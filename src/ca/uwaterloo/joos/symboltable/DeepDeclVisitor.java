package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.statement.Block;

public class DeepDeclVisitor extends SemanticsVisitor {
	
	public DeepDeclVisitor(SymbolTable table) {
		super(table);
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {

		if (node instanceof MethodDeclaration) {
			TypeScope currentScope = (TypeScope) this.getCurrentScope();
			String name = currentScope.signatureOfMethod((MethodDeclaration) node);
			Scope scope = this.table.addBlock(name, currentScope, node);
			
			this.blockCount = 0;
			this.pushScope(scope);
		} else if (node instanceof Block || node instanceof LocalVariableDeclaration) {
			BlockScope currentScope = (BlockScope) this.getCurrentScope();
			String name = currentScope.getName() + ".block" + this.blockCount;
			Scope scope = this.table.addBlock(name, currentScope, node);
			
			this.blockCount++;
			this.pushScope(scope);
		} else {
			super.willVisit(node);
		}
		
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if (node instanceof FieldDeclaration) {
			
		}
		else if (node instanceof VariableDeclaration) {
			VariableDeclaration varDecl = (VariableDeclaration) node;
			BlockScope currentScope = (BlockScope) this.getCurrentScope();
			currentScope.addVariableDecl(varDecl);
		}

		return !(node instanceof VariableDeclaration);
	}
}
