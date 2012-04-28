import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * LL1 Lexer that reads a file and generates Tokens.
 */
public class LL1Lexer {
	private Scanner scanner;
	private Queue<Token> tokenBuffer;
	private File outputFile;
	
	/**
	 * Construct a new LL1 LExer with the given file.
	 * @param f File to perform lexical analysis on.
	 */
	public LL1Lexer(File f) {
		try {
			scanner = new Scanner(f);
			tokenBuffer = new LinkedList<Token>();
			outputFile = new File(f.getName().substring(0, f.getName().lastIndexOf('.')) + ".tok");
			writeToOutputFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a Token to the tokenBuffer based off the given String.
	 * @param s String to tokenize and add to the tokenBuffer
	 */
	public void fillTokenBuffer(String s) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < s.length(); i++) {
			// Loop through all characters in the string
			char c = s.charAt(i);
			
			if (c == ' ' || c == ',' || c == ';' || c == '&' || c == '+' || c == '-' || c == '(' || c == ')' || c == '%' || c == '*') {
				if (sb.toString().length() > 0) {
					tokenBuffer.add(new Token(sb.toString()));
				}
				tokenBuffer.add(new Token(new Character(c).toString()));
				sb.setLength(0);
			} else if (c == ':' && i + 1 < s.length() && s.charAt(i + 1) == '=') {
				if (sb.toString().length() > 0) {
					tokenBuffer.add(new Token(sb.toString()));
				}
				tokenBuffer.add(new Token(":="));
				i++;
				sb.setLength(0);
			} else {
				sb.append(c);
				
				if (i == s.length() - 1) {
					tokenBuffer.add(new Token(sb.toString()));
				}
			}
		}
	}
	
	private void writeToOutputFile() {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while (hasNext()) {
			writer.print(next().getType().getIdentifier() + " ");
		}
		
		writer.flush();
		writer.close();
	}
	
	/**
	 * Determine if the lexer has anything left to parse.
	 * @return Return true when the Scanner has items left or the tokenBuffer is not empty
	 */
	private boolean hasNext() {
		return scanner.hasNext() || !tokenBuffer.isEmpty();
	}
	
	/**
	 * Step through the Scanner until the tokenBuffer contains something.
	 * @return Return the first Token inserted in the tokenBuffer
	 */
	private Token next() {
		if (!scanner.hasNext() && tokenBuffer.isEmpty()) {
			return null;
		}
		
		while (tokenBuffer.isEmpty()) {
			fillTokenBuffer(scanner.next());
		}
		
		return tokenBuffer.remove();
	}

	public File getOutputFile() {
		return outputFile;
	}
}
