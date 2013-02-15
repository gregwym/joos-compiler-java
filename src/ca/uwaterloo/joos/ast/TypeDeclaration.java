package ca.uwaterloo.joos.ast;

import java.util.List;

import ca.uwaterloo.joos.ast.body.ClassDeclaration;
import ca.uwaterloo.joos.ast.body.InterfaceDeclaration;
import ca.uwaterloo.joos.ast.body.MethodDeclaration;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class TypeDeclaration extends ASTNode {
		
		private String type;
		
		private ClassDeclaration classDeclaration;
		
		private InterfaceDeclaration interfaceDeclaration;
		
		public TypeDeclaration(Node TypeRoot)
		{
			if(TypeRoot instanceof TreeNode)
			{
				List<Node> childrenFileNode  = ((TreeNode) TypeRoot).children;
			    for(Node child :childrenFileNode)
			    {
			    	TreeNode childTreeNode = (TreeNode)child;
			    	if(childTreeNode.productionRule.getLefthand().equals("classdecl")){
			    		classDeclaration = new ClassDeclaration(childTreeNode);
			    	}
			    	
			    	if(childTreeNode.productionRule.getLefthand().equals("interfacedecl")){
			    		interfaceDeclaration = new InterfaceDeclaration(childTreeNode);
			    	}
			    }
			}
		}
	
}
