import java.util.Formatter;

public class Table {
	private static final String rowSeparator = "-";
	private static final String columnSeparator = "|";
	
	public static String createTable(String[][] data, int padding, boolean headers) {
		int rows = data.length;
		int cols = data[0].length;
		
		Formatter output = new Formatter();
		
		int[] colSize = new int[cols];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (data[r][c].length() > colSize[c]) {
					colSize[c] = data[r][c].length();
				}
			}
		}
		
		String[] rowsText = new String[rows];
		for (int r = 0; r < rows; r++) {
			Formatter rowText = new Formatter();
			
			for (int c = 0; c < cols; c++) {
				rowText.format(repeatString(" ", padding));
				rowText.format("%" + ((headers && c == 0) ? "-" : "") + colSize[c] + "s", data[r][c]);
				rowText.format(repeatString(" ", padding));
				
				if (c == 0 && headers || c != cols - 1) {
					rowText.format(columnSeparator);
				}
			}
			
			rowsText[r] = rowText.toString();
		}
		
		int rowSize = rowsText[0].length();
		
		for (int r = 0; r < rows; r++) {
			if (r == 1 && headers) {
				output.format(repeatString(rowSeparator, rowSize));
			}
			
			output.format("\n");
			output.format(rowsText[r]);
			output.format("\n");
			
			if (r != 0 && headers) {
				for (int i = 0; i < rowsText[r].length(); i++) {
					if (rowsText[r].charAt(i) == columnSeparator.charAt(0) && r != rows - 1) {
						output.format(columnSeparator);
					} else {
						output.format(" ");
					}
				}
			}
		}
		
		return output.toString();
	}
	
	private static String repeatString(String string, int numberOfTimes) {
		return new String(new char[numberOfTimes]).replace("\0", string);
	}
}
