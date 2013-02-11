package ca.uwaterloo.joos.scanner;

public class Token {
	private String kind;
	private String lexeme;

	public Token(String kind, String lexeme) {
		this.kind = kind;
		this.lexeme = lexeme;
	}

	/**
	 * @return the kind
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * @return the lexeme
	 */
	public String getLexeme() {
		return lexeme;
	}

	@Override
	public String toString() {
		return "<Token> " + this.kind + " " + this.lexeme;
	}
}
