package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.*;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.primary.ExpressionPrimary;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class TopDeclVisitor extends SemanticsVisitor {

	private String name = null;
	public TopDeclVisitor(SymbolTable ist) {
		super(ist);
		// TODO Auto-generated constructor stub
	}

	public boolean visit(ASTNode node) throws ChildTypeUnmatchException{
		//TODO Interface Declaration
		
		if (node instanceof PackageDeclaration){
			PackageDeclaration PNode = (PackageDeclaration) node;
			name = PNode.getPackageName();
			st.setName(name);
			return true;
		}
		
		if (node instanceof ClassDeclaration){
			//TODO check if class has already been defined
			String tname = ((ClassDeclaration) node).getIdentifier();
			st.setName(st.getName() + "." + tname);
			st.addScope();//Adds the current symbol table to the static symbol table map.
		}
		
		if (node instanceof FieldDeclaration){
			try {
				String key = this.st.getName() + "." + ((FieldDeclaration) node).getName().getName(); 
				if (!st.hasField(key)) st.addField(key, node);
				else{
					System.err.println("TopDeclVisitor.visit(): Multiple Field Declarations with same name. Exiting with 42");
					System.exit(42);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
		if (node instanceof MethodDeclaration){
			//TODO check signatures

			try {
				String key = this.st.getName() + "." + ((MethodDeclaration) node).getName().getName();
				if (!st.hasMethod(key))st.addMethod(key, node);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		
		
		
		
		return true;
	}

	@Override
	public void willVisit(ASTNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void didVisit(ASTNode node) {
		// TODO Auto-generated method stub

	}

}
