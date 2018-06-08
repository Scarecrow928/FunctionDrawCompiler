package parsing;

import com.alibaba.fastjson.annotation.JSONField;


public class FunctionNode extends Node {
	
	@JSONField(name="child")
	private Node child;
	
	@JSONField(name="function")
	private int function;
	
	public FunctionNode(Node child, int function) {
		super();
		this.child = child;
		this.function = function;
	}

	public Node getChild() {
		return child;
	}

	public void setChild(Node child) {
		this.child = child;
	}

	public int getFunction() {
		return function;
	}

	public void setFunction(int function) {
		this.function = function;
	}


	
}
