import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates an LL1 Parsing Table.
 */
public class LL1ParsingTable {
	private Map<Variable, List<Production>> first;
	private Map<Variable, List<Production>> follow;
	private Map<Variable, Map<Terminal, List<RuleElement>>> table;
	private LL1Grammar grammar;

	/**
	 * Constructs and builds a new LL1 Parsing Table.
	 * @param grammar Given LL1 Grammar to build the table with.
	 */
	public LL1ParsingTable(LL1Grammar grammar) {
		this.grammar = grammar;
		
		// First you must determine the First set
		first = first(grammar.getRules());
		
		if (LL1Parser.VERBOSE) {
			System.out.println("First:");
			for (Variable v : grammar.getVariables()) {
				System.out.print(v);
				List<Production> productions = first.get(v);
				for (Production p : productions) {
					System.out.print(" " + p.getTerminal());
				}
				System.out.println();
			}
			System.out.println();
		}
		
		// Then you can build the Follow set
		follow = follow(grammar.getRules(), first, grammar.getStartVariable(), grammar.getEndTerminal());
		
		if (LL1Parser.VERBOSE) {
			System.out.println("Follow:");
			for (Variable v : grammar.getVariables()) {
				System.out.print(v);
				List<Production> productions = follow.get(v);
				for (Production p : productions) {
					System.out.print(" " + p.getTerminal());
				}
				System.out.println();
			}
			System.out.println();
		}
		
		// Finally you can build the Parsing Table with the First and Follow sets
		table = generateParsingTable(grammar.getVariables(), first, follow);
		
		if (LL1Parser.VERBOSE) {
			System.out.println("Table:");
			
			int largestTextSize = 0;
			for (Variable v : grammar.getVariables()) {
				for (Terminal t : grammar.getTerminals()) {					
					List entry = table.get(v).get(t);
					if (entry != null) {
						largestTextSize = entry.toString().length() > largestTextSize ? entry.toString().length() : largestTextSize;
					}
				}
			}
			largestTextSize += 2;
			
			String format = "%" + largestTextSize + "s";			
			System.out.printf(format, "");
			for (Terminal t : grammar.getTerminals()) {
				if (t.isEmptyString()) {
					continue;
				}
				
				System.out.printf(format, t);
			}
			System.out.println();
			for (Variable v : grammar.getVariables()) {
				System.out.printf(format, v);
	
				for (Terminal t : grammar.getTerminals()) {
					if (t.isEmptyString()) {
						continue;
					}
					
					System.out.printf(format, table.get(v).get(t));
				}
				
				System.out.println();
			}
			System.out.println();
		}
	}

	private static Map<Variable, Map<Terminal, List<RuleElement>>> generateParsingTable(
			List<Variable> variables,
			Map<Variable, List<Production>> first,
			Map<Variable, List<Production>> follow) {
		Map<Variable, Map<Terminal, List<RuleElement>>> parsingTable = new HashMap<Variable, Map<Terminal, List<RuleElement>>>();
		
		for (Variable v : variables) {
			Map<Terminal, List<RuleElement>> column = new HashMap<Terminal, List<RuleElement>>();
			
			List<Production> firstOfVariable = first.get(v);
			
			for (Production firstP : firstOfVariable) {
				Terminal firstT = firstP.getTerminal();
				if (firstT.isEmptyString()) {
					List<Production> followOfVariable = follow.get(v);
					
					for (Production followP : followOfVariable) {
						Terminal followT = followP.getTerminal();
						column.put(followT, followP.getRuleElements());
					}
				} else {
					column.put(firstT, firstP.getRuleElements());
				}
			}
			
			parsingTable.put(v, column);
		}
		
		return parsingTable;
	}

