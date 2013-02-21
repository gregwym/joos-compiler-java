package ca.uwaterloo.joos.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.ImportDeclaration;
import ca.uwaterloo.joos.ast.decl.InterfaceDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class FileUnit extends ASTNode {
	protected static final ChildDescriptor PACKAGE = new ChildDescriptor(PackageDeclaration.class);
	protected static final ChildListDescriptor IMPORTS = new ChildListDescriptor(ImportDeclaration.class);
	protected static final ChildDescriptor TYPE = new ChildDescriptor(TypeDeclaration.class);

	public FileUnit(Node node, String fileName, ASTNode parent) throws Exception {
		super(node, parent);
		this.setIdentifier(fileName);
	}

	public PackageDeclaration getPackageDeclaration() throws ChildTypeUnmatchException{
		return (PackageDeclaration) this.getChildByDescriptor(FileUnit.PACKAGE);
	}

	@SuppressWarnings("unchecked")
	public List<ImportDeclaration> getImportDeclarations() throws ChildTypeUnmatchException{
		return (List<ImportDeclaration>) this.getChildByDescriptor(FileUnit.IMPORTS);
	}

	public TypeDeclaration getTypeDeclaration() throws ChildTypeUnmatchException{
		return (TypeDeclaration) this.getChildByDescriptor(FileUnit.TYPE);
	}

	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		Set<Node> offers = new HashSet<Node>();
		if (treeNode.productionRule.getLefthand().equals("packagedecl")) {
			PackageDeclaration packageDeclaration = new PackageDeclaration(treeNode, this);
			addChild(PACKAGE, packageDeclaration);
			logger.fine("Added PackageDecl: " + packageDeclaration);
		} else if (treeNode.productionRule.getLefthand().equals("importdecls")) {
			List<ImportDeclaration> imports = getImportDeclarations();
			if(imports == null) {
				imports = new ArrayList<ImportDeclaration>();
				addChild(IMPORTS, imports);
			}
			ImportDeclaration importDeclaration = new ImportDeclaration(treeNode, this);
			imports.add(importDeclaration);
			logger.fine("Added ImportDecl: " + importDeclaration);
		} else if (treeNode.productionRule.getLefthand().equals("classdecl")) {
			ClassDeclaration classDeclaration = new ClassDeclaration(treeNode, this);
			addChild(TYPE, classDeclaration);
			logger.fine("Added ClassDecl: " + classDeclaration);
		} else if (treeNode.productionRule.getLefthand().equals("interfacedecl")) {
			InterfaceDeclaration interfaceDeclaration = new InterfaceDeclaration(treeNode, this);
			addChild(TYPE, interfaceDeclaration);
			logger.fine("Added InterfaceDecl: " + interfaceDeclaration);
		}
		else {
			offers.addAll(treeNode.children);
		}
		return offers;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
	}
}
