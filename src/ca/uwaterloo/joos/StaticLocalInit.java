package ca.uwaterloo.joos;
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
	private String currentBlock = "__default__";
	private TypeScope ts;
	public StaticLocalInit(SymbolTable table) {
		super(table);
		// TODO Auto-generated constructor stub
		//	Count the local var declarations
		//	Generate code for static field declaration
		//	How to read expressions...?
	}
	
	
	@Override
	public void willVisit(ASTNode node) throws Exception{
		//Look for Local Field Declarations
		//Check if they are static
		//They NEED to be inited
		//Check the INITIAL child node
		//...?
		
		if (node instanceof FileUnit){
			System.out.println("StaticLocalInit FILE: " + ((FileUnit)node).getIdentifier());
		}
		
		if (node instanceof PackageDeclaration){
			if (((PackageDeclaration)node).getPackageName() != null){
				currentBlock = ((PackageDeclaration)node).getPackageName().getName();
			}
		}
		
		if (node instanceof ClassDeclaration){
			currentBlock = currentBlock + "." + ((ClassDeclaration)node).getIdentifier();
			ts = table.getType(currentBlock);
			if (ts != null){
				System.out.println("SLI.ClassDeclaration.ts: " + ts);
				System.out.println("LIST SYMBOLS");
				ts.listSymbols();
			}
		}
		
		
		if (node instanceof TypeBody){
			//Emit code for all of the static fields
			
			List<FieldDeclaration> fields = ((TypeBody)node).getFields();
			List<MethodDeclaration> methods = ((TypeBody)node).getMethods();
//			System.out.println(methods);
			for (FieldDeclaration fd : fields){
				countField(fd);				
			}

			for (MethodDeclaration md: methods){
				countVars(md);
			}			
		}
	}
	
	private void countVars(MethodDeclaration md) throws Exception {
		//TODO run a count of all local variable declarations in a method
		//TODO change so that an index is associated to every
		//		local variable in getLocalVariable()
		//		Place value of locals into the table entry 
		//		for the related var
		if (md.getBody() != null && md.getBody().getLocalVariable() != null){
			List<ParameterDeclaration> params = md.getParameters();
			for (ParameterDeclaration pd : params){
				pd.setIndex(locals);
//				System.out.println("PARAMDECL: " + pd.getName().getName() + " " + pd.getIndex());
				locals++;
			}
			locals = 0;
			
			List<LocalVariableDeclaration> localvars= md.getBody().getLocalVariable();
			for (LocalVariableDeclaration lvd : localvars){
				lvd.setIndex(locals);
//				System.out.println("LOCALDECL: " + lvd.getName().getName() + " " + lvd.getIndex());
				locals++;
			}
		}
		locals = 0;
	}


	private void countField(FieldDeclaration fd) throws Exception {
		//TODO here is where we emit code for a field declaration
		Modifiers md = fd.getModifiers();
		if (md.containModifier(Modifier.STATIC)){
			//Code gen for Static field here
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
		if (node instanceof FileUnit){
//			System.out.println(this.locals);
		}
		
	}

}
