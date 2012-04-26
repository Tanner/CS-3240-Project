/**
 * Terminal that has a String identifier.
 */
public class Terminal extends RuleElement {
	/**
	 * Construct a new Terminal with a String identifier.
	 * @param identifier String identifier
	 */
	public Terminal(String identifier) {
		super(identifier);
	}
	
	/**
	 * Return the TokenType from the Identifier for this Terminal.
	 * @return TokenType
	 */
	public TokenType tokenType() {
		for (TokenType tt : TokenType.values()) {
			if (tt.getIdentifier().equals(getIdentifier())) {
				return tt;
			}
		}
		
		return TokenType.T_UNKNOWN;
	}
	
}
