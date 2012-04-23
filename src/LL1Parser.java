import java.io.File;


public class LL1Parser {
	LL1Grammar grammar;
	LL1Scanner scanner;
	
	public static void main(String[] args) {
		if (args.length == 2) {
			File grammarFile = new File(args[0]);
			File fileToParse = new File(args[1]);
			
			LL1Parser parser = new LL1Parser(grammarFile, fileToParse);
			parser.start();
		} else {
			System.err.println("Incorrect usage. Required argument filename not provided.");
		}
	}
	
	public LL1Parser(File grammarFile, File fileToParse) {
		grammar = new LL1Grammar(grammarFile);
		scanner = new LL1Scanner(fileToParse);
	}
	
	public void start() {
		System.out.println(grammar);
		
		while (scanner.hasNext()) {
			System.out.println(scanner.next().getType());
		}
	}
}
