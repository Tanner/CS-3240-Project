
public class Token {
	private String string;
	private TokenType type;
	
	public Token(String string) {
		this.string = string;
		this.type = identifyType(string);
	}
	
	private TokenType identifyType(String string) {
		for (TokenType type : TokenType.values()) {
			if (string.matches(type.getRegex())) {
				return type;
			}
		}
		
		return TokenType.T_UNKNOWN;
	}
	
	public String toString() {
		return string;
	}
	
	public TokenType getType() {
		return type;
	}
}
