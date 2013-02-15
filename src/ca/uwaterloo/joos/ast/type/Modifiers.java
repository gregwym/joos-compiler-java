package ca.uwaterloo.joos.ast.type;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.scanner.Token;

public class Modifiers extends ASTNode {
	List<Token> modifierList = new ArrayList<Token>();
	public Modifiers(Node modifiersNode)
	{
		if(modifiersNode instanceof TreeNode)
		{
			
			List<Node> modifierChildren = ((TreeNode)modifiersNode).children;
			searchNode(modifierChildren,"modifier");
		}
		
	}
	private void searchNode(List<Node> targetTree,String searchType)
	{
		
		for (Node child : targetTree) {
			if (child instanceof TreeNode) {
				TreeNode treeNode = (TreeNode) child;
				if (treeNode.productionRule.getLefthand().equals(
						searchType)) {
					modifierList.add(((LeafNode)treeNode.children.get(0)).token);
					return;
				}
				else{
					searchNode(treeNode.children,searchType);
				}
			}
		}
		return;
	}

	@Override
	public String toString(int level) {
		String str = super.toString(level);
		str += "<Modifiers> ";
		for(Token token: this.modifierList) 
			str += token.getKind() + " ";
		str += "\n";
		return str;
	}
}
