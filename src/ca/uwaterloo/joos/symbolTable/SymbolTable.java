//Scratch
//TODO 
//	-JAVADOC
//	-Define Table
package ca.uwaterloo.joos.symbolTable;

//Proposal
import java.util.HashMap;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.AST;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.statement.Block;
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
	
	
	static private int 	level = 0;													//INT 		- Represents the current block level
	private AST 		tree = null;												//AST 		- Link to an AST to scan. Updated in walk()
	HashMap<String, TableEntry> SymbolTable = null;									//HASHMAP 	- 
	
	public void openScope(){
		level++;
	}
	
	public void closeScope(){
		level--;
	}
	
	//Constructs a symbol table
	//An AST is generated at this point, walk it.
	
	//First task is to build the environment
	//	-Replace each symbol declaration with a reference to the Symbol table
	//	-Add the symbol to the table at some index and put that index into the AST
	//	-Associate each package...
	
	private class TableEntry{
		//An entry mapped to in the symboltable hashmap
		String 		value;
		TableEntry 	prevVal;
		TableEntry 	chain;
		Object		attributes;
		
	}
	
	public SymbolTable(AST ast){
		//TODO 
		//	-init table
		
		SymbolTable = new HashMap<String, TableEntry>();
		this.tree = ast;
	}
	
	
	private void constructTable(){
		//TODO REMOVE
		//Walks the AST and constructs a table holding declarations 
		//Right now the symbols are stored in a hash table for quick lookup time
		
		//Check that:
		//	-No two fields in the same class have the same name
		//	-No two local variables with overlapping scope have the same name
		//	-No two classes or interfaces have the same name
		
		
	}
	
	public void constructTable(ASTNode ast){
		//Calls the AST's accept method with visitor type SemanticsVisitor
		processNode(tree.getRoot());
		
	}

	private void processNode(ASTNode root) {
		if (root instanceof )
		
	}
	
}