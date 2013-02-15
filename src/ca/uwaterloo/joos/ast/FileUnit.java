package ca.uwaterloo.joos.ast;

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

	private List<ImportDeclaration> importDeclarations;
	private PackageDeclaration packageDeclaration;
	private TypeDeclaration typeDeclaration;

	public FileUnit(Node node, String fileName, ASTNode parent) throws ASTConstructException {
		super(parent);
		
		assert node instanceof TreeNode : "FileUnit is expecting a TreeNode";
		
		TreeNode treeNode = (TreeNode) node;
		this.identifier = fileName;

		for (Node oneChild : treeNode.children) {
			TreeNode child = (TreeNode) oneChild;
			if (child.productionRule.getLefthand().equals("packagedecl")) {
//				 this.packageDeclaration = new PackageDeclaration(child);
			}
			else if (child.productionRule.getLefthand().equals("importdecls")) {
//				this.importDeclarations.add(new ImportDeclaration());
			}
			else if (child.productionRule.getLefthand().equals("typedecl")) {
				List<String> members = Arrays.asList(child.productionRule.getRighthand());
				
				if(members.contains("classdecl")) {
					this.typeDeclaration = new ClassDeclaration(child.children.get(0), this);
				}
				else if(members.contains("interfacedecl")) {
					this.typeDeclaration = new InterfaceDeclaration(child.children.get(0), this);
				}
			}
		}
	}
	
	/**
	 * @return the importDeclarations
	 */
	public List<ImportDeclaration> getImportDeclarations() {
		return importDeclarations;
	}

	/**
	 * @return the packageDeclaration
	 */
	public PackageDeclaration getPackageDeclaration() {
		return packageDeclaration;
	}

	/**
	 * @return the typeDeclaration
	 */
	public TypeDeclaration getTypeDeclaration() {
		return typeDeclaration;
	}
	
	@Override
	public String toString(int level) {
		String str = super.toString(level);
		str += "\n";
//		str += this.packageDeclaration.toString(level + 1);
//		for(ImportDeclaration importDecl: this.importDeclarations)
//			str += importDecl.toString(level + 1);
		str += this.typeDeclaration.toString(level + 1);
		return str;
	}

	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.ASTNode#accept(ca.uwaterloo.joos.ast.ASTVisitor)
	 */
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.willVisit(this);
		if(visitor.visit(this)) {
			this.packageDeclaration.accept(visitor);
			for(ImportDeclaration importDecl: this.importDeclarations)
				importDecl.accept(visitor);
			this.typeDeclaration.accept(visitor);
		}
		visitor.didVisit(this);
	}
}
