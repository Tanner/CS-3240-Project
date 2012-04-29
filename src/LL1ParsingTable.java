import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates an LL1 Parsing Table.
 */
public class LL1ParsingTable {
	private Map<Variable, List<TerminalPair>> first;
	private Map<Variable, List<TerminalPair>> follow;
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
				List<TerminalPair> productions = first.get(v);
				for (TerminalPair p : productions) {
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
				List<TerminalPair> productions = follow.get(v);
				for (TerminalPair p : productions) {
					System.out.print(" " + p.getTerminal());
				}
				System.out.println();
			}
			System.out.println();
		}
		
		// Finally you can build the Parsing Table with the First and Follow sets
		table = generateParsingTable(grammar.getVariables(), first, follow);
				
		List<Variable> variables = grammar.getVariables();
		List<Terminal> terminals = grammar.getTerminals();
		
		terminals.remove(new EmptyString());
		
		int numberVariables = variables.size();
		int numberTerminals = terminals.size();
		String[][] data = new String[numberVariables + 1][numberTerminals + 1];
		
		for (int r = 0; r < data.length; r++) {
			for (int c = 0; c < data[r].length; c++) {
				if (r == 0 && c == 0) {
					data[r][c] = "";
					continue;
				} else if (r == 0) {
					data[r][c] = terminals.get(c - 1).toString();
				} else if (c == 0) {
					data[r][c] = variables.get(r - 1).toString(); 
				} else {
					List<RuleElement> entry = table.get(variables.get(r - 1)).get(terminals.get(c - 1));
					
					if (entry != null) {
						data[r][c] = entry.toString();
					} else {
						data[r][c] = "";
					}
				}
			}
		}
		
		String tableText = Table.createTable(data, 2, true);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("parsing_table.txt");
			writer.print(tableText);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if (LL1Parser.VERBOSE) {
			System.out.println("Table:");
			System.out.println(tableText);
		}
	}

	private static Map<Variable, Map<Terminal, List<RuleElement>>> generateParsingTable(
			List<Variable> variables,
			Map<Variable, List<TerminalPair>> first,
			Map<Variable, List<TerminalPair>> follow) {
		Map<Variable, Map<Terminal, List<RuleElement>>> parsingTable = new HashMap<Variable, Map<Terminal, List<RuleElement>>>();
		
		for (Variable v : variables) {
			Map<Terminal, List<RuleElement>> column = new HashMap<Terminal, List<RuleElement>>();
			
			List<TerminalPair> firstOfVariable = first.get(v);
			
			for (TerminalPair firstP : firstOfVariable) {
				Terminal firstT = firstP.getTerminal();
				if (firstT.isEmptyString()) {
					List<TerminalPair> followOfVariable = follow.get(v);
					
					for (TerminalPair followP : followOfVariable) {
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

	private static Map<Variable, List<TerminalPair>> first(List<Rule> rules) {
		HashMap<Variable, List<TerminalPair>> firstSet = new HashMap<Variable, List<TerminalPair>>();
		
		int i = -1;
		boolean changed;
		do {
			i++;
			changed = false;
			
			for (Rule r : rules) {
				Variable v = r.getLeftSide();
				
				List<TerminalPair> firstProductionList;
				if (firstSet.containsKey(v)) {
					firstProductionList = firstSet.get(v);
				} else {
					firstProductionList = new ArrayList<TerminalPair>();
				}
				
				for (RuleElement re : r.getRightSide()) {
					if (re instanceof Terminal) {
						if (!TerminalPair.productionListContainsTerminal(firstProductionList, (Terminal)re)) {
							firstProductionList.add(new TerminalPair((Terminal)re, r.getRightSide()));
							changed = true;
						}
						break;
					} else if (i > 0 && re instanceof Variable) {
						List<TerminalPair> variableFirstList = firstSet.get(re); 
						
						if (variableFirstList != null && variableFirstList.size() > 0) {
							for (TerminalPair p : variableFirstList) {
								Terminal t = p.getTerminal();
								if (!TerminalPair.productionListContainsTerminal(firstProductionList, t)) {
									firstProductionList.add(new TerminalPair(t, r.getRightSide()));
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
					firstProductionList.add(new TerminalPair(new EmptyString(), r.getRightSide()));
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

	private static Map<Variable, List<TerminalPair>> follow(List<Rule> rules, Map<Variable, List<TerminalPair>> first, Variable startVariable, Terminal endTerminal) {
		HashMap<Variable, List<TerminalPair>> followSet = new HashMap<Variable, List<TerminalPair>>();
		
		List<TerminalPair> startVariableTerminalList = new ArrayList<TerminalPair>();
		List<RuleElement> emptyStringRuleElementList = new ArrayList<RuleElement>();
		emptyStringRuleElementList.add(new EmptyString());
		
		TerminalPair startProduction = new TerminalPair(endTerminal, emptyStringRuleElementList);
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
						
						List<TerminalPair> followProductionList;
						if (followSet.containsKey(v)) {
							followProductionList = followSet.get(v);
						} else {
							followProductionList = new ArrayList<TerminalPair>();
						}
						
						int k = j + 1;
						for (k = j + 1; k < r.getRightSide().size(); k++) {
							if (r.getRightSide().get(k) instanceof Variable) {
								RuleElement nextVariable = r.getRightSide().get(k);
								List<TerminalPair> firstOfNextVariable = first.get(nextVariable);
								
								for (TerminalPair p : firstOfNextVariable) {
									Terminal t = p.getTerminal();
									if (!t.isEmptyString() && !TerminalPair.productionListContainsTerminal(followProductionList, t)) {
										followProductionList.add(new TerminalPair(t, emptyStringRuleElementList));
										changed = true;
									}									
								}
								
								if (!TerminalPair.productionListContainsTerminal(firstOfNextVariable, new EmptyString())) {
									break;
								}
							} else if (r.getRightSide().get(k) instanceof Terminal) {
								Terminal t = (Terminal)r.getRightSide().get(k);
								if (!TerminalPair.productionListContainsTerminal(followProductionList, t)) {
									followProductionList.add(new TerminalPair(t, emptyStringRuleElementList));
									changed = true;
								}
								
								break;
							}
						}
						
						if (k == r.getRightSide().size()) {
							List<TerminalPair> followOfVariable = followSet.get(r.getLeftSide());
							
							if (followOfVariable == null) {
								changed = true;
							} else {
								for (TerminalPair p : followOfVariable) {
									Terminal t = p.getTerminal();
									if (!TerminalPair.productionListContainsTerminal(followProductionList, t)) {
										followProductionList.add(new TerminalPair(t, emptyStringRuleElementList));
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
