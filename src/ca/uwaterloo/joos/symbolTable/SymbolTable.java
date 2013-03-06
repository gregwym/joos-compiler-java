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
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.AST;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;
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
	private static Stack<SymbolTable>view = new Stack<SymbolTable>();	// The Current Scope
	
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
	
	public List<String> appendScope(SymbolTable st, int blocks, int level){
		//ONLY CALLED FROM BLOCK VISITOR
		List<String> tmp = new ArrayList<String>();
		
		for (String key : st.symbolTable.keySet()) {
			if (st.symbolTable.get(key).getLevel() <= level){
				String lkey = key.substring(0, key.lastIndexOf("."));
				String rkey = key.substring(key.lastIndexOf(".") + 1);
				logger.info("Append Scope: "+ lkey + "." + blocks + "Block." + rkey);
				this.symbolTable.put(lkey + "." + blocks + "Block." + rkey, st.symbolTable.get(key));
				tmp.add(lkey + "." + blocks + "Block." + rkey);
			}
		}
		
		return tmp;
		
	}
	
	public void unAppendScope(int blocks, List<String> tmp){
		//ONLY CALLED FROM BLOCK VISITOR
		List<String> keys = tmp;
		for (String key: keys){
			this.symbolTable.remove(key);
			
		}
		
	}
	public Stack<SymbolTable> getView(){
		return view;
	}
	
	public void openScope(String st){
		view.push(scopes.get(st));
	}
	
	public void closeScope(){
		view.pop();
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

	public void addVariableDecl(VariableDeclaration node) throws Exception{
		//Add a field
		symbolTable.put(this.nameForDecl(node), new TableEntry(node));
	}
	
	public void addVariableDecl(VariableDeclaration node, int level) throws Exception{
		//Add a field
		TableEntry entry = new TableEntry(node);
		entry.setLevel(level);
		symbolTable.put(this.nameForDecl(node), entry);
	}

	public TableEntry getVariableDecl(VariableDeclaration node) throws Exception{
		return this.symbolTable.get(this.nameForDecl(node));
	}
	
	public void build(ASTVisitor visitor, ASTNode astNode) throws Exception {
		//Called whenever a visitor wants to build a new table
		astNode.accept(visitor);
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