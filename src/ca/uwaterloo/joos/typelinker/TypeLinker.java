package ca.uwaterloo.joos.typelinker;

import java.util.Stack;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.symboltable.Scope;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;

public class TypeLinker extends SemanticsVisitor {
	
	protected Stack<Scope> viewStack;
	protected Stack<Integer> blocks;
	protected SymbolTable table;

	public TypeLinker(SymbolTable table) {
		super(table);
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if(node instanceof ReferenceType) {
			ReferenceType refType = (ReferenceType) node;
			logger.info("Visiting Type: " + refType + " Scoping " + this.getCurrentScope());
		}
		return true;
	}

}
