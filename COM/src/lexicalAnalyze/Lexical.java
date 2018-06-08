package lexicalAnalyze;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.rowset.CachedRowSet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.asm.Label;
import com.alibaba.fastjson.asm.MethodWriter;
import java.lang.Character;
import java.lang.reflect.Array;

public class Lexical {
	private FileReader fileReader;
	private int state;
	private StringBuffer tokenBuffer;
	private String fileName;

	public static String LETTER = "[A-Za-z]";
	public static String DIGIT = "[0-9]";
	public static String SEMICO = ";";
	public static String LEFT_BRACKET = "\\(";
	public static String RIGHT_BRACKET = "\\)";
	public static String COMMA = ",";
	public static String DOT = "\\.";
	public static String PLUS = "\\+";
	public static String MINUS = "\\-";
	public static String MUL = "\\*";
	public static String DIV = "/";
	public static String SPACE = "\\s";
	public static String EOF = "\\u0000";
	public static String ERRORTOKEN = ".*";
	public static String NEWLINE = "\\n";
	public static String[] REGEXS = {LETTER, DIGIT, SEMICO, COMMA, LEFT_BRACKET, RIGHT_BRACKET, DOT, PLUS,
			MINUS, MUL, DIV, NEWLINE, SPACE, EOF};
	public static final int SIN = 0;
	public static final int COS = 1;
	public static final int TAN = 2;
	public static final int EXP = 3;
	public static final int LN = 4;
	public static final int SQRT = 5;
	

	public Lexical(String filename) throws IOException {
		super();
		this.fileName = filename;
		this.fileReader = new FileReader(filename);
		this.state = 0;
		this.tokenBuffer = new StringBuffer();
	}

	public Token getToken() throws IOException {
		while (true) {
			String next = String.valueOf(fileReader.next());
			match(next);
			
			if (state >= 0 && state <= 8) {
				//重新开始读取
				tokenBuffer.append(next);
			} else if (state == 9) {
				//注释完成
				state = 0;
				tokenBuffer = new StringBuffer();
			} else if (state == -1) {
				//读取完成一个符号
				fileReader.back();
				state = 0;
				String tokenString = tokenBuffer.toString().toLowerCase();
				tokenBuffer = new StringBuffer();
				return generatToken(tokenString);
			} else if (state == -2) {
				//空格
				state = 0;
				continue;
			} else if (state == -3) {
				//文件结尾
				return new Token(TokenType.EOF, next, 0D);
			} else if (state == -4) {
				//错误符号
				state = 0;
				return new Token(TokenType.ERRTOKEN, next, 0D);
			}
		}
	}
	
	private void nextState(String in) {
		switch (state) {
		case 0:
			if (in.equals(LETTER)) {
				state = 1;
			} else if (in.equals(DIGIT)) {
				state = 2;
			} else if (in.equals(MUL)) {
				state = 4;
			} else if (in.equals(DIV)) {
				state = 6;
			} else if (in.equals(MINUS)) {
				state = 7;
			} else if (in.equals(PLUS) || in.equals(COMMA) || in.equals(SEMICO) || in.equals(LEFT_BRACKET) || in.equals(RIGHT_BRACKET)) {
				state = 5;
			} else if (in.equals(EOF)) {
				state = -3;
			} else if (in.equals(SPACE) || in.equals(NEWLINE)){
				state = -2;
			} else {
				state = -4;
			}
			break;
		case 1:
			if (in.equals(LETTER) || in.equals(DIGIT)) {
				state = 1;
			} else {
				state = -1;
			}
			break;
		case 2:
			if (in.equals(DIGIT)) {
				state = 2;
			} else if (in.equals(DOT)) {
				state = 3;
			} else {
				state = -1;
			}
			break;
		case 3:
			if (in.equals(DIGIT)) {
				state = 3;
			} else {
				state = -1;
			}
			break;
		case 4:
			if (in.equals(MUL)) {
				state = 5;
			} else {
				state = -1;
			}
			break;
		case 5:
			state = -1;
			break;
		case 6:
			if (in.equals(DIV)) {
				state = 8;
			} else {
				state = -1;
			}
			break;
		case 7:
			if (in.equals(MINUS)) {
				state = 8;
			} else {
				state = -1;
			}
			break;
		case 8:
			if (in.equals(NEWLINE)){
				state = 9;
			} else if (in.equals(EOF)) {
				state = -3;
			} else {
				state = 8;
			}
			break;
		case 9:
			break;
		case -1:
			break;
		case -2:
			break;
		default:
			state = -2;
		}
	}
	
