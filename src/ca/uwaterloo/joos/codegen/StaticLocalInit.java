package ca.uwaterloo.joos.codegen;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;

public class StaticLocalInit extends SemanticsVisitor {

	protected int parameters = 1;
	protected int locals = 0;		// Counts the local variable declarations
	
	protected int fields = 0;
	protected int methods = 0;		// Counts the methods

	public StaticLocalInit(SymbolTable table) {
		super(table);
	}
	
	@Override
	public void willVisit(ASTNode node) throws Exception{		
		super.willVisit(node);
		
		if (node instanceof TypeDeclaration) {
			this.fields = 0;
			this.methods = 0;
		} else if (node instanceof MethodDeclaration) {
			this.parameters = 1;
			this.locals = 0;
		}
	}

	@Override
	public boolean visit(ASTNode node){
		if (node instanceof LocalVariableDeclaration) {
			((LocalVariableDeclaration) node).setIndex(this.locals++);
		} else if (node instanceof ParameterDeclaration) {
			((ParameterDeclaration) node).setIndex(this.parameters++);
		} else if (node instanceof FieldDeclaration) {
			((FieldDeclaration) node).setIndex(this.fields++);
		} else if (node instanceof MethodDeclaration) {
			// TODO: Type decl with super type need inherited super class's counter 
			((MethodDeclaration) node).setIndex(this.methods++);
		}
		return true;
	}
	
	@Override
	public void didVisit(ASTNode node) throws Exception{
		if (node instanceof TypeDeclaration) {
			((TypeDeclaration) node).totalFieldDeclarations = this.fields;
		} else if (node instanceof MethodDeclaration) {
			((MethodDeclaration) node).totalLocalVariables = this.locals;
		}
		super.didVisit(node);
	}

}
