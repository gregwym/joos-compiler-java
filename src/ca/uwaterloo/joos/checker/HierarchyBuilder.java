package ca.uwaterloo.joos.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.Modifiers;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.InterfaceDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.ast.visitor.TypeDeclVisitor;
import ca.uwaterloo.joos.symboltable.Scope;
import ca.uwaterloo.joos.symboltable.SymbolTable;

public class HierarchyBuilder extends TypeDeclVisitor {

	public HierarchyBuilder(SymbolTable table) {
		super(table);
	}

	private Map<String, String> hierarchyMap = new HashMap<String, String>();
	private Map<String, List<String>> implementMap = new HashMap<String, List<String>>();
	private Map<String, Scope> methodMap = new HashMap<String, Scope>();
	private Set<String> interfaces = new HashSet<String>();
	private Set<String> classes = new HashSet<String>();
	private Set<String> finals = new HashSet<String>();

	@Override
	protected void visitClassDecl(TypeDeclaration node) throws Exception {
		if (!node.fullyQualifiedName.equals("java.lang.Object")) {
			hierarchyMap.put(node.fullyQualifiedName, "java.lang.Object");
		}
		
		methodMap.put(node.fullyQualifiedName, this.table.getType(node.fullyQualifiedName));
		if (node.getModifiers().getModifiers().contains(Modifiers.Modifier.FINAL)) {

			finals.add(node.fullyQualifiedName);

		}
		if (node instanceof ClassDeclaration) {

			classes.add(node.fullyQualifiedName);
			if (((ClassDeclaration) node).getSuperClass() != null) {

				ReferenceType EXTEND = (ReferenceType) ((ClassDeclaration) node).getSuperClass();
				String extendClasses = EXTEND.getFullyQualifiedName();

				hierarchyMap.put(node.fullyQualifiedName, extendClasses);

			}
			if (node.getInterfaces().size() != 0) {
				List<ReferenceType> IMPLENTS = node.getInterfaces();
				List<String> implentClasses = new ArrayList<String>();
				for (ReferenceType implent : IMPLENTS) {
					if (implentClasses.contains(implent.getFullyQualifiedName())) {
						throw new Exception("An interface must not be mentioned more than once in the same implements clause of a class");
					}
					implentClasses.add(implent.getFullyQualifiedName());
				}
				implementMap.put(node.fullyQualifiedName, implentClasses);

			}
		} else {
			interfaces.add(node.fullyQualifiedName);
			if (((InterfaceDeclaration) node).getInterfaces() != null) {
				List<ReferenceType> EXTEND = ((InterfaceDeclaration) node).getInterfaces();
				for (ReferenceType EXTENDNAME : EXTEND) {
					String extendClasses = EXTENDNAME.getFullyQualifiedName();
					hierarchyMap.put(node.fullyQualifiedName, extendClasses);

				}

			}

		}

	}

	public Map<String, Scope> getMethodMap() {
		return methodMap;
	}

	public Map<String, String> getHierarchyMap() {
		return hierarchyMap;
	}

	public Map<String, List<String>> getImplementMap() {
		return implementMap;
	}

	public Set<String> getClasses() {
		return classes;
	}

	public Set<String> getInterfaces() {
		return interfaces;
	}

	public Set<String> getFinals() {
		return finals;
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {

	}

	@Override
	public void didVisit(ASTNode node) {

	}

}
