/**
 * Custom Exception for LL1 Parsing use only.
 */
public class LL1ParseException extends Exception {
	/**
	 * Construct a new LL1 Parse Exception with the given message.
	 * @param string Message
	 */
	public LL1ParseException(String string) {
		super(string);
	}
}