package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class TopDeclVisitor extends SemanticsVisitor {

	private String name = null;

	public TopDeclVisitor(SymbolTable st) {
		super(st);
	}

	public boolean visit(ASTNode node) throws Exception {
		// TODO: Interface Declaration

		if (node instanceof PackageDeclaration) {
			PackageDeclaration PNode = (PackageDeclaration) node;
			name = PNode.getPackageName();
			st.setName(name);
		} else if (node instanceof ClassDeclaration) {
			// TODO: check if class has already been defined
			String tname = ((ClassDeclaration) node).getIdentifier();
			st.setName(st.getName() + "." + tname + "{}");
			// Adds the current symbol table to the static symbol table map
			st.addScope();
			st.addClass(st.getName() + "." + tname, node);
		} else if (node instanceof FieldDeclaration) {
			String key = this.st.getName() + "." + ((FieldDeclaration) node).getName().getName();
			if (!st.hasField(key))
				st.addField(key, node);
			else {
				System.err.println("TopDeclVisitor.visit(): Multiple Field Declarations with same name. Exiting with 42");
				System.exit(42);
			}
		} else if (node instanceof MethodDeclaration) {
			// TODO: check signatures

			String key = this.st.getName() + "." + ((MethodDeclaration) node).getName().getName();
			if (!st.hasMethod(key))
				st.addMethod(key, node);
		}

		return !(node instanceof BodyDeclaration);
	}

	@Override
	public void willVisit(ASTNode node) {
	}

	@Override
	public void didVisit(ASTNode node) {
	}

}