	private static Map<Variable, List<Production>> first(List<Rule> rules) {
		HashMap<Variable, List<Production>> firstSet = new HashMap<Variable, List<Production>>();
		
		int i = -1;
		boolean changed;
		do {
			i++;
			changed = false;
			
			for (Rule r : rules) {
				Variable v = r.getLeftSide();
				
				List<Production> firstProductionList;
				if (firstSet.containsKey(v)) {
					firstProductionList = firstSet.get(v);
				} else {
					firstProductionList = new ArrayList<Production>();
				}
				
				for (RuleElement re : r.getRightSide()) {
					if (re instanceof Terminal) {
						if (!Production.productionListContainsTerminal(firstProductionList, (Terminal)re)) {
							firstProductionList.add(new Production((Terminal)re, r.getRightSide()));
							changed = true;
						}
						break;
					} else if (i > 0 && re instanceof Variable) {
						List<Production> variableFirstList = firstSet.get(re); 
						
						if (variableFirstList != null && variableFirstList.size() > 0) {
							for (Production p : variableFirstList) {
								Terminal t = p.getTerminal();
								if (!Production.productionListContainsTerminal(firstProductionList, t)) {
									firstProductionList.add(new Production(t, r.getRightSide()));
									changed = true;
								}
							}
							break;
						}
					} else {
						break;
					}
				}
				
				if (i > 0 && firstProductionList.size() == 0) {
					firstProductionList.add(new Production(new EmptyString(), r.getRightSide()));
					changed = true;
				}
				
				if (firstSet.containsKey(v)) {
					firstSet.remove(v);
					firstSet.put(v, firstProductionList);
				} else {
					firstSet.put(v, firstProductionList);
				}
			}
		} while (changed || i < 3);
		
		return firstSet;
	}

	private static Map<Variable, List<Production>> follow(List<Rule> rules, Map<Variable, List<Production>> first, Variable startVariable, Terminal endTerminal) {
		HashMap<Variable, List<Production>> followSet = new HashMap<Variable, List<Production>>();
		
		List<Production> startVariableTerminalList = new ArrayList<Production>();
		List<RuleElement> emptyStringRuleElementList = new ArrayList<RuleElement>();
		emptyStringRuleElementList.add(new EmptyString());
		
		Production startProduction = new Production(endTerminal, emptyStringRuleElementList);
		startVariableTerminalList.add(startProduction);
		followSet.put(startVariable, startVariableTerminalList);
		
		boolean changed;
		do {
			changed = false;
			
			for (Rule r : rules) {				
				for (int j = 0; j < r.getRightSide().size(); j++) {
					RuleElement re = r.getRightSide().get(j);
					if (re instanceof Variable) {
						Variable v = (Variable)re;
						
						List<Production> followProductionList;
						if (followSet.containsKey(v)) {
							followProductionList = followSet.get(v);
						} else {
							followProductionList = new ArrayList<Production>();
						}
						
						int k = j + 1;
						for (k = j + 1; k < r.getRightSide().size(); k++) {
							if (r.getRightSide().get(k) instanceof Variable) {
								RuleElement nextVariable = r.getRightSide().get(k);
								List<Production> firstOfNextVariable = first.get(nextVariable);
								
								for (Production p : firstOfNextVariable) {
									Terminal t = p.getTerminal();
									if (!t.isEmptyString() && !Production.productionListContainsTerminal(followProductionList, t)) {
										followProductionList.add(new Production(t, emptyStringRuleElementList));
										changed = true;
									}									
								}
								
								if (!Production.productionListContainsTerminal(firstOfNextVariable, new EmptyString())) {
									break;
								}
							} else if (r.getRightSide().get(k) instanceof Terminal) {
								Terminal t = (Terminal)r.getRightSide().get(k);
								if (!Production.productionListContainsTerminal(followProductionList, t)) {
									followProductionList.add(new Production(t, emptyStringRuleElementList));
									changed = true;
								}
								
								break;
							}
						}
						
						if (k == r.getRightSide().size()) {
							List<Production> followOfVariable = followSet.get(r.getLeftSide());
							
							if (followOfVariable == null) {
								changed = true;
							} else {
								for (Production p : followOfVariable) {
									Terminal t = p.getTerminal();
									if (!Production.productionListContainsTerminal(followProductionList, t)) {
										followProductionList.add(new Production(t, emptyStringRuleElementList));
										changed = true;
									}									
								}
							}
						}
						
						if (followSet.containsKey(v)) {
							followSet.remove(v);
							followSet.put(v, followProductionList);
						} else {
							followSet.put(v, followProductionList);
						}	
					}
				}
			}
		} while (changed);
		
		return followSet;
	}

	public List<RuleElement> getRuleElements(Variable v, TokenType t) {
		Map<Terminal, List<RuleElement>> column = table.get(v);
		Terminal terminal = grammar.terminalForIdentifier(t.getIdentifier());
		List<RuleElement> ruleElements = column.get(terminal);
		return ruleElements;
	}
	
}
