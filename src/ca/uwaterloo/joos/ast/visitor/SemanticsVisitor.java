package ca.uwaterloo.joos.ast.visitor;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.body.*;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.expr.AssignmentExpression;
import ca.uwaterloo.joos.ast.expr.primary.ExpressionPrimary;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.ast.statement.Statement;

public class SemanticsVisitor extends ASTVisitor {
	
	
	public boolean visit(MethodDeclaration node){
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean visit(ASTNode node) throws ChildTypeUnmatchException{
		if (node instanceof ClassBody){
			System.out.println("SemanticsVisitor.visit: Class Body found!");
			List<FieldDeclaration> tst = ((ClassBody) node).getFields();
			
		}
		
		else if (node instanceof MethodDeclaration){
			MethodDeclaration nodeMD = (MethodDeclaration) node;
			System.out.println("SemanticsVisitor.visit: Method Declaration found! " + nodeMD.getName().toString());
			Block tst = ((MethodDeclaration) node).getBody();
			System.out.println(tst.toString());
			
			List<LocalVariableDeclaration> vars = (List<LocalVariableDeclaration>) tst.getChildByDescriptor(tst.LOCAL_VAR);
			List<Statement> stmnt = (List<Statement>) tst.getChildByDescriptor(tst.STATEMENTS);
			for (int i = 0; i < vars.size(); i++){
				System.out.println(vars.get(i));
//				System.out.println(yolo.get(i).getType().);
			}
			
			for (int i = 0; i < stmnt.size(); i++){
//				System.out.println(stmnt.get(i));
//				System.out.println(yolo.get(i).getType().);
			}
			
		}
		
		else if (node instanceof ExpressionPrimary){
			ExpressionPrimary nodeAE = (ExpressionPrimary) node;
			
			System.out.println(nodeAE.getExpression().toString());
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
