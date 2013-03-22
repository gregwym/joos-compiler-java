//Ensures each use of a local variable is absolutely declared

package ca.uwaterloo.joos.reachability;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;

public class InitalizedChecker extends ASTVisitor {
	String declName;
	List<String> decls;
	public InitalizedChecker(String currentDecl, List<String> decls) {
		declName = currentDecl;
		this.decls = new ArrayList<String>(decls);
	}
	
	@Override
	public void didVisit(ASTNode node) throws Exception{
		
		if (node instanceof SimpleName && 
				!(node.getParent() instanceof ReferenceType)){
			if (((SimpleName) node).getName().equals(declName)) throw new Exception ("Use in Initalization");
			else if (!decls.contains(((SimpleName) node).getName())){
				throw new Exception ("Use before declaration " + ((SimpleName) node).getName());
			}
		}		
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {
		
	}

}
