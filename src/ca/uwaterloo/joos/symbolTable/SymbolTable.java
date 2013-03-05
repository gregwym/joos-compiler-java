//Scratch
//TODO 
//	-JAVADOC
//	-Define Table
package ca.uwaterloo.joos.symbolTable;

//Proposal
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.AST;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.statement.Block;
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
	Map<String, ASTNode> 			SymbolTable = null;									//	A map mapping identifiers to their related ASTNode
	static Map<String, SymbolTable> Scopes 		= new HashMap<String, SymbolTable>(); 	//	Links each Scope together
	private static Stack<SymbolTable>view		= new Stack<SymbolTable>();
	
	//Constructs a symbol table
	//An AST is generated at this point, walk it.
	
	//First task is to build the environment
	//	-Replace each symbol declaration with a reference to the Symbol table
	//	-Add the symbol to the table at some index and put that index into the AST
	//	-Associate each package...
	
	private class TableEntry{
		//An entry mapped to in the symboltable hashmap
		//TODO remove.
		String 		value;
		TableEntry 	prevVal;
		TableEntry 	chain;
		int			scope;	
		Object		attributes;
		
	}
	
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
		this.name = iname;
		System.out.println("SymbolTable.setName() Setting Name to " + iname);
	}
	
	public String getName(){
		return name;
	}
	public SymbolTable(){
		//TODO 
		//	-init table
		this.SymbolTable = new HashMap<String, ASTNode>();
	}
	
	public void addField(String key, ASTNode value){
		SymbolTable.put(key, value);
	}
	
	public void addMethod(String key, ASTNode value){
		SymbolTable.put(key + "()", value);
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
	
	public void constructTable(ASTNode ast) throws Exception{
		//Calls the AST's accept method with visitor type SemanticsVisitor
		ast.accept(new TopDeclVisitor(this));
		ast.accept(new DeepDeclVisitor(this));
	}
	
	public void constructScope(ASTNode ast) throws Exception{
		ast.accept(new DeepDeclVisitor(this));
	}
	
}