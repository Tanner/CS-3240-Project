/**
 * RuleElement that has a String identifier.
 */
public class RuleElement {
	private String identifier;
	
	/**
	 * Construct a new RuleElement with a String identifier.
	 * @param identifier String identifier
	 */
	public RuleElement(String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * Return the identifier of the RuleElement.
	 * @return Identifier of the RuleElement
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public String toString() {
		return identifier;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof RuleElement) {
			RuleElement other = (RuleElement)o;
			
			return other.identifier.equals(identifier);
		}
		
		return false;
	}
}
