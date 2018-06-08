package lexicalAnalyze;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;

public class Token {
	private TokenType Type;
	public TokenType getType() {
		return Type;
	}

	public void setType(TokenType type) {
		Type = type;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getFunction() {
		return function;
	}

	public void setFunction(int function) {
		this.function = function;
	}

	private String string;
	private double value;
	private int function;
	
	public Token(TokenType type, String string, double value, int function) {
		super();
		this.Type = type;
		this.string = string;
		this.value = value;
		this.function = function;
	}

	public Token(TokenType type, String string, double value) {
		super();
		Type = type;
		this.string = string;
		this.value = value;
		this.function = -1;
	}

	@Override
	public String toString() {
		return "Token [Type=" + Type + ", string=" + string + ", value=" + value + ", function=" + function + "]";
	}
	
	public JSONObject toJSONObject() {
		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("type", Type);
		hashMap.put("string", string);
		hashMap.put("value", value);
		hashMap.put("function", function);
		return new JSONObject(hashMap);
	}

}
