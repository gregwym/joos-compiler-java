package ca.uwaterloo.joos.ast.body;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser;

public class ClassBody extends TypeBody {

	public ClassBody(Node bodyNode, ASTNode parent) throws ASTConstructException {
		super(parent);
		assert bodyNode instanceof TreeNode : "ClassBody is expecting a TreeNode";

		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser(this) {

			public Set<Node> processTreeNode(TreeNode treeNode) throws ASTConstructException {
				Set<Node> offers = new HashSet<Node>();
				if (treeNode.productionRule.getLefthand().equals("classbodydecl")) {
					BodyDeclaration bodyDecl = BodyDeclaration.newBodyDeclaration(treeNode.children.get(0), parent);
					if(bodyDecl != null) members.add(bodyDecl);
				} else {
					for (Node n : treeNode.children)
						offers.add(n);
				}
				return offers;
			}

			public void processLeafNode(LeafNode leafNode) {}

		});

		traverse.traverse(bodyNode);
	}
}