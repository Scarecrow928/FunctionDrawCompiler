package parsing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import lexicalAnalyze.*;

public class Parsing {
	private String filename;
	private Lexical lexical;
	private ArrayList<Token> arrayList;
	private int position;
	public static String STATE_TYPE = "statetype";
	public static String NODE_LIST = "nodelist";

	public Parsing(String sourceFile) throws IOException {
		filename = sourceFile;
		lexical = new Lexical(filename);
		position = -1;
	}

	public Node makeExpressionNode(TokenType tokenType, double arg, Node[] child, LeftValue value) {
		Node node;
		switch (tokenType) {
		case CONST_ID:
			node = new ConstNode(arg);
			break;

		case FUNC:
			node = new FunctionNode(child[0], (int) arg);
			break;

		case T:
			node = new ParameterNode(value);
			break;

		default:
			node = new OperatorNode(child[0], child[1]);
		}

		node.setTokenType(tokenType);
		return node;
	}

	public ArrayList<JSONObject> Parse() throws IOException {
		arrayList = lexical.lexicalAnalyzeToList();
		Token token;
		ArrayList<JSONObject> statementList = new ArrayList<>();
		
		try {
			while ((token = nextToken()).getType() != TokenType.EOF) {
				prevviousToken();
				switch (token.getType()) {
				case FOR:
					JSONObject forObject = new JSONObject();
					forObject.put(STATE_TYPE, TokenType.FOR);
					forObject.put(NODE_LIST, ForStatement());
					statementList.add(forObject);
					matchToken(TokenType.SEMICO);
					break;
					
				case ORIGIN:
					JSONObject originObject = new JSONObject();
					originObject.put(STATE_TYPE, TokenType.ORIGIN);
					originObject.put(NODE_LIST, OriginStatement());
					statementList.add(originObject);
					matchToken(TokenType.SEMICO);
					break;
				
				case ROT:
					JSONObject rotObject = new JSONObject();
					rotObject.put(STATE_TYPE, TokenType.ROT);
					rotObject.put(NODE_LIST, RotStatement());
					statementList.add(rotObject);
					matchToken(TokenType.SEMICO);
					break;
					
				case SCALE:
					JSONObject scaleObject = new JSONObject();
					scaleObject.put(STATE_TYPE, TokenType.SCALE);
					scaleObject.put(NODE_LIST, ScaleStatement());
					statementList.add(scaleObject);
					matchToken(TokenType.SEMICO);
					break;

				default:
					break;
				}
			}
		} catch (SyntaxError e) {
			System.out.println("Lost \';\'.");
		}
		
		return statementList;
	}
	
