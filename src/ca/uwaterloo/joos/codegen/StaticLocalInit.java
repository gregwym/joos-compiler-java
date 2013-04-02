package ca.uwaterloo.joos.codegen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.Modifiers.Modifier;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.checker.HierarchyChecker;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;
import ca.uwaterloo.joos.symboltable.TypeScope;

public class StaticLocalInit extends SemanticsVisitor {
	//Visits each TypeDeclaring node to build a hashmap of hierarchy nodes
	
	
	//The Hierarchy tree. Each key is the fully qualified name of the 
	//class the HierarchyNode represents
		
	//A Map of method indices. Each Key is the signature of a method and it
	//maps to the index of that method. When looking at a method, the signature
	//of the method is constructed and it is checked if the method exists
	//in the map. If not, the method is indexed and it is placed here. If it
	//does exist, the mapped index is placed in the method node's index field.
	HashMap<String, Integer> countList = new HashMap<String, Integer>();
	Map<TypeDeclaration, Stack<TypeScope>> HierarchyChain = HierarchyChecker.getClassHierachyChain();
	ArrayList<FieldDeclaration> staticFields = new ArrayList<FieldDeclaration>();
	ArrayList<String> checkedType = new ArrayList<String>();
	
	protected int parameters = 2;
	protected int locals = 1;		// Counts the local variable declarations
	
	protected int fields = 1;
	protected int methods = 0;		// Counts the methods

	public StaticLocalInit(SymbolTable table) {
		super(table);
//		System.out.println(HierarchyChain);
//		Iterator<Entry<TypeDeclaration, Stack<TypeScope>>> itHierachy = HierarchyChain.entrySet().iterator();
//		while(itHierachy.hasNext()){
//			Entry<TypeDeclaration, Stack<TypeScope>> en = itHierachy.next();
//			System.out.println(en.getValue());
//		}
	}
	
	@Override
	public void willVisit(ASTNode node) throws Exception{		
		super.willVisit(node);
		
		if (node instanceof ClassDeclaration){
			int methods = 0;
			int fields = 0;
			Stack<TypeScope> chain = HierarchyChain.get(node);
			while (!chain.isEmpty()){
				TypeScope ts = chain.pop();
				if (!checkedType.contains(ts.getName())){
//					System.out.println("OUTER: " + ts.getReferenceNode().getIdentifier());
					checkedType.add(ts.getName());
					//TODO Count all methods here...
					methods += methodCount((TypeDeclaration)ts.getReferenceNode(), methods);
					fields += fieldCount((TypeDeclaration)ts.getReferenceNode(), fields);
				}
				methods += ((TypeDeclaration)ts.getReferenceNode()).totalMethodDeclarations;
				fields += ((TypeDeclaration)ts.getReferenceNode()).totalFieldDeclarations;
			}
		}
		if (node instanceof TypeDeclaration) {
			this.fields = this.fields + 1;
			this.methods = 0;
		} else if (node instanceof MethodDeclaration) {
			this.parameters = 2;
			this.locals = 1;
		} else if (node instanceof FieldDeclaration){
			if (((FieldDeclaration)node).getModifiers().containModifier(Modifier.STATIC)){
				staticFields.add(((FieldDeclaration)node));
			}
		}
	}

	private int fieldCount(TypeDeclaration referenceNode, int icount) throws Exception {
		//TODO count the fields in the refnode and set that node's field counter
		int count = icount;
		for (FieldDeclaration fd : referenceNode.getBody().getFields()){
			fd.setIndex(count);
			count++;
			System.out.println(referenceNode.fullyQualifiedName+"." + fd.getIdentifier() + ": " + fd.getIndex());
		}
		referenceNode.totalFieldDeclarations = count;
		return count-icount;
	}
	private int methodCount(TypeDeclaration referenceNode, int icount) throws Exception {
		//TODO Give indicies for each method here
		//		Set total method count...
		int count = icount;
		for (MethodDeclaration md : referenceNode.getBody().getMethods()){
			if (md.getOverideMethod() != null){
				//AT this point, the overriden method SHOULD have already been indexed
				//Check this...
				md.setIndex(md.getOverideMethod().getIndex());
			}
			else{
				md.setIndex(count);
				count++;
//				System.out.println(referenceNode.fullyQualifiedName+"." + md.getIdentifier() + ": " + md.getIndex());
			}	
		}
		referenceNode.totalMethodDeclarations = count-icount;
		return count;
	}

	@Override
	public boolean visit(ASTNode node){
		if (node instanceof LocalVariableDeclaration) {
			
			((LocalVariableDeclaration) node).setIndex(this.locals++);
		} else if (node instanceof ParameterDeclaration) {
			((ParameterDeclaration) node).setIndex(this.parameters++);
		} else if (node instanceof FieldDeclaration) {
			((FieldDeclaration) node).setIndex(this.fields++);
		} else if (node instanceof MethodDeclaration) {
			// TODO: Type decl with super type need inherited super class's counter 
//			((MethodDeclaration) node).setIndex(this.methods++);
		}
		return true;
	}
	
	@Override
	public void didVisit(ASTNode node) throws Exception{
		if (node instanceof TypeDeclaration) {
//			((TypeDeclaration) node).totalFieldDeclarations = this.fields;
//			((TypeDeclaration) node).totalMethodDeclarations = this.methods;
		} else if (node instanceof MethodDeclaration) {
			((MethodDeclaration) node).totalLocalVariables = this.locals;
			this.methods++;
		}
		super.didVisit(node);
	}
}
