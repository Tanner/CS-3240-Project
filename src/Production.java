import java.util.List;


public class Production {
	private Terminal terminal;
	private List<RuleElement> ruleElements;
	
	Production(Terminal terminal, List<RuleElement> ruleElements) {
		this.terminal = terminal;
		this.ruleElements = ruleElements;
	}

	public Terminal getTerminal() {
		return terminal;
	}

	public List<RuleElement> getRuleElements() {
		return ruleElements;
	}
	
	public static boolean productionListContainsTerminal(List<Production> productions, Terminal terminal) {
		for (Production p : productions) {
			if (p.getTerminal().equals(terminal)) {
				return true;
			}
		}
		
		return false;
 	}
}
