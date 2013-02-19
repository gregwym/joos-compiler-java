package ca.uwaterloo.joos.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.ImportDeclaration;
import ca.uwaterloo.joos.ast.decl.InterfaceDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class FileUnit extends ASTNode {

	public FileUnit(Node node, String fileName, ASTNode parent) throws ASTConstructException {
		super(parent);
		
		assert node instanceof TreeNode : "FileUnit is expecting a TreeNode";
		
		TreeNode treeNode = (TreeNode) node;
		this.identifier = fileName;

		for (Node oneChild : treeNode.children) {
			TreeNode child = (TreeNode) oneChild;
			if (child.productionRule.getLefthand().equals("packagedecl")) {
				 //this.packageDeclaration = new PackageDeclaration(child);
				PackageDeclaration packageDeclaration = new PackageDeclaration(child,this);
				childrenList.put(new SimpleDescriptor("packagedecl"),packageDeclaration);
			}
			else if (child.productionRule.getLefthand().equals("importdecls")) {
				List<ImportDeclaration> importDeclarations = new ArrayList<ImportDeclaration>(); 
				ImportDeclaration importDeclaration = new ImportDeclaration(child,this);
				importDeclarations.add(importDeclaration);
				childrenList.put(new ListDescriptor("importdecls"),importDeclarations);
//				this.importDeclarations.add(new ImportDeclaration());
			}
			else if (child.productionRule.getLefthand().equals("typedecl")) {
				List<String> members = Arrays.asList(child.productionRule.getRighthand());
				
				if(members.contains("classdecl")) {
					ClassDeclaration classDeclaration = new ClassDeclaration(child.children.get(0),this);
					childrenList.put(new ChildDescriptor("classdecl"),classDeclaration);
				}
				else if(members.contains("interfacedecl")) {
					InterfaceDeclaration interfaceDeclaration = new InterfaceDeclaration(child.children.get(0),this);
					childrenList.put(new ChildDescriptor("interfacedecl"),interfaceDeclaration);
				}
			}
		}
	}

	
	//@Override
	/*public String toString(int level) {
		String str = super.toString(level);
		str += "\n";
//		str += this.packageDeclaration.toString(level + 1);
//		for(ImportDeclaration importDecl: this.importDeclarations)
//			str += importDecl.toString(level + 1);
		str += this.typeDeclaration.toString(level + 1);
		return str;
	}*/

	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.ASTNode#accept(ca.uwaterloo.joos.ast.ASTVisitor)
	 */
	@Override
	public void accept(ASTVisitor visitor) throws Exception{
		visitor.willVisit(this);
		if(visitor.visit(this)) {
//			this.packageDeclaration.accept(visitor);
//			for(ImportDeclaration importDecl: this.importDeclarations)
//				importDecl.accept(visitor);
			//this.typeDeclaration.accept(visitor);
		}
		visitor.didVisit(this);
	}
}
