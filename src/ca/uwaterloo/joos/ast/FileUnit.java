package ca.uwaterloo.joos.ast;

import java.util.Arrays;
import java.util.List;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.body.ClassDeclaration;
import ca.uwaterloo.joos.ast.body.InterfaceDeclaration;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class FileUnit extends ASTNode {

	private List<ImportDeclaration> importDeclarations;
	private PackageDeclaration packageDeclaration;
	private TypeDeclaration typeDeclaration;

	public FileUnit(Node node, String fileName) throws ASTConstructException {
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
					this.typeDeclaration = new ClassDeclaration(child.children.get(0));
				}
				else if(members.contains("interfacedecl")) {
					this.typeDeclaration = new InterfaceDeclaration(child.children.get(0));
				}
				this.typeDeclaration.parent = this;
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
		str += "<FileUnit> filename: " + this.identifier + "\n";
//		str += this.packageDeclaration.toString(level + 1);
//		for(ImportDeclaration importDecl: this.importDeclarations)
//			str += importDecl.toString(level + 1);
		str += this.typeDeclaration.toString(level + 1);
		return str;
	}
}
