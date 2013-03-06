package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class DeepDeclVisitor extends SemanticsVisitor {

	private String name = null;

	public DeepDeclVisitor(SymbolTable st) {
		super(st);
	}

	public void willVisit(ASTNode node) {

		if (node instanceof MethodDeclaration) {

			// Make a new symbol table which builds
			MethodDeclaration CNode = (MethodDeclaration) node;
			name = name + "." + CNode.getIdentifier();
			SymbolTable nst = new SymbolTable();
			nst.setName(name + "()");
			nst.openScope(nst.getName());
			 // Adds the new block symboltable to the global hash of tables
			nst.addScope();

			try {
				// visit the blocks within the method
				nst.build(new BlockVisitor(nst), node);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (node instanceof ClassDeclaration) {
			ClassDeclaration CNode = (ClassDeclaration) node;
			name = name + "." + CNode.getIdentifier() + "{}";
			st.setName(name);
		}
	}

	public void didVisit(ASTNode node) {
		if (node instanceof MethodDeclaration) {
			st.closeScope();
			name = name.substring(0, name.lastIndexOf("."));
		}
	}

	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception {
		if (node instanceof PackageDeclaration) {
			PackageDeclaration PNode = (PackageDeclaration) node;
			name = PNode.getPackageName();
			st.setName(name);
		}
		else if (node instanceof MethodDeclaration) {
			return false;
		}

		return true;
	}

}
