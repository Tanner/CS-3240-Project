
public class RuleElement {
	private String identifier;
	
	public RuleElement(String identifier) {
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String toString() {
		return identifier;
	}
	
	public boolean equals(Object o) {
		if (o instanceof RuleElement) {
			RuleElement other = (RuleElement)o;
			
			return other.identifier.equals(identifier);
		}
		
		return false;
	}
}
