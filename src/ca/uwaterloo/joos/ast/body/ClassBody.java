package ca.uwaterloo.joos.ast.body;

import java.util.LinkedList;
import java.util.Queue;

import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ClassBody extends TypeBody {
	public ClassBody(Node bodyNode) {
		assert bodyNode instanceof TreeNode : "ClassBody is expecting a TreeNode";

		Queue<Node> nodeQueue = new LinkedList<Node>();
		nodeQueue.offer(bodyNode);

		while (!nodeQueue.isEmpty()) {
			Node node = nodeQueue.poll();
			logger.finer("Dequeued node " + node.toString());

			if (node instanceof TreeNode) {
				TreeNode treeNode = (TreeNode) node;
				if (treeNode.productionRule.getLefthand().equals(
						"classbodydecl")) {
					logger.fine("Reach: " + treeNode);
					BodyDeclaration bodyDecl = BodyDeclaration.newBodyDeclaration(treeNode.children.get(0));
					if(bodyDecl != null) this.members.add(bodyDecl);
				} else {
					for (Node n : treeNode.children) {
						nodeQueue.offer(n);
					}
				}
			}
		}
	}
}
