//Scratch
//TODO 
//	-JAVADOC
//	-Define Table
package ca.uwaterloo.joos.symbolTable;

//Proposal
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.AST;
import ca.uwaterloo.joos.ast.ASTNode;
//import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;
import ca.uwaterloo.joos.ast.visitor.BlockVisitor;
import ca.uwaterloo.joos.ast.visitor.DeepDeclVisitor;
import ca.uwaterloo.joos.ast.visitor.SemanticsVisitor;
import ca.uwaterloo.joos.ast.visitor.TopDeclVisitor;


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
	static Logger 		logger = Main.getLogger(Main.class);
	
	
	private String 					name		= null;									//	Represents the name of the current scope
	private AST 					tree 		= null;									//	Link to an AST to scan. Updated in walk()
	Map<String, TableEntry> 		SymbolTable = null;									//	A map mapping identifiers to their related ASTNode
	static Map<String, SymbolTable> Scopes 		= new HashMap<String, SymbolTable>(); 	//	Links each Scope together
	private static Stack<SymbolTable>view		= new Stack<SymbolTable>();				//	The Current Scope
	
	//Constructs a symbol table
	//An AST is generated at this point, walk it.
	
	//First task is to build the environment
	//	-Replace each symbol declaration with a reference to the Symbol table.
	//	-Add the symbol to the table at some index and put that index into the AST
	//	-Associate each package...
	
	
	
	public Stack getView(){
		return view;
	}
	
	public void openScope(String st){
		view.push(Scopes.get(st));
	}
	
	public void closeScope(){
		view.pop();
	}
	
	public void setName(String iname){
//		System.out.println("Setting name to: " + iname);
		this.name = iname;
	}
	
	public String getName(){
		return name;
	}
	public SymbolTable(){
		//TODO 
		//	-init table
		this.SymbolTable = new HashMap<String, TableEntry>();
	}
	
	public void addField(String key, ASTNode value){
		//Add a field
		SymbolTable.put(key, new TableEntry(value));
	}
	
	public void addDeclaration(String key, ASTNode value, int level){
		TableEntry te = new TableEntry(value);
		te.setLevel(level);
		SymbolTable.put(key, te);
		
	}
	public void addMethod(String key, ASTNode value){
		SymbolTable.put(key + "()", new TableEntry(value));
	}
	
	public TableEntry getMethod(String key){
		return this.SymbolTable.get(key+"()");
		
	}
	
	public TableEntry getField(String key){
		return this.SymbolTable.get(key);
	}
	
	public boolean hasField(String key){
		//if false, no field exists and we can add it
		return (SymbolTable.containsKey(key));
	}
	
	public boolean hasMethod(String key){
		//If false, no Method exists and we can add it
		return (SymbolTable.containsKey(key + "()"));
	}
	public void addScope(){
		this.Scopes.put(this.name, this);
	}
	
	public void build(ASTVisitor visitor, ASTNode ast) throws Exception {
		ast.accept(visitor);
	}
	
	public void build(List<AST> asts) throws Exception{
		for (AST iast: asts){
			iast.getRoot().accept(new TopDeclVisitor(this));
			iast.getRoot().accept(new DeepDeclVisitor(this));
		}
	}

	public static void listScopes() {
		System.out.println("Listing Scopes");
		for (String key : Scopes.keySet()){
			System.out.println(Scopes.get(key).getName());
			Scopes.get(key).ListSymbols();
			
		}
	}
	
	public void ListSymbols(){
		for (String key: this.SymbolTable.keySet()){
			System.out.println("	" + key + "    " + this.SymbolTable.get(key).getNode() + "   Level: " + this.SymbolTable.get(key).getLevel());
		}
	}
}