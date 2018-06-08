package parsing;

import com.alibaba.fastjson.annotation.JSONField;


public class OperatorNode extends Node {
	
	@JSONField(name="leftchild")
	private Node leftChild;
	
	@JSONField(name="rightchild")
	private Node rightChild;
	
	public OperatorNode(Node leftChild, Node rightChild) {
		super();
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	public Node getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
	}

	public Node getRightChild() {
		return rightChild;
	}

	public void setRightChild(Node rightChild) {
		this.rightChild = rightChild;
	}

}
