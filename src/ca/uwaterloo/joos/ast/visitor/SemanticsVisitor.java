package ca.uwaterloo.joos.ast.visitor;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.body.*;
import ca.uwaterloo.joos.ast.decl.*;
import ca.uwaterloo.joos.ast.FileUnit;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.expr.AssignmentExpression;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.primary.ExpressionPrimary;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.ast.statement.Statement;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class SemanticsVisitor extends ASTVisitor {
	static SymbolTable st = null;			//SYMBOLTABLE - Link to the global SymbolTable
	
	
	public SemanticsVisitor(SymbolTable ist){
		st = ist;
	}
	
	
	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception{
		//Processes a single node in the AST tree
		
		
		if (node instanceof ClassBody){
			System.out.println("SemanticsVisitor.visit: Class Body found!");
			/*List<FieldDeclaration> tst = ((ClassBody) node).getFields();
			
			for (int i = 0; i < tst.size(); i++){
				System.out.println(tst.get(i).getIdentifier().toString());
			}*/
		}
		
		if (node instanceof VariableDeclaration){
			System.out.println("SemanticsVisitor.visit: VarDecl Found!");
		}
		
		/*if (node instanceof MethodDeclaration){
			MethodDeclaration nodeMD = (MethodDeclaration) node;
			System.out.println("SemanticsVisitor.visit: Method Declaration found! " + nodeMD.getName().toString());
			Block tst = ((MethodDeclaration) node).getBody();
			System.out.println(tst.toString());
			
			List<LocalVariableDeclaration> vars = (List<LocalVariableDeclaration>) tst.getChildByDescriptor(tst.LOCAL_VAR);
			List<Statement> stmnt = (List<Statement>) tst.getChildByDescriptor(tst.STATEMENTS);
			for (int i = 0; i < vars.size(); i++){
				System.out.println(vars.get(i));
				System.out.println("WHAT: " + vars.get(i).getInitial().toString());
//				System.out.println(stmnt.get(0));			//IE IF statement
			}
			
		}
		*/
		if (node instanceof Block){
			Block nodeB = (Block)node;
			System.out.println("SemanticsVisitor.visit(): Block: " + node.toString());
			/*List<LocalVariableDeclaration> lv = nodeB.getLocalVariable();
			
			for (int i = 0; i < lv.size(); i++){
				System.out.println(lv.get(i).toString());
				
				if (lv.get(i).getInitial() instanceof ExpressionPrimary){
				ExpressionPrimary ep = (ExpressionPrimary) lv.get(i).getInitial();
				System.out.println(ep.getExpression());
				}
				//System.out.println(lv.get(i).getInitial().toString());
			}*/
			
		}
		
		return true;
	}
	@Override
	public void willVisit(ASTNode node) {
		//What happens for each node type initially
		
		if (node instanceof ClassBody){
			
		}

	}

	@Override
	public void didVisit(ASTNode node) {
		// TODO Auto-generated method stub

	}

}
