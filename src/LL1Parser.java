import java.io.File;
import java.util.List;
import java.util.Stack;


public class LL1Parser {
	private LL1Grammar grammar;
	private LL1ParsingTable parsingTable;
	private LL1Lexer lexer;
	
	public static boolean VERBOSE = false;
	
	public static void main(String[] args) {
		if (args.length >= 2 && args.length <= 3) {
			File grammarFile = new File(args[0]);
			File fileToParse = new File(args[1]);
			
			if (args.length >= 3 && args[2].equals("-pt")) {
				VERBOSE = true;
			}
			
			LL1Parser parser = new LL1Parser(grammarFile, fileToParse);
			try {
				parser.parse();
			} catch (LL1ParseException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Incorrect usage. Required arguments: [grammar file] [file to parse]");
		}
	}
	
	public LL1Parser(File grammarFile, File fileToParse) {
		try {
			grammar = new LL1Grammar(grammarFile);
		} catch (LL1GrammarException e) {
			e.printStackTrace();
		}
		
		if (VERBOSE) {
			System.out.println(grammar);
		}
		
		parsingTable = new LL1ParsingTable(grammar);
		lexer = new LL1Lexer(fileToParse);
	}
	
	public boolean parse() throws LL1ParseException {
		Stack<RuleElement> parsingStack = new Stack<RuleElement>();
		parsingStack.push(grammar.getStartVariable());
		
		Token token = lexer.next();
		while (!parsingStack.isEmpty()) {
			if (VERBOSE) {
				System.out.println(parsingStack);
			}
			RuleElement re = parsingStack.pop();
			if (VERBOSE) {
				System.out.println("Popped " + re);
			}
			if (re instanceof Variable) {
				Variable v = (Variable)re;
				List<RuleElement> newRuleElements = parsingTable.getRuleElements(v, token.getType());
				
				if (newRuleElements != null) {
					for (int i = newRuleElements.size() - 1; i >= 0; i--) {
						parsingStack.push(newRuleElements.get(i));
					}
				} else {
					throw new LL1ParseException("Parsing failed with token of type " + token.getType() + " and stack: " + parsingStack);
				}
			} else if (re instanceof Terminal && !(re instanceof EmptyString)) {
				Terminal t = (Terminal)re;
				TokenType tokenType = TokenType.tokenWithIdentifier(t.toString());
				if (tokenType == token.getType()) {
					if (parsingStack.isEmpty()) {
						// if token is matched and parsing stack is empty, no need to go further
						break;
					}
					
					if (VERBOSE) {
						System.out.println("Parsed " + token);
					}
					token = lexer.next();
				} else {
					throw new LL1ParseException("Unexpected " + token.getType() + " (expected "+ tokenType + ")");
				}
			}
		}
		
		if (lexer.hasNext()) {
			throw new LL1ParseException("Parsing ended with input remaining");
		}
		
		System.out.println("Successful parse!");
		return true;
	}
}
