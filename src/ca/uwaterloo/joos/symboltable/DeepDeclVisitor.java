package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.ast.type.ArrayType;
import ca.uwaterloo.joos.ast.type.Type;

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
	
	@Override
	public void didVisit(ASTNode node) throws Exception {
		super.didVisit(node);
		
		if(node instanceof FieldDeclaration || node instanceof MethodDeclaration) {
			Type type = ((BodyDeclaration) node).getType();
			if(type == null) {
				return;
			}
			
			TypeScope enclosingScope = (TypeScope) this.getCurrentScope();
			TableEntry entry = enclosingScope.getTableEntry(node);
			entry.setType(type);
			
			if(type instanceof ArrayType) {
				type = ((ArrayType) type).getType();
			}
			TypeScope typeScope = this.table.getType(type.getFullyQualifiedName());
			entry.setTypeScope(typeScope);
		} else if(node instanceof VariableDeclaration) {
			Type type = ((BodyDeclaration) node).getType();
			if(type == null) {
				return;
			}
			
			BlockScope enclosingScope = (BlockScope) this.getCurrentScope();
			TableEntry entry = enclosingScope.getVariableDecl((VariableDeclaration) node);
			entry.setType(type);
			
			if(type instanceof ArrayType) {
				type = ((ArrayType) type).getType();
			}
			TypeScope typeScope = this.table.getType(type.getFullyQualifiedName());
			entry.setTypeScope(typeScope);
		}
	}
}
