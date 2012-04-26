/**
 * Token with a name and a TokenType.
 */
public class Token {
	private String string;
	private TokenType type;
	
	/**
	 * Construct a new Token with a String.
	 * @param string String
	 */
	public Token(String string) {
		this.string = string;
		this.type = identifyType(string);
	}
	
	/**
	 * Identify the TokenType from the String value.
	 * @param string String to determine TokenType from
	 * @return Resulting TokenType
	 */
	private TokenType identifyType(String string) {
		for (TokenType type : TokenType.values()) {
			if (string.matches(type.getRegex())) {
				return type;
			}
		}
		
		return TokenType.T_UNKNOWN;
	}
	
	/**
	 * Return the TokenType of the Token.
	 * @return TokenType
	 */
	public TokenType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return string;
	}
}
