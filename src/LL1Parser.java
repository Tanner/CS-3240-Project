import java.io.File;


public class LL1Parser {
	LL1Scanner scanner;
	
	public static void main(String[] args) {
		if (args.length == 1) {
			File fileToParse = new File(args[0]);
			LL1Parser parser = new LL1Parser(fileToParse);
			parser.start();
		} else {
			System.err.println("Incorrect usage. Required argument filename not provided.");
		}
	}
	
	public LL1Parser(File fileToParse) {
		scanner = new LL1Scanner(fileToParse);
	}
	
	public void start() {
		while (scanner.hasNext()) {
			System.out.println(scanner.next().getType());
		}
	}
}
