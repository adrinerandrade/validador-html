package leitor.html;

public class InvalidHtmlFormatException extends RuntimeException {

	private final static String LINE_SUFIX = "%s: Line %d";
	
	public InvalidHtmlFormatException(String message, int line) {
		super(String.format(LINE_SUFIX, message, line));
	}
	
}
