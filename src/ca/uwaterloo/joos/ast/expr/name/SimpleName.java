package ca.uwaterloo.joos.ast.expr.name;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.SimpleDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public class SimpleName extends Name {
	
	public static final SimpleDescriptor NAME = new SimpleDescriptor(String.class); 

	public SimpleName(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	public SimpleName(String name) throws Exception {
		super(null, null);
		this.addChild(NAME, name);
		this.setIdentifier(name);
	}

	@Override
	public String getName() throws ChildTypeUnmatchException {
		return (String) this.getChildByDescriptor(NAME);
	}
	
	@Override
	public String getSimpleName() throws Exception {
		return this.getName();
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		this.addChild(NAME, leafNode.token.getLexeme());
	}

}
