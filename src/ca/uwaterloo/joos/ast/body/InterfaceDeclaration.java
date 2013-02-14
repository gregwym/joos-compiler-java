package ca.uwaterloo.joos.ast.body;

import java.util.List;

import ca.uwaterloo.joos.ast.type.Modifier;
import ca.uwaterloo.joos.scanner.Token;

public class InterfaceDeclaration {
	private Token classID;
	private List<Modifier> modifers;
	private InterfaceBody interfaceBody;
	public InterfaceDeclaration(List<Modifier> modifers,Token classID,InterfaceBody interfaceBody)
	{
		
	}
}
