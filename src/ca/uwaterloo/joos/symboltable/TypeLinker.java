package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.symboltable.SymbolTable.SymbolTableException;

public class TypeLinker extends SemanticsVisitor {
	
	public TypeLinker(SymbolTable table) {
		super(table);
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if(node instanceof ReferenceType) {
			ReferenceType refType = (ReferenceType) node;
			
			Scope currentScope = this.getCurrentScope();
			if(!currentScope.resolveReferenceType(refType, this.table)) {
				throw new SymbolTableException("Cannot resolve type " + refType.getName().getName());
			}
		}
		return true;
	}

}