	public void parsingToFile() throws IOException {
		ArrayList<JSONObject> arrayList = Parse();
		String newFileName = filename.substring(0, filename.lastIndexOf('.'));
		FileOutputStream fileOutputStream = new FileOutputStream(newFileName + "_parsing.txt");
		
		for (JSONObject jsonObject: arrayList) {
			String string = JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat);
			System.out.println(string);
			fileOutputStream.write((string + "\r\n").getBytes());
			fileOutputStream.flush();
		}
		fileOutputStream.close();
	}
	
	public void printObject() throws IOException {
		ArrayList<JSONObject> arrayList = Parse();
		for (JSONObject jsonObject: arrayList) {
			System.out.println(JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat));
		}
	}
	
	public void matchToken(TokenType type) throws SyntaxError {
		if (nextToken().getType() == type) {
			return ;
		} else {
			SyntaxError error = new SyntaxError(type.toString());
			throw error;
		}
	}
	
	// FOR T FROM start TO end STEP step DRAW (x, y);
	public Node[] ForStatement() {
		try {
			Node start, end, step, x, y;
			
			matchToken(TokenType.FOR);
			matchToken(TokenType.T);
			matchToken(TokenType.FROM);
			start = Expression();
			matchToken(TokenType.TO);
			end = Expression();
			matchToken(TokenType.STEP);
			step = Expression();
			matchToken(TokenType.DRAW);
			matchToken(TokenType.L_BRACKET);
			x = Expression();
			matchToken(TokenType.COMMA);
			y = Expression();
			matchToken(TokenType.R_BRACKET);
			
			Node[] nodes = {start, end, step, x, y};
			return nodes;
		} catch (SyntaxError e) {
			Token token;
			System.out.print("SyntaxError location: ");
			while((token = nextToken()).getType() != TokenType.SEMICO && token.getType() != TokenType.EOF)
				System.out.print(token.getString() + " ");
			System.out.println();
			return null;
		}
	}
	
	// ORIGIN IS (x, y);
	public Node[] OriginStatement() {
		try {
			Node x, y;
			
			matchToken(TokenType.ORIGIN);
			matchToken(TokenType.IS);
			matchToken(TokenType.L_BRACKET);
			x = Expression();
			matchToken(TokenType.COMMA);
			y = Expression();
			matchToken(TokenType.R_BRACKET);
			
			Node[] nodes = {x, y};
			return nodes;
		} catch (SyntaxError e) {
			e.printStackTrace();
			Token token;
			System.out.print("SyntaxError location: ");
			while((token = nextToken()).getType() != TokenType.SEMICO && token.getType() != TokenType.EOF)
				System.out.print(token.getString() + " ");
			System.out.println();
			return null;
		}
	}
	
	//ROT IS r;
	public Node[] RotStatement() {
		try {
			Node r;
			
			matchToken(TokenType.ROT);
			matchToken(TokenType.IS);
			r = Expression();
			
			Node[] nodes = {r};
			return nodes;

		} catch (SyntaxError e) {
			e.printStackTrace();
			Token token;
			System.out.print("SyntaxError location: ");
			while((token = nextToken()).getType() != TokenType.SEMICO && token.getType() != TokenType.EOF)
				System.out.print(token.getString() + " ");
			System.out.println();
			return null;
		}
	}
	
	//SCALE IS (x, y);
	public Node[] ScaleStatement() {
		try {
			Node x, y;
			
			matchToken(TokenType.SCALE);
			matchToken(TokenType.IS);
			matchToken(TokenType.L_BRACKET);
			x = Expression();
			matchToken(TokenType.COMMA);
			y = Expression();
			matchToken(TokenType.R_BRACKET);
			
			Node[] nodes = {x, y};
			return nodes;

		} catch (SyntaxError e) {
			e.printStackTrace();
			Token token;
			System.out.print("SyntaxError location: ");
			while((token = nextToken()).getType() != TokenType.SEMICO && token.getType() != TokenType.EOF)
				System.out.print(token.getString() + " ");
			System.out.println();
			return null;
		}
	}

	public Node Expression() {
		Node left, right, parentNode;
		parentNode = left = Term();

		while (true) {
			Token plusOrMinus = nextToken();
			if (plusOrMinus.getType() == TokenType.PLUS || plusOrMinus.getType() == TokenType.MINUS) {
				right = Term();
				Node[] children = {left, right};
				parentNode = makeExpressionNode(plusOrMinus.getType(), 0, children, null);
				left.setParent(parentNode);
				right.setParent(parentNode);
				left = parentNode;
			} else {
				prevviousToken();
				return parentNode;
			}
		}
	}

	public Node Term() {
		Node left, right, parentNode;
		parentNode = left = Factor();

		while (true) {
			Token mulOrDiv = nextToken();
			if (mulOrDiv.getType() == TokenType.MUL || mulOrDiv.getType() == TokenType.DIV) {
				right = Term();
				Node[] children = {left, right};
				parentNode = makeExpressionNode(mulOrDiv.getType(), 0, children, null);
				left.setParent(parentNode);
				right.setParent(parentNode);
				left = parentNode;
			} else {
				prevviousToken();
				return parentNode;
			}
		}
	}

	public Node Factor() {
		Token next = nextToken();
		if (next.getType() == TokenType.PLUS || next.getType() == TokenType.MINUS) {
			Node child = Factor();
			Node const_0 = makeExpressionNode(TokenType.CONST_ID, 0D, null, null);
			Node[] children = {const_0, child};
			Node parentNode = makeExpressionNode(next.getType(), 0, children, null);
			const_0.setParent(parentNode);
			child.setParent(parentNode);
			return parentNode;
		} else {
			prevviousToken();
			return Component();
		}
	}

	public Node Component() {
		Token next;
		Node atom = Atom();
		next = nextToken();
		
		if (next.getType() == TokenType.POWER) {
			Node component = Component();
			Node[] children = {atom, component};
			Node parentNode = makeExpressionNode(TokenType.POWER, 0, children, null);
			atom.setParent(parentNode);
			component.setParent(parentNode);
			return parentNode;
		} else {
			prevviousToken();
			return atom;
		}
	}

	public Node Atom() {
		Token next = nextToken();
		if (next.getType() == TokenType.CONST_ID) {
			return makeExpressionNode(TokenType.CONST_ID, next.getValue(), null, null);
		} else if (next.getType() == TokenType.T) {
			return makeExpressionNode(TokenType.T, 0, null, new LeftValue(0D));
		} else if (next.getType() == TokenType.FUNC) {
			Token function = next;
			next = nextToken();
			if (next.getType() == TokenType.L_BRACKET) {
				Node expression = Expression();
				Node[] children = {expression};
				Node parentNode = makeExpressionNode(TokenType.FUNC, (double) function.getFunction(), children, null);
				expression.setParent(parentNode);
				
				next = nextToken();
				if (next.getType() == TokenType.R_BRACKET) {
					return parentNode;
				} else {
					return null;
				}
				
			} else {
				return null;
			}
		} else if (next.getType() == TokenType.L_BRACKET) {	
			Node expression = Expression();
			next = nextToken();
			if (next.getType() == TokenType.R_BRACKET) {
				return expression;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private Token nextToken() {
		if (arrayList != null && position < arrayList.size() && position >= -1) {
			return arrayList.get(++position);
		} else {
			return null;
		}
	}

	private Token prevviousToken() {
		if (arrayList != null && position >= 0) {
			return arrayList.get(position--);
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		try {
			Parsing parsing = new Parsing("test.txt");
			parsing.printObject();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
