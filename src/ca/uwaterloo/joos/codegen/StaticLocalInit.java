package ca.uwaterloo.joos.codegen;
//TODO move to codegen package when finished
//TODO get the method index
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.FileUnit;
import ca.uwaterloo.joos.ast.Modifiers;
import ca.uwaterloo.joos.ast.Modifiers.Modifier;
import ca.uwaterloo.joos.ast.body.TypeBody;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.symboltable.BlockScope;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;
import ca.uwaterloo.joos.symboltable.TableEntry;
import ca.uwaterloo.joos.symboltable.TypeScope;

public class StaticLocalInit extends SemanticsVisitor {

	protected int locals = 0;//Counts the local variable declarations
	protected int methods = 0; //Counts the methods
	private TypeScope ts;
	public StaticLocalInit(SymbolTable table) {
		super(table);
	}
	
	
	@Override
	public void willVisit(ASTNode node) throws Exception{		
		
		if (node instanceof TypeBody){
			//Emit code for all of the static fields
			
			List<FieldDeclaration> fields = ((TypeBody)node).getFields();
			List<MethodDeclaration> methods = ((TypeBody)node).getMethods();
			for (FieldDeclaration fd : fields){
				countField(fd);				
			}

			for (MethodDeclaration md: methods){
				countVars(md);
			}			
		}
	}
	
	private void countVars(MethodDeclaration md) throws Exception {
		if (md.getBody() != null && md.getBody().getLocalVariable() != null){
			List<ParameterDeclaration> params = md.getParameters();
			for (ParameterDeclaration pd : params){
				pd.setIndex(locals);
				locals++;
			}
			locals = 0;
			
			List<LocalVariableDeclaration> localvars= md.getBody().getLocalVariable();
			for (LocalVariableDeclaration lvd : localvars){
				lvd.setIndex(locals);
				locals++;
			}
		}
		locals = 0;
	}


	private void countField(FieldDeclaration fd) throws Exception {
		Modifiers md = fd.getModifiers();
		if (md.containModifier(Modifier.STATIC)){
			fd.setIndex(locals);
			locals++;
		}
		locals = 0;
		
	}


	@Override
	public boolean visit(ASTNode node){
		
		return true;
	}
	
	@Override
	public void didVisit(ASTNode node){
		
	}

}