	private void match(String next) {
		for(String s: REGEXS) {
			if (next.matches(s)) {
				nextState(s);
				return ;
			}
		}
		
		if(next.matches(ERRORTOKEN)) {
			nextState(ERRORTOKEN);
		}
	}
	
	private Token generatToken(String string) {
		switch (string) {
		case "origin":
			return new Token(TokenType.ORIGIN, string, 0D);
		
		case "scale":
			return new Token(TokenType.SCALE, string, 0D);

		case "rot":
			return new Token(TokenType.ROT, string, 0D);
			
		case "is":
			return new Token(TokenType.IS, string, 0D);
			
		case "for":
			return new Token(TokenType.FOR, string, 0D);
			
		case "from":
			return new Token(TokenType.FROM, string, 0D);
			
		case "to":
			return new Token(TokenType.TO, string, 0D);
			
		case "step":
			return new Token(TokenType.STEP, string, 0D);
			
		case "draw":
			return new Token(TokenType.DRAW, string, 0D);
			
		case "sin":
		case "cos":
		case "tan":
		case "ln":
		case "exp":
		case "sqrt":
			return new Token(TokenType.FUNC, string, 0D, funcGenerator(string));

		case "pi":
			return new Token(TokenType.CONST_ID, string, Math.PI);
			
		case "e":
			return new Token(TokenType.CONST_ID, string, Math.E);
			
		case "t":
			return new Token(TokenType.T, string, 0D);
			
		case ";":
			return new Token(TokenType.SEMICO, string, 0D);
			
		case "(":
			return new Token(TokenType.L_BRACKET, string, 0D);
			
		case ")":
			return new Token(TokenType.R_BRACKET, string, 0D);
			
		case ",":
			return new Token(TokenType.COMMA, string, 0D);
			
		case "+":
			return new Token(TokenType.PLUS, string, 0D);
			
		case "-":
			return new Token(TokenType.MINUS, string, 0D);
			
		case "*":
			return new Token(TokenType.MUL, string, 0D);
			
		case "/":
			return new Token(TokenType.DIV, string, 0D);
			
		case "**":
			return new Token(TokenType.POWER, string, 0D);
			
		default:
			if (Pattern.matches("\\d+\\.?\\d*", string)) {
				return new Token(TokenType.CONST_ID, string, Double.valueOf(string));
			} else if (Pattern.matches("\\w+", string)) {
				return new Token(TokenType.ID, string, 0D);
			} else {
				return null;
			}
		}
	}
	
	private int funcGenerator(String funcName) {
		switch (funcName) {
		case "sin":
			return SIN;
			
		case "cos":
			return COS;
			
		case "tan":
			return TAN;
			
		case "exp":
			return EXP;
			
		case "ln":
			return LN;
			
		case "sqrt":
			return SQRT;

		default:
			return -1;
		}
	}
	
	public void lexicalAnalyze() {
		try {
			Token token;
			String newFileName = fileName.substring(0, fileName.lastIndexOf('.'));
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(newFileName + "_lex.txt"));
			
			while ((token = this.getToken()).getType() != TokenType.EOF) {
				System.out.println(token.toJSONObject());
				String jsonString = JSON.toJSONString(token) + "\r\n";
				bufferedOutputStream.write(jsonString.getBytes());
				bufferedOutputStream.flush();
			}
			System.out.println(token.toJSONObject());
			String jsonString = JSON.toJSONString(token) + "\r\n";
			bufferedOutputStream.write(jsonString.getBytes());
			bufferedOutputStream.flush();
			bufferedOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Token> lexicalAnalyzeToList() throws IOException {
		Token token;
		ArrayList<Token> arrayList = new ArrayList<>();

		while ((token = this.getToken()).getType() != TokenType.EOF) {
			arrayList.add(token);
		}
		arrayList.add(token);
		return arrayList;
	}
	
	public static void main(String[] agrs) {
		try {
			Lexical lexical = new Lexical("test.txt");
			lexical.lexicalAnalyze();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
