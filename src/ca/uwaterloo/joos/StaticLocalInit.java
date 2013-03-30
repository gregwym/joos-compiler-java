package ca.uwaterloo.joos;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.Modifiers;
import ca.uwaterloo.joos.ast.Modifiers.Modifier;
import ca.uwaterloo.joos.ast.body.TypeBody;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;

public class StaticLocalInit extends SemanticsVisitor {

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
		
		if (node instanceof TypeBody){
			//Emit code for all of the static fields
			List<FieldDeclaration> fields = ((TypeBody)node).getFields();
			for (FieldDeclaration fd : fields){
				emitField(fd);
			}
		}
		
	}
	
	private void emitField(FieldDeclaration fd) throws Exception {
		//TODO HERE is where we emit code for a field declaration
		Modifiers md = fd.getModifiers();
		if (md.containModifier(Modifier.STATIC)){
			//Code gen for Static field here
		}
		
		
	}


	@Override
	public boolean visit(ASTNode node){
		
		
		
		
		
		return false;
	}
	
	@Override
	public void didVisit(ASTNode node){
		
	}

}
