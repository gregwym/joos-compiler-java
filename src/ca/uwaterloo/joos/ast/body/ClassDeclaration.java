package ca.uwaterloo.joos.ast.body;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.type.ClassName;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.scanner.Token;

public class ClassDeclaration extends ASTNode{
	// private List<TypeParameter> Parameters;

	private List<ClassName> extendClassList;
	private List<ClassName> implementClassList;
	// private Token classKeyword;
	private Token classID;
	private Modifiers modifiers;
	private ClassBody classBody;

	public ClassDeclaration(Node ClassDeclarRoot) {
		if (ClassDeclarRoot instanceof TreeNode) {
			List<Node> childrenFileNode = ((TreeNode) ClassDeclarRoot).children;
			for (Node child : childrenFileNode) {
				if (child instanceof TreeNode) {

					TreeNode childTreeNode = (TreeNode) child;
					if (childTreeNode.productionRule.getLefthand().equals(
							"modifiers")) {
						System.out.println("here+modifiers");
						modifiers = new Modifiers(childTreeNode);
					}

					if (childTreeNode.productionRule.getLefthand().equals(
							"classbody")) {
						classBody = new ClassBody(childTreeNode);
					}
				}
				else{
					
				}
			}
		}
	}

}
