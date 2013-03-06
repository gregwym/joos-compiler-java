//Scratch
//TODO 
//	-JAVADOC
//	-Define Table
package ca.uwaterloo.joos.symbolTable;

//Proposal
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.AST;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.Modifiers.Modifier;
import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.visitor.DeepDeclVisitor;
import ca.uwaterloo.joos.ast.visitor.TopDeclVisitor;
//import ca.uwaterloo.joos.ast.ASTNode;


public class SymbolTable{
	/**
	 * Symbol Table
	 * 
	 * Scans an AST of a validated joos source file.
	 * The class maintains a static HashMap. After an AST scan is completed
	 * the HashMap is updated with the declarations held in the file's global
	 * namespace.
	 * 
	 */
	static Logger 		logger = Main.getLogger(SymbolTable.class);
	
	
	private String name = null;	// Represents the name of the current scope
	private Map<String, TableEntry> symbolTable = null;	// A map mapping identifiers to their related ASTNode
	private static Map<String, SymbolTable> scopes = new HashMap<String, SymbolTable>();	// Links each Scope together
	
	//Constructs a symbol table
	//An AST is generated at this point, walk it.
	
	//First task is to build the environment
	//	-Replace each symbol declaration with a reference to the Symbol table.
	//	-Add the symbol to the table at some index and put that index into the AST
	//	-Associate each package...
	
	public SymbolTable(String name){
		this.symbolTable = new HashMap<String, TableEntry>();
		this.name = name;
		SymbolTable.scopes.put(name, this);
	}
	
	public static SymbolTable getScope(String name) {
		SymbolTable table = SymbolTable.scopes.get(name);
		if(table == null) {
			table = new SymbolTable(name);
		}
		return table;
	}
	
	public static boolean containScope(String name) {
		return SymbolTable.scopes.containsKey(name);
	}
	
	public void appendScope(SymbolTable table){
		//ONLY CALLED FROM BLOCK VISITOR
		
		for (String key : table.symbolTable.keySet()) {
			TableEntry entry = table.symbolTable.get(key);
			this.symbolTable.put(key, entry);
		}
	}
	
	public void addPublicMembers(SymbolTable table) throws ChildTypeUnmatchException {
		for(String key: table.symbolTable.keySet()) {
			TableEntry entry = table.symbolTable.get(key);
			
			if(entry.getNode() instanceof BodyDeclaration) {
				BodyDeclaration node = (BodyDeclaration) entry.getNode();
				if(node.getModifiers().getModifiers().contains(Modifier.PUBLIC)) {
					this.symbolTable.put(key, entry);
				}
			}
		}
	}
	
	public String getName(){
		return name;
	}
	
	public void addClass(String key, ASTNode node){
		TableEntry te = new TableEntry(node);
		te.setLevel(0);
		symbolTable.put(key, te);
	}
	
	public TableEntry getClass(String key){
		return symbolTable.get(key + "{}");
	}
	
	public void addDeclaration(String key, ASTNode node, int level){
		TableEntry te = new TableEntry(node);
		te.setLevel(level);
		symbolTable.put(key, te);
		
	}
	
	public String signatureOfMethod(MethodDeclaration method) throws Exception {
		String name = this.name + "." + method.getName().getName() + "(";
		for(ParameterDeclaration parameter: method.getParameters()) {
			name += parameter.getType().getIdentifier();
		}
		name += ")";
		return name;
	}

	public void addMethod(MethodDeclaration node) throws Exception{
		String name = this.signatureOfMethod(node);
		symbolTable.put(name, new TableEntry(node));
	}
	
	public TableEntry getMethod(MethodDeclaration node) throws Exception{
		//If false, no Method exists and we can add it
		String name = this.signatureOfMethod(node);
		return symbolTable.get(name);
	}
	
	public String nameForDecl(VariableDeclaration field) throws Exception {
		String name = this.getName() + "." + field.getName().getName();
		return name;
	}
	
	public boolean containVariableName(VariableDeclaration varDecl) throws Exception {
		String simpleName = varDecl.getName().getSimpleName();
		for(String key: this.symbolTable.keySet()) {
			TableEntry entry = this.symbolTable.get(key);
			ASTNode node = entry.getNode(); 
			
			if((node instanceof LocalVariableDeclaration || 
					node instanceof ParameterDeclaration) && 
					((BodyDeclaration) node).getName().getSimpleName().equals(simpleName)) {
				return true;
			}
		}
		return false;
	}

	public void addVariableDecl(VariableDeclaration node) throws Exception{
		//Add a field
		symbolTable.put(this.nameForDecl(node), new TableEntry(node));
	}
	
	public void addVariableDecl(VariableDeclaration node, int level) throws Exception{
		//Add a field
		TableEntry entry = new TableEntry(node);
		entry.setLevel(level);
		String name = this.nameForDecl(node);
		
//		if(name.substring(name.lastIndexOf("."), name.length()));
		
		symbolTable.put(name, entry);
	}

	public TableEntry getVariableDecl(VariableDeclaration node) throws Exception{
		return this.symbolTable.get(this.nameForDecl(node));
	}
	
	public static void build(List<AST> asts) throws Exception{
		logger.setLevel(Level.FINE);
		logger.fine("Building SymbolTable");
		//Called from main
		for (AST ast: asts){
			ast.getRoot().accept(new TopDeclVisitor());
		}
		
		logger.info("Pass 1 Finished");
		
		for (AST iast: asts){
			iast.getRoot().accept(new DeepDeclVisitor());
		}
	}

	public static void listScopes() {
		System.out.println("Listing Scopes");
		List<String> keys = new ArrayList<String>(scopes.keySet());
		Collections.sort(keys);
		for (String key : keys){
			System.out.println(scopes.get(key).getName());
			scopes.get(key).listSymbols();
		}
	}
	
	public void listSymbols(){
		for (String key: this.symbolTable.keySet()){
			System.out.println("\t" + key + "\t" + this.symbolTable.get(key).getNode() + "\tLevel: " + this.symbolTable.get(key).getLevel());
		}
		System.out.println();
	}
	
	public String toString() {
		return "<" + this.getClass().getSimpleName() + "> " + this.name;
	}
}