//Scratch
//TODO 
//	-JAVADOC
//	-Define Table
package ca.uwaterloo.joos.symbolTable;

//Proposal
import java.util.ArrayList;
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
	private static Map<String, SymbolTable> Scopes = new HashMap<String, SymbolTable>();	// Links each Scope together
	private static Stack<SymbolTable>view = new Stack<SymbolTable>();	// The Current Scope
	
	//Constructs a symbol table
	//An AST is generated at this point, walk it.
	
	//First task is to build the environment
	//	-Replace each symbol declaration with a reference to the Symbol table.
	//	-Add the symbol to the table at some index and put that index into the AST
	//	-Associate each package...
	
	
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
		view.push(Scopes.get(st));
	}
	
	public void closeScope(){
		view.pop();
	}
	
	public void setName(String name){
//		System.out.println("Setting name to: " + iname);
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public SymbolTable(){
		//TODO 
		//	-init table
		this.symbolTable = new HashMap<String, TableEntry>();
	}
	
	public void addField(String key, ASTNode node){
		//Add a field
		symbolTable.put(key, new TableEntry(node));
	}
	
	public void addClass(String key, ASTNode node){
		TableEntry te = new TableEntry(node);
		te.setLevel(0);
		symbolTable.put(key + "{}", te);
	}
	
	public TableEntry getClass(String key){
		return symbolTable.get(key + "{}");
	}
	
	public void addDeclaration(String key, ASTNode node, int level){
		TableEntry te = new TableEntry(node);
		te.setLevel(level);
		symbolTable.put(key, te);
		
	}
	public void addMethod(MethodDeclaration node) throws Exception{
		String name = this.name + "." + node.getName().getName() + "(";
		for(ParameterDeclaration parameter: node.getParameters()) {
			name += parameter.getType().getIdentifier();
		}
		name += ")";
		symbolTable.put(name, new TableEntry(node));
	}
	
	public TableEntry getMethod(String key){
		return this.symbolTable.get(key+"()");
		
	}
	
	public TableEntry getField(String key){
		return this.symbolTable.get(key);
	}
	
	public boolean hasField(String key){
		logger.fine("KEY: "+key);
		//if false, no field exists and we can add it
		//Currently this just checks the immediate scope for any overlap
		//a nested block has it's parent's scope pushed into it during processing
		//and is removed after...
		return (symbolTable.containsKey(key));
	}
	
	public boolean hasMethod(MethodDeclaration method) throws Exception{
		//If false, no Method exists and we can add it
		String name = this.name + "." + method.getName().getName() + "(";
		for(ParameterDeclaration parameter: method.getParameters()) {
			name += parameter.getType().getIdentifier();
		}
		name += ")";
		return (symbolTable.containsKey(name));
	}
	
	public static boolean hasClass(String key) {
		return SymbolTable.Scopes.containsKey(key);
	}
	
	public void addScope(){
		SymbolTable.Scopes.put(this.name, this);
	}
	
	public void build(ASTVisitor visitor, ASTNode astNode) throws Exception {
		//Called whenever a visitor wants to build a new table
		astNode.accept(visitor);
	}
	
	public void build(List<AST> asts) throws Exception{
		logger.setLevel(Level.FINE);
		logger.fine("Building SymbolTable");
		//Called from main
		for (AST iast: asts){
			iast.getRoot().accept(new TopDeclVisitor(this));
			iast.getRoot().accept(new DeepDeclVisitor(this));
		}
	}

	public static void listScopes() {
		System.out.println("Listing Scopes");
		for (String key : Scopes.keySet()){
			System.out.println(Scopes.get(key).getName());
			Scopes.get(key).listSymbols();
		}
	}
	
	public void listSymbols(){
		for (String key: this.symbolTable.keySet()){
			System.out.println("	" + key + "    " + this.symbolTable.get(key).getNode() + "   Level: " + this.symbolTable.get(key).getLevel());
		}
		System.out.println();
	}
}