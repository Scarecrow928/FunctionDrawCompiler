package parsing;

import com.alibaba.fastjson.annotation.JSONField;

public class ConstNode extends Node {
	@JSONField(name="constvalue")
	private double constValue;
	
	public ConstNode(double constValue) {
		super();
		this.constValue = constValue;
	}


	public double getConstValue() {
		return constValue;
	}

	public void setConstValue(double constValue) {
		this.constValue = constValue;
	}

}
