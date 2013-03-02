//Scratch

package ca.uwaterloo.joos.symbolTable;

import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.AST;
import ca.uwaterloo.joos.ast.visitor.SemanticsVisitor;

public class SymbolTable{
	static Logger logger = Main.getLogger(Main.class);
	//Constructs a symbol table
	//An AST is generated at this point, walk it.
	
	//First task is to build the environment
	//	-Replace each symbol declaration with a reference to the Symbol table
	//	-Add the symbol to the table at some index and put that index into the AST
	//	-Associate each package...
	
	AST tree;
	
	public SymbolTable(AST ast){
		//Right now, do AST walking in this class
		this.tree = ast;
		
		walk();
	}
	
	public void constructTable(){
		//Walks the AST and constructs a table holding declarations 
		//Right now the symbols are stored in a hash table for quick lookup time
		
		//Check that:
		//	-No two fields in the same class have the same name
		//	-No two local variables with overlapping scope have the same name
		//	-No two classes or interfaces have the same name
		
		
	}
	
	private void walk(){
		System.out.println("In Walk");
		//TODO Test. REMOVE.
		try {
			tree.getRoot().accept(new SemanticsVisitor());
			System.out.println(tree.getRoot().getIdentifier());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}