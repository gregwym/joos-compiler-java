package ca.uwaterloo.joos.ast.expr.name;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.SimpleListDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.symboltable.TableEntry;
import ch.lambdaj.Lambda;

public class QualifiedName extends Name {

	public static final SimpleListDescriptor COMPONENTS = new SimpleListDescriptor(String.class);
	public final List<TableEntry> originalDeclarations = new ArrayList<TableEntry>();

	public QualifiedName(Node qualifiedName, ASTNode parent) throws Exception {
		super(qualifiedName, parent);
	}
	
	public QualifiedName(String typeName) throws Exception {
		super(null, null);
		String[] components = typeName.split("\\.");
		for(String component: components) {
			this.addChild(COMPONENTS, component);
		}
	}

	public String getQualifiedName() throws ChildTypeUnmatchException {
		return Lambda.join(this.getComponents(), ".");
	}
	
	@Override
	public String getName() throws Exception {
		return this.getQualifiedName();
	}
	
	@Override
	public String getSimpleName() throws Exception {
		List<String> components = this.getComponents();
		return components.get(components.size() - 1);
	}

	@SuppressWarnings("unchecked")
	public List<String> getComponents() throws ChildTypeUnmatchException {
		return (List<String>) this.getChildByDescriptor(COMPONENTS);
	}

	

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		if (leafNode.token.getKind().equals("ID")) {
			this.addChild(QualifiedName.COMPONENTS, leafNode.token.getLexeme());
		}
	}

}
