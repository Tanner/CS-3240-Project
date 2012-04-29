import java.util.List;


public class TerminalPair {
	private Terminal terminal;
	private List<RuleElement> ruleElements;
	
	TerminalPair(Terminal terminal, List<RuleElement> ruleElements) {
		this.terminal = terminal;
		this.ruleElements = ruleElements;
	}

	public Terminal getTerminal() {
		return terminal;
	}

	public List<RuleElement> getRuleElements() {
		return ruleElements;
	}
	
	public static boolean productionListContainsTerminal(List<TerminalPair> productions, Terminal terminal) {
		for (TerminalPair p : productions) {
			if (p.getTerminal().equals(terminal)) {
				return true;
			}
		}
		
		return false;
 	}
	
	public String toString() {
		return terminal + ", " + ruleElements;
	}
}
