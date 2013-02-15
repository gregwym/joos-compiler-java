package ca.uwaterloo.joos.ast.body;

import java.util.List;

import ca.uwaterloo.joos.ast.TypeDeclaration;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.scanner.Token;

public class InterfaceDeclaration extends TypeDeclaration {
	private Token classID;
	private List<Modifiers> modifers;
	private InterfaceBody interfaceBody;
	public InterfaceDeclaration(Node TypeRoot)
	{
		
	}
}
