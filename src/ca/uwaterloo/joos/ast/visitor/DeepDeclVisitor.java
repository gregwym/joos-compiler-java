package ca.uwaterloo.joos.ast.visitor;

import java.util.Stack;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.ImportDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.OnDemandImport;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class DeepDeclVisitor extends SemanticsVisitor {
	
	private String name = null;
	public DeepDeclVisitor(SymbolTable ist) {
		super(ist);
		String lookup = null;
	}
	
	public void willVisit(ASTNode node){
		
		
		if (node instanceof ImportDeclaration){
			ImportDeclaration INode = (ImportDeclaration) node;
			try {
				System.out.println("IMPORT: " + INode.getImportName().getName());
			} catch (ChildTypeUnmatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//NOTE: import is a file given on the command line
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		if (node instanceof MethodDeclaration){
			
			//Make a new symbol table which builds 
			MethodDeclaration CNode = (MethodDeclaration) node;
//			System.out.println("METHOD NAME: " + CNode.getIdentifier());
			name = name + "." + CNode.getIdentifier();
//			System.out.println("METHOD FOUND CONSTRUCTING SYMBOL TABLE WITH NAME: " + name);
			SymbolTable nst = new SymbolTable();
			nst.setName(name + "()");
			nst.openScope(nst.getName());
			nst.addScope();		//Adds the new block symboltable to the global hash of tables
			try {
				//visit the blocks within the method
				nst.build(new BlockVisitor(nst), node);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (node instanceof ClassDeclaration){
			ClassDeclaration CNode = (ClassDeclaration) node;
//			System.out.println("CLASS NAME: " + CNode.getIdentifier());
			name = name + "." + CNode.getIdentifier();
//			st.openScope(name);
		}
	}
	
	public void didVisit(ASTNode node){
		if (node instanceof MethodDeclaration){
			MethodDeclaration CNode = (MethodDeclaration) node;
			st.closeScope();
			name = name.substring(0, name.lastIndexOf("."));
//			System.out.println("DONE BODY: " + name);
//			System.out.println(name);
			
		}
	}
	
	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception {
		
		if (node instanceof Block){
			System.out.println("BLOCK");
		}
		if (node instanceof OnDemandImport){
			OnDemandImport INode = (OnDemandImport) node;
			System.out.println("IMPORT: " + INode.getImportName());
		}
		
		if (node instanceof PackageDeclaration){
			PackageDeclaration PNode = (PackageDeclaration) node;
			name = PNode.getPackageName();
			st.setName(name);
		}
		
		if (node instanceof ClassDeclaration){	
			
		}
		
		if (node instanceof MethodDeclaration){
			//open a new symboltable at Package.classname.Methodname
			
//			nst.constructScope(node);
			return false;
		}
		
		return true;
	}

}
