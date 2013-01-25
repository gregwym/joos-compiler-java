package ca.uwaterloo.joos;

//import java.util.*;

public class Token {
	String kind;
	String lexeme;

	public Token(String kind, String lexeme) {
		this.kind = kind;
		this.lexeme = lexeme;
	}

	public String toString() {
		return this.kind + " " + this.lexeme;
	}
}
