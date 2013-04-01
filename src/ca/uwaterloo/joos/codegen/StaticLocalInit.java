package ca.uwaterloo.joos.codegen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.Modifiers.Modifier;
import ca.uwaterloo.joos.ast.body.TypeBody;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;

//MATT ADD
class HierarchyNode{
	//Temporary
	//A node in the Hierarchy Tree
	
	String 			className = null;						//The fully qualified name of the class
	List<String> 	extenders = new ArrayList<String>();	//A list of fully qualified names which extend this class
	String 			extend = null;							//The class this class extends
	
	//Print functions
	void showExtend(){ 
		System.out.println("\textends:");
		System.out.println("\t\t" + extend);
		System.out.println();
	}
	
	void showExtenders(){
		System.out.println("\textended by:");
		for (int i = 0; i < extenders.size(); i++){
			System.out.println("\t\t" + extenders.get(i));
		}
		System.out.println();
	}
}
//	ADD END

public class StaticLocalInit extends SemanticsVisitor {
	//Visits each TypeDeclaring node to build a hashmap of hierarchy nodes
	
	
	//The Hierarchy tree. Each key is the fully qualified name of the 
	//class the HierarchyNode represents
	HashMap<String, HierarchyNode> hierarchy;
		
	//A Map of method indices. Each Key is the signature of a method and it
	//maps to the index of that method. When looking at a method, the signature
	//of the method is constructed and it is checked if the method exists
	//in the map. If not, the method is indexed and it is placed here. If it
	//does exist, the mapped index is placed in the method node's index field.
	HashMap<String, Integer> countList = new HashMap<String, Integer>();
	ArrayList<FieldDeclaration> staticFields = new ArrayList<FieldDeclaration>();
	
	
	protected int parameters = 1;
	protected int locals = 0;		// Counts the local variable declarations
	
	protected int fields = 0;
	protected int methods = 0;		// Counts the methods

	public StaticLocalInit(SymbolTable table) {
		super(table);
	}
	
	@Override
	public void willVisit(ASTNode node) throws Exception{		
		super.willVisit(node);
		
		//MATT ADD
		if (node instanceof ClassDeclaration){
			
			ReferenceType rt = ((ClassDeclaration)node).getSuperClass();
			String newClass = ((ClassDeclaration)node).fullyQualifiedName;
			if (newClass.equals("java.lang.Object")){
				if (!hierarchy.containsKey("java.lang.Object")){
					HierarchyNode nh = new HierarchyNode();
					nh.className = newClass;
					hierarchy.put(newClass, nh);
				}
			}
			else if (rt == null){		
				if (!hierarchy.containsKey("java.lang.Object")){
					HierarchyNode nh = new HierarchyNode();
					nh.className = "java.lang.Object";
					hierarchy.put(nh.className, nh);
				}
				HierarchyNode nh = new HierarchyNode();
				nh.className = newClass;
				nh.extend = "java.lang.Object";
				hierarchy.put(nh.className, nh);
				hierarchy.get("java.lang.Object").extenders.add(newClass);
				
			}
			else if (rt != null){
				String extendedClass = ((ClassDeclaration)node).getSuperClass().getFullyQualifiedName();
//				System.out.println("MTD: " + extendedClass);
//				System.out.println(((ClassDeclaration)node).fullyQualifiedName);
			
				if (!hierarchy.containsKey(newClass)){
					HierarchyNode nh = new HierarchyNode();
					nh.className = newClass;
					hierarchy.put(nh.className, nh);
				}
			
				HierarchyNode gh = hierarchy.get(newClass);
				gh.extend = extendedClass;
			
				if (!hierarchy.containsKey(extendedClass)){
					HierarchyNode nh = new HierarchyNode();
					nh.className = extendedClass;
					hierarchy.put(nh.className, nh);
				}
			
				hierarchy.get(extendedClass).extenders.add(newClass);
			}
		}
//		ADD END
		
		if (node instanceof TypeDeclaration) {
			this.fields = 0;
			this.methods = 0;
		} else if (node instanceof MethodDeclaration) {
			this.parameters = 1;
			this.locals = 0;
		} else if (node instanceof FieldDeclaration){
			if (((FieldDeclaration)node).getModifiers().containModifier(Modifier.STATIC)){
				staticFields.add(((FieldDeclaration)node));
			}
		}
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
			((TypeDeclaration) node).totalFieldDeclarations = this.fields;
		} else if (node instanceof MethodDeclaration) {
			((MethodDeclaration) node).totalLocalVariables = this.locals;
		}
		super.didVisit(node);
	}
	
	
	//MATT ADD
	//After every AST has been traversed, a hashmap containing the
	//class hierarchy is constructed
	//This hashmap is then traversed here, outside of the visitor pattern.
	
	
	//TODO: Replace HierarchyNode hashmap with HierarchyChecker hashmap
			
	public void traverseMethods() throws Exception{
		//Look for the Object class
		//Actual traversal begins in the numberMethods() method
		for (String key : hierarchy.keySet()){
			if (hierarchy.get(key).extend == null){
				//This is a topmost class
				numberMethods(key, 0);
				countList = new HashMap<String,Integer>();
			}
		}
	}
	
	private void numberMethods(String key, int count) throws Exception{
		System.out.println(key);
		String sig;
		int indexCount = count;
		//Number the methods in a class and all classes it extends
		TypeBody Cnode = ((TypeDeclaration) this.table.getType(hierarchy.get(key).className).getReferenceNode()).getBody();
		List<MethodDeclaration> md = Cnode.getMethods();
		for (MethodDeclaration mnode: md){
			sig = mnode.getName().getName() + "_[";
			for (ParameterDeclaration pd : mnode.getParameters()){
				sig = sig + pd.getType().getIdentifier()+ ",";
				
			}
			sig = sig + "]";
			//Signature built. Check override
			if (countList.containsKey(sig)){
				mnode.setIndex(countList.get(sig));
			}
			else{
				countList.put(sig, indexCount);
				mnode.setIndex(indexCount);
				indexCount++;
			}
			
			System.out.println("\t" + sig + " " + mnode.getIndex());
			
		}
		
		for (String exs : hierarchy.get(key).extenders){
			numberMethods(exs, indexCount);
		}
	}
	public void listHierarchy(){
		Set<String> keys = hierarchy.keySet();
		for (String key: keys){
			System.out.println(hierarchy.get(key).className);
			hierarchy.get(key).showExtend();
			hierarchy.get(key).showExtenders();
			
			System.out.println("");
		}
	}
	//ADD END
}
