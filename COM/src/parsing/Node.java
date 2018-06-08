package parsing;

import com.alibaba.fastjson.annotation.JSONField;

import lexicalAnalyze.TokenType;

public class Node {
	
	@JSONField(name="type")
	private TokenType tokenType;
	
	@JSONField(serialize=false)
	private Node parent;
	
	public TokenType getTokenType() {
		return tokenType;
	}
	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
}
