package ca.uwaterloo.joos.ast;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class FileNode extends ASTNode {
	private List<ImportDeclaration> importDeclarations;
	private PackageDeclaration packageDeclaration;
	private TypeDeclaration typeDeclaration;
	
	public FileNode(Node astFileNode)
	{
		if(astFileNode instanceof TreeNode)
		{
			List<Node> childrenFileNode  = ((TreeNode) astFileNode).children;
		    for(Node child :childrenFileNode)
		    {
		    	TreeNode childTreeNode = (TreeNode)child;
		    	if(childTreeNode.productionRule.getLefthand().equals("importdecls")){
		    		//importDeclarations.add(new ImportDeclaration());
		    	}
		    	if(childTreeNode.productionRule.getLefthand().equals("packagedecl")){
		    		packageDeclaration = new PackageDeclaration(childTreeNode);
		    	}
		    	if(childTreeNode.productionRule.getLefthand().equals("typedecl")){
		    		typeDeclaration = new TypeDeclaration(childTreeNode);
		    	}
		    }
		
		
		
		}
		
	}
	
	public void traverseChildren(TreeNode root, List<Object> list, Class<?> type) {
		for(;; root = (TreeNode) root.children.get(0)) {
			//list.add(type.newInstance());
			
		}
	}
}
