package ca.uwaterloo.joos.ast.type;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.scanner.Token;

public class PrimitiveType extends Type{
	public Token typeOfPrimitive;
	public PrimitiveType(Node primitiveNod, ASTNode parent) {
		super(parent);
	}
}
