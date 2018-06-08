package parsing;

import com.alibaba.fastjson.annotation.JSONField;

public class ParameterNode extends Node {
	@JSONField(name="leftvalue")
	private LeftValue parameterValue;

	public ParameterNode(LeftValue parameterValue) {
		super();
		this.parameterValue = parameterValue;
	}

	public LeftValue getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(LeftValue parameterValue) {
		this.parameterValue = parameterValue;
	}


}
