package ca.uwaterloo.joos.ast.decl;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class InterfaceDeclaration extends TypeDeclaration {

	public InterfaceDeclaration(Node node) throws ASTConstructException {
		assert node instanceof TreeNode : "FileUnit is expecting a TreeNode";
		TreeNode treeNode = (TreeNode) node;

		for (Node oneChild : treeNode.children) {
			if(oneChild instanceof LeafNode) {	// TODO: not ideal, need a name class
				LeafNode child = (LeafNode) oneChild;
				if(child.token.getKind().equals("ID")) {
					this.identifier = child.token.getLexeme();
				}
				continue;
			}
			
			TreeNode child = (TreeNode) oneChild;
			if (child.productionRule.getLefthand().equals("modifiers")) {
				this.modifiers = new Modifiers(child);
			}
			else if (child.productionRule.getLefthand().equals("interfacebody")) {
//				this.body = new InterfaceBody(child);
			}
		}
	}
	
	@Override
	public String toString(int level) {
		String str = super.toString(level);
		str += "<InterfaceDecl>";
		str += " extends: ";
//		for(String id: this.interfaces)
//			str += id + " ";
		str += "\n";
		str += this.modifiers.toString(level + 1);
//		str += this.body.toString(level + 1);
		return str;
	}
}

