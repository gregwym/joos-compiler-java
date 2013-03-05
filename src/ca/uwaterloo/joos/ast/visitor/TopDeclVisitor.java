package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.*;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.primary.ExpressionPrimary;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class TopDeclVisitor extends SemanticsVisitor {

	public TopDeclVisitor(SymbolTable ist) {
		super(ist);
		// TODO Auto-generated constructor stub
	}

	public boolean visit(ASTNode node) throws ChildTypeUnmatchException{
		//TODO Interface Declaration
		
		if (node instanceof PackageDeclaration){
			String tname = ((PackageDeclaration) node).getPackageName();
			st.setName(tname);
			return true;
		}
		
		if (node instanceof ClassDeclaration){
			String tname = ((ClassDeclaration) node).getIdentifier();
			st.setName(st.getName() + "." + tname);
//			System.out.println("TopDeclVisitor.visit(): Class Found: " + st.getName());
			st.addScope();//Adds the current symbol table to the static symbol table map.
		}
		
		if (node instanceof FieldDeclaration){
			try {
				String key = this.st.getName() + "." + ((FieldDeclaration) node).getName().getName(); 
//				System.out.println("TopDeclVisitor.visit(): Found Field: " + key + " Empty??" + st.isEmpty(key));
				if (!st.hasField(key)) st.addField(key, node);
				else{
//					st.g
					System.err.println("TopDeclVisitor.visit(): Multiple Field Declarations with same name. Exit with -1");
					System.exit(-1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
		if (node instanceof MethodDeclaration){
			//TODO check signatures
			MethodDeclaration Mnode = (MethodDeclaration) node;
			try {
//				System.out.println("TopDeclVisitor.visit(): Found Method: " + this.st.getName() + "." + Mnode.getName().getName());
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
