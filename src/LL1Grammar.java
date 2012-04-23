import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class LL1Grammar {
	private static Scanner grammarScanner;
	private ArrayList<Terminal> terminals;
	private ArrayList<Variable> variables;
	private Variable start;
	private ArrayList<Rule> rules;
	private HashMap<Terminal, String> nonTerminalStrings;
	
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
				for (Variable v : variables) {
					if (v.getIdentifier().equals(line[1])) {
						start = v;
					}
				}
				
				if (start == null) {
					System.err.println("Critical Error: start non-terminal not in non-terminals list");
				}
			} else if (line[0].equalsIgnoreCase("%Rules")) {
				rules = new ArrayList<Rule>();
			}
		}
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
		sb.append(start);
		sb.append("\n");
		
		sb.append("Rules:");
		for (Rule r : rules) {
			sb.append(r);
		}
		sb.append("\n");
		
		return sb.toString();
	}
}
