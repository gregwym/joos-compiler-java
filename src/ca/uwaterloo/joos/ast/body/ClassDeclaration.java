package ca.uwaterloo.joos.ast.body;

import java.util.List;

import ca.uwaterloo.joos.ast.type.ClassName;
import ca.uwaterloo.joos.ast.type.Modifier;
import ca.uwaterloo.joos.scanner.Token;

public class ClassDeclaration {
	// private List<TypeParameter> Parameters;

	private List<ClassName> extendClassList;
    private List<ClassName> implementClassList;
   //private Token classKeyword;
	private Token classID;
	private List<Modifier> modifers;
	private ClassBody classBody;
	public ClassDeclaration(List<Modifier> modifers,Token classID,ClassBody classBody)
	{
		 
	} 
	
}
