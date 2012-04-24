
public enum TokenType {
	T_ID("ID", "[a-zA-Z][a-zA-Z0-9_]*"),
	T_INTNUM("INTNUM", "[0-9]+"),
	T_ASSIGNMENT("ASSIGN", ":="),
	T_COMMA("COMMA", ","),
	T_STAR("STAR", "\\*"),
	T_PERCENT("MODULUS", "%"),
	T_SEMICOLON("SEMICOLON", ";"),
	T_PLUS("PLUS", "\\+"),
	T_MINUS("MINUS", "-"),
	T_UNKNOWN("UNKNOWN", "\\w");
	
	private String identifier;
	private String regex;
	
	private TokenType(String identifier, String regex) {
		this.identifier = identifier;;
		this.regex = regex;
	}
	
	public String getRegex() {
		return regex;
	}

	public String getIdentifier() {
		return identifier;
	}
}

