
public enum TokenType {
	T_ID("[a-zA-Z][a-zA-Z0-9_]*"),
	T_INTNUM("[0-9]+"),
	T_ASSIGNMENT(":="),
	T_COMMA(","),
	T_STAR("\\*"),
	T_PERCENT("%"),
	T_SEMICOLON(";"),
	T_PLUS("\\+"),
	T_MINUS("-"),
	T_UNKNOWN(".");
	
	private String regex;
	
	private TokenType(String regex) {
		this.regex = regex;
	}
	
	public String getRegex() {
		return regex;
	}
}

