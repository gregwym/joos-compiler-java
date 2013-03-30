package ca.uwaterloo.joos;
//TODO move to codegen package when finished
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;

public class StaticInitalizerVisitor extends SemanticsVisitor{

	public StaticInitalizerVisitor(SymbolTable table) {
		super(table);
		// TODO Auto-generated constructor stub
	}
	//TODO Extend Semantics visitor
	//Generate code which initalizes static fields 
	//Count the number of local declarations in a given method
	//Store the cumulative local declarations in BlockScope node
	
	//On static field decl
	//	check for any INIT child
	//	if none exists, init to default value {0, true}
	//	Otherwise, init to whatever the assignment expression evals to
	
	
	//On Block node
	
	//Visit:
	//	look for localvardecl nodes
	//	for each increment the block decl counter
	//DidVisit:
	//	Get all child block decl counts...?
	//  add them to current count
	
	//Get a list of the external methods used in a type scope
	
	//OUTPUT:
	//Concatenation of strings which form the init code for a file
	//List of strings which hold the signatures of external methods
	//		NEED TO GET FQN OF A METHOD FROM ITS INVOCATION
}
