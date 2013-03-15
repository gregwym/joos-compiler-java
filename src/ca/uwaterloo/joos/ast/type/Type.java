package ca.uwaterloo.joos.ast.type;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public abstract class Type extends ASTNode {

	public Type(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public abstract String getFullyQualifiedName() throws Exception;

	public boolean equals(Type type) throws Exception {
		if(!(type instanceof PrimitiveType) && !(this instanceof PrimitiveType) && 
				(this.getFullyQualifiedName().equals("__NULL__") || 
						(type.getFullyQualifiedName().equals("__NULL__")))) {
			return true;
		}
		boolean result = this.getFullyQualifiedName().equals(type.getFullyQualifiedName());
		return result;
	}
}
