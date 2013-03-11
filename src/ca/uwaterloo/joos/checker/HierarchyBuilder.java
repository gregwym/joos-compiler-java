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
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.ast.visitor.TypeDeclVisitor;
import ca.uwaterloo.joos.symboltable.Scope;
import ca.uwaterloo.joos.symboltable.SymbolTable;

public class HierarchyBuilder extends TypeDeclVisitor {

	
	private static Map<String, String> hierarchyMap = new HashMap<String,String>();
	private static Map<String, List<String>> implementMap = new HashMap<String, List<String>>();
	private static Set<String> interfaces = new HashSet<String>();
	private static Set<String> classes = new HashSet<String>();
	private static Set<String> finals = new HashSet<String>();

	@Override
	protected void visitClassDecl(TypeDeclaration node) throws Exception {
		//System.out.println("visiting @@@@"+node.getIdentifier()+node.getModifiers().getModifiers()+node.getModifiers().getModifiers().contains(Modifier.FINAL));
		if (node.getModifiers().getModifiers().contains(Modifiers.Modifier.FINAL)) {
			//System.out.println("this is final !!!!!"+node.getIdentifier());
			finals.add(node.fullyQualifiedName);
		}
		if (node instanceof ClassDeclaration) {
			
			classes.add(node.fullyQualifiedName);
			if (((ClassDeclaration) node).getSuperClass()!= null) {
				//System.out.println("getHierarchyMap supers@@@@"+((ClassDeclaration) node).getSuperClass());
				ReferenceType EXTEND = (ReferenceType) ((ClassDeclaration) node).getSuperClass();
				String extendClasses = EXTEND.fullyQualifedTypeName;
				//System.out.println("getHierarchyMap!!!!!@@@@"+node.getIdentifier()+ extendClasses);
				hierarchyMap.put(node.fullyQualifiedName, extendClasses);
				System.out.println("add"+node.fullyQualifiedName+" extend"+extendClasses);
			}
		} else {
			interfaces.add(node.fullyQualifiedName);
			System.out.println("add interfaces" +node.fullyQualifiedName);
		}
		if (node.getInterfaces().size()!=0) {
			List<ReferenceType> IMPLENTS = node.getInterfaces();
			List<String> implentClasses = new ArrayList<String>();
			for (ReferenceType implent : IMPLENTS) {
				implentClasses.add(implent.fullyQualifedTypeName);
			}
			implementMap.put(node.fullyQualifiedName, implentClasses);
			System.out.println("add"+node.fullyQualifiedName+" implent"+implentClasses);
		}

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didVisit(ASTNode node) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
