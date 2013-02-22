package ca.uwaterloo.joos.ast.expr;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public class InfixExpression extends Expression {

	public InfixExpression(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		// TODO Auto-generated constructor stub
	}
	
	public static Set<String> getAcceptingKinds() {
		Set<String> acceptingKinds = new HashSet<String>();

		acceptingKinds.add("corexpr");
		acceptingKinds.add("candexpr");
		acceptingKinds.add("inorexpr");
		acceptingKinds.add("andexpr");
		acceptingKinds.add("equalexpr");
		acceptingKinds.add("relationexpr");
		acceptingKinds.add("addiexpr");
		acceptingKinds.add("multiexpr");
		
		return acceptingKinds;
	}

}
