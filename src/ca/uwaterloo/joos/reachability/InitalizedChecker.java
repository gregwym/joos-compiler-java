//Ensures each use of a local variable is absolutely declared

package ca.uwaterloo.joos.reachability;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;

public class InitalizedChecker extends SemanticsVisitor {
	String DeclName;
	List<String> decls;
	public InitalizedChecker(SymbolTable table, String currentDecl, List<String> idecls) {
		super(table);
		DeclName = currentDecl;
		decls = idecls;
	}
	
	@Override
	public void didVisit(ASTNode node) throws Exception{
		
		if (node instanceof SimpleName && 
				!(node.getParent() instanceof ReferenceType)){
			if (((SimpleName) node).getName().equals(DeclName)) throw new Exception ("Use in Initalization");
			else if (!decls.contains(((SimpleName) node).getName())){
				throw new Exception ("Use before declaration " + ((SimpleName) node).getName());
			}
		}		
	}

}
