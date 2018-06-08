package parsing;

import com.alibaba.fastjson.annotation.JSONField;

public class LeftValue {
	@JSONField(name="value")
	private double value;

	public LeftValue(double value) {
		super();
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}


}
