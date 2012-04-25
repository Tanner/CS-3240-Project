import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class LL1ParserTest {
	@Test
	public void testLL1ParserTest1() throws LL1ParseException {
		testGrammar("tests/test1/grammar.txt", "tests/test1/test.txt");
	}
	
	@Test
	public void testLL1ParserTest2() throws LL1ParseException {
		testGrammar("tests/test2/grammar.txt", "tests/test2/test.txt");
	}
	
	@Test
	public void testLL1ParserTiny() throws LL1ParseException {
		testGrammar("tests/tiny/grammar.txt", "tests/tiny/test.txt");
	}

	public void testGrammar(String grammarPath, String pathToParse) throws LL1ParseException {
		LL1Parser parser = new LL1Parser(new File(grammarPath), new File(pathToParse));
		assertTrue(parser.parse());
	}
}