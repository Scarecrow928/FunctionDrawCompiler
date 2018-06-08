package parsing;

import lexicalAnalyze.TokenType;

public class SyntaxError extends Exception {
	public SyntaxError(String string) {
		super(string);
	}
}
