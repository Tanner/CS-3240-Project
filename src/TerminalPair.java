import java.util.List;

/**
 * A simple pairing of a Terminal and a List of RuleElements.
 */
public class TerminalPair {
	private Terminal terminal;
	private List<RuleElement> ruleElements;
	
	/**
	 * Construct a new TerminalPair with the given Terminal and RuleElements.
	 * @param terminal Terminal
	 * @param ruleElements List of RuleElements
	 */
	public TerminalPair(Terminal terminal, List<RuleElement> ruleElements) {
		this.terminal = terminal;
		this.ruleElements = ruleElements;
	}

	/**
	 * Get the Terminal.
	 * @return Terminal
	 */
	public Terminal getTerminal() {
		return terminal;
	}

	/**
	 * Get the RuleElements.
	 * @return List of RuleElements
	 */
	public List<RuleElement> getRuleElements() {
		return ruleElements;
	}
	
	/**
	 * Determine if a List of TerminalPairs contains a specific terminal.
	 * @param terminalPairs List of TerminalPairs to search
	 * @param terminal Terminal to search for
	 * @return True if Terminal is in the List of TerminalPairs
	 */
	public static boolean terminalPairListContainsTerminal(List<TerminalPair> terminalPairs, Terminal terminal) {
		for (TerminalPair p : terminalPairs) {
			if (p.getTerminal().equals(terminal)) {
				return true;
			}
		}
		
		return false;
 	}
	
	@Override
	public String toString() {
		return terminal + ", " + ruleElements;
	}
}
