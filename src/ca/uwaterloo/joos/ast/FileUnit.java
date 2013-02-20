package ca.uwaterloo.joos.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.ImportDeclaration;
import ca.uwaterloo.joos.ast.decl.InterfaceDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;
import ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser;

public class FileUnit extends ASTNode {
	protected static final ChildDescriptor PACKAGE = new ChildDescriptor(PackageDeclaration.class);
	protected static final ChildListDescriptor IMPORTS = new ChildListDescriptor(ImportDeclaration.class);
	protected static final ChildDescriptor TYPE = new ChildDescriptor(TypeDeclaration.class);

	public FileUnit(Node node, String fileName, ASTNode parent) throws ASTConstructException {
		super(parent);
		this.identifier = fileName;

		assert node instanceof TreeNode : "FileUnit is expecting a TreeNode";
		
		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser(this) {
			
			public Set<Node> processTreeNode(TreeNode treeNode) throws ASTConstructException {
				Set<Node> offers = new HashSet<Node>();
				if (treeNode.productionRule.getLefthand().equals("packagedecl")) {
					PackageDeclaration packageDeclaration = new PackageDeclaration(treeNode, parent);
					childrenList.put(PACKAGE, packageDeclaration);
					logger.fine("Added PackageDecl: " + packageDeclaration);
				} else if (treeNode.productionRule.getLefthand().equals("importdecls")) {
					List<ASTNode> imports = getImportDeclarations();
					if(imports == null) {
						imports = new ArrayList<ASTNode>();
						childrenList.put(IMPORTS, imports);
					}
					ImportDeclaration importDeclaration = new ImportDeclaration(treeNode, parent);
					imports.add(importDeclaration);
					logger.fine("Added ImportDecl: " + importDeclaration);
				} else if (treeNode.productionRule.getLefthand().equals("classdecl")) {
					ClassDeclaration classDeclaration = new ClassDeclaration(treeNode, parent);
					childrenList.put(TYPE, classDeclaration);
					logger.fine("Added ClassDecl: " + classDeclaration);
				} else if (treeNode.productionRule.getLefthand().equals("interfacedecl")) {
					InterfaceDeclaration interfaceDeclaration = new InterfaceDeclaration(treeNode, parent);
					childrenList.put(TYPE, interfaceDeclaration);
					logger.fine("Added InterfaceDecl: " + interfaceDeclaration);
				}
				else {
					for (Node n : treeNode.children) 
						offers.add(n);
				}		
				return offers;
			}
	
			public void processLeafNode(LeafNode leafNode) throws ASTConstructException {
			}
		});
		
		traverse.traverse(node);
	}
	
	public PackageDeclaration getPackageDeclaration() throws ChildTypeUnmatchException{
		return (PackageDeclaration) this.getChildByDescriptor(FileUnit.PACKAGE);
	}
	
	public List<ASTNode> getImportDeclarations() throws ChildTypeUnmatchException{
		return this.getChildByDescriptor(FileUnit.IMPORTS);
	}
	
	public TypeDeclaration getTypeDeclaration() throws ChildTypeUnmatchException{
		return (TypeDeclaration) this.getChildByDescriptor(FileUnit.TYPE);
	}
	
	@Override
	public String toString(int level) {
		String str = super.toString(level);
		for (Descriptor key : this.childrenList.keySet()) {
			if (key instanceof ChildListDescriptor) {
				try {
					List<ASTNode> children = this.getChildByDescriptor((ChildListDescriptor) key);
					for (ASTNode child : children) {
						System.out.println(child.toString());
						str += child.toString(level + 1);
					}
				} catch (ChildTypeUnmatchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(key instanceof ChildDescriptor) {
				try {
					str += this.getChildByDescriptor((ChildDescriptor) key).toString(level + 1);
				} catch (ChildTypeUnmatchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return str;
	}
}
