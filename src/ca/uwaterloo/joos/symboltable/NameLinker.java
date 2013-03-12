package ca.uwaterloo.joos.symboltable;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.expr.name.Name;

public class NameLinker extends SemanticsVisitor {
	
	public Set<Class<?>> nameUsageParentClasses;

	public NameLinker(SymbolTable table) {
		super(table);
		this.nameUsageParentClasses = new HashSet<Class<?>>();
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if(node instanceof Name) {
			((Name) node).originalDeclaration = this.getCurrentScope().resolveVariableToDecl((Name) node);
			if(((Name) node).originalDeclaration != null) {
//				System.out.println("Name " + ((Name) node).getName() + " matches with decl " + ((Name) node).originalDeclaration + " with parent " + node.getParent());
//				System.out.println(node.getParent().getClass().getName());
				this.nameUsageParentClasses.add(node.getParent().getClass());
			}
		}
		return super.visit(node);
	}
}
