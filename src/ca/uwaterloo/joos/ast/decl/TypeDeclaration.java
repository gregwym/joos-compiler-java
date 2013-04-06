package ca.uwaterloo.joos.ast.decl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.Modifiers;
import ca.uwaterloo.joos.ast.body.ClassBody;
import ca.uwaterloo.joos.ast.body.InterfaceBody;
import ca.uwaterloo.joos.ast.body.TypeBody;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.symboltable.Scope;

public abstract class TypeDeclaration extends ASTNode {

	protected static final ChildDescriptor MODIFIERS = new ChildDescriptor(Modifiers.class);
	protected static final ChildListDescriptor IMPLEMNTS = new ChildListDescriptor(ReferenceType.class);
	protected static final ChildDescriptor BODY = new ChildDescriptor(TypeBody.class);
	public String fullyQualifiedName;
	public boolean checked = false;
	public int totalFieldDeclarations = 0;
	public int totalMethodDeclarations = 0;
	Map<Integer, Scope> sigList = new HashMap<Integer,Scope>();	//Holds the scopes of the methods in this type
	
	public void setSignatures(Map<Integer, Scope> methodList){
		//Called in StaticLocalInit
		sigList.putAll(methodList);
	}
	
	public Map<Integer, Scope> getSignatures(){
		//called in CodeGenerator. The Signatures need to be converted 
		//with methodLabel
		//TODO static methods
		return sigList;
	}
	
	public TypeDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public Modifiers getModifiers() throws ChildTypeUnmatchException {
		return (Modifiers) this.getChildByDescriptor(TypeDeclaration.MODIFIERS);
	}

	@SuppressWarnings("unchecked")
	public List<ReferenceType> getInterfaces() throws ChildTypeUnmatchException {
		return (List<ReferenceType>) this.getChildByDescriptor(TypeDeclaration.IMPLEMNTS);
	}

	public TypeBody getBody() throws ChildTypeUnmatchException {
		return (TypeBody) this.getChildByDescriptor(TypeDeclaration.BODY);
	}
	

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.productionRule.getLefthand().equals("modifiers")) {
			Modifiers modifiers = new Modifiers(treeNode, this);
			addChild(MODIFIERS, modifiers);
		}
		else if (treeNode.productionRule.getLefthand().equals("classbody")) {
			ClassBody body = new ClassBody(treeNode, this);
			addChild(BODY, body);
		} else if (treeNode.productionRule.getLefthand().equals("interfacebody")) {
			InterfaceBody body = new InterfaceBody(treeNode, this);
			addChild(BODY, body);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		if (leafNode.token.getKind().equals("ID")) {
			setIdentifier(leafNode.token.getLexeme());
		}
	}
}
