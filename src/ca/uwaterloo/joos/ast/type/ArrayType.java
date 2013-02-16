package ca.uwaterloo.joos.ast.type;

import ca.uwaterloo.joos.ast.ASTNode;

public class ArrayType extends Type{
     protected Type typeOfArray ;
     protected Integer dimisions;
     
     public ArrayType(ASTNode parent) {
 		super(parent);
 	}
}
