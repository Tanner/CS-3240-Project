
public class Terminal extends RuleElement {

	public Terminal(String identifier) {
		super(identifier);
	}	
	
	public TokenType tokenType() {
		for (TokenType tt : TokenType.values()) {
			if (tt.getIdentifier().equals(getIdentifier())) {
				return tt;
			}
		}
		
		return TokenType.T_UNKNOWN;
	}
	
}
