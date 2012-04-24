import java.io.File;
import java.util.List;
import java.util.Stack;


public class LL1Parser {
	private LL1Grammar grammar;
	private LL1ParsingTable parsingTable;
	private LL1Lexer lexer;
	
	public static void main(String[] args) {
		if (args.length == 2) {
			File grammarFile = new File(args[0]);
			File fileToParse = new File(args[1]);
			
			LL1Parser parser = new LL1Parser(grammarFile, fileToParse);
			try {
				parser.parse();
			} catch (LL1ParseException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Incorrect usage. Required argument filename not provided.");
		}
	}
	
	public LL1Parser(File grammarFile, File fileToParse) {
		grammar = new LL1Grammar(grammarFile);
		System.out.println(grammar);
		parsingTable = new LL1ParsingTable(grammar);
		lexer = new LL1Lexer(fileToParse);
	}
	
	public void parse() throws LL1ParseException {
		Stack<RuleElement> parsingStack = new Stack<RuleElement>();
		parsingStack.push(grammar.getStartVariable());
		
		while (lexer.hasNext()) {
			Token token = lexer.next();
			RuleElement re = parsingStack.pop();
			if (re instanceof Variable) {
				Variable v = (Variable)re;
				List<RuleElement> newRuleElements = parsingTable.getRuleElements(v, token.getType());
				
				if (newRuleElements != null) {
					if (!(newRuleElements.get(0) instanceof EmptyString)) {
						for (int i = 0; i < newRuleElements.size(); i++) {
							parsingStack.push(newRuleElements.get(i));
						}
					}
				} else {
					throw new LL1ParseException("Parsing failed with token " + token.getType());
				}
				System.out.println(lexer.next().getType());
			}
		}
	}
}
