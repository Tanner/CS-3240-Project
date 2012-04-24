import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class LL1Grammar {
	private static Scanner grammarScanner;
	private ArrayList<Terminal> terminals;
	private ArrayList<Variable> variables;
	private Variable startVariable;
	private List<Rule> rules;
	
	public LL1Grammar(File grammarDescription) {
		try {
			grammarScanner = new Scanner(grammarDescription);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while (grammarScanner.hasNext()) {
			String[] line = grammarScanner.nextLine().split(" ");
			if (line[0].equalsIgnoreCase("%Tokens")) {
				terminals = new ArrayList<Terminal>();
				
				for (int i = 1; i < line.length; i++) {
					terminals.add(new Terminal(line[i]));
				}
			} else if (line[0].equalsIgnoreCase("%Non-terminals")) {
				variables = new ArrayList<Variable>();
				
				for (int i = 1; i < line.length; i++) {
					variables.add(new Variable(line[i]));
				}
			} else if (line[0].equalsIgnoreCase("%Start")) {
				startVariable = variableForIdentifier(line[1]);
				
				if (startVariable == null) {
					System.err.println("Critical Error: start non-terminal not in non-terminals list");
				}
			} else if (line[0].equalsIgnoreCase("%Rules")) {
				rules = new ArrayList<Rule>();
				
				while (grammarScanner.hasNext()) {
					String[] ruleLine = grammarScanner.nextLine().split(" ");
					
					Variable leftSide = variableForIdentifier(ruleLine[0]);
					ArrayList<RuleElement> rightSide = new ArrayList<RuleElement>();
					// start at two two skip colon
					for (int i = 2; i < ruleLine.length; i++) {
						if (ruleLine[i].equals("|")) {
							// new rule because of OR
							rules.add(new Rule(leftSide, rightSide));
							rightSide = new ArrayList<RuleElement>();
						} else {
							rightSide.add(ruleElementForIdentifier(ruleLine[i]));
						}
					}
					
					rules.add(new Rule(leftSide, rightSide));
				}
			}
		}
		
		removeLeftRecursion();
	}
	
	public void removeLeftRecursion() {
		ArrayList<Rule> newRules = new ArrayList<Rule>();
		for (Rule rule : rules) {
			if (rule.hasLeftRecursion()) {				
				String tailVariableIdentifier = rule.getLeftSide().getIdentifier().replace(">", "_tail>");
				Variable tail = new Variable(tailVariableIdentifier);
				variables.add(tail);
				List<RuleElement> tailRuleRightSide = new ArrayList<RuleElement>();
				for (int i = 1; i < rule.getRightSide().size(); i++) {
					tailRuleRightSide.add(rule.getRightSide().get(i));
				}
				tailRuleRightSide.add(tail);
				
				Rule tailRule = new Rule(tail, tailRuleRightSide);
				Rule tailEpsilonRule = new Rule(tail, new EmptyString());
				
				newRules.add(tailRule);
				newRules.add(tailEpsilonRule);
				
				List<Rule> siblingRules = rulesWithLeftSide(rule.getLeftSide());
				for (Rule sibling : siblingRules) {
					sibling.addToRightSide(tail);
				}
			} else {
				newRules.add(rule);
			}
		}
		
		rules = newRules;
	}
	
	public List<Rule> rulesWithLeftSide(Variable v) {
		ArrayList<Rule> rulesWithLeftSide = new ArrayList<Rule>();
		
		for (Rule rule : rules) {
			if (rule.getLeftSide() == v) {
				rulesWithLeftSide.add(rule);
			}
		}
		
		return rulesWithLeftSide;
	}
	
	public RuleElement ruleElementForIdentifier(String identifier) {
		RuleElement re = variableForIdentifier(identifier);
		if (re == null) {
			re = terminalForIdentifier(identifier);
		}
		
		return re;
	}
	
	public Variable variableForIdentifier(String identifier) {
		for (Variable v : variables) {
			if (v.getIdentifier().equalsIgnoreCase(identifier)) {
				return v;
			}
		}
		
		return null;
	}
	
	public Terminal terminalForIdentifier(String identifier) {
		for (Terminal t : terminals) {
			if (t.getIdentifier().equalsIgnoreCase(identifier)) {
				return t;
			}
		}
		
		return null;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Terminals: ");
		for (Terminal t : terminals) {
			sb.append(t + " ");
		}
		sb.append("\n");
		
		sb.append("Variables: ");
		for (Variable v : variables) {
			sb.append(v + " ");
		}
		sb.append("\n");

		sb.append("Start Variable: ");
		sb.append(startVariable);
		sb.append("\n");
		
		sb.append("Rules:\n");
		for (Rule r : rules) {
			sb.append(r);
			sb.append("\n");
		}
		
		return sb.toString();
	}

	public Variable getStartVariable() {
		return startVariable;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public List<Rule> getRules() {
		return rules;
	}
}
