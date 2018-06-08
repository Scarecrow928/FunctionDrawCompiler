package semantics;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

import javax.print.attribute.standard.NumberOfDocuments;
import javax.print.attribute.standard.RequestingUserName;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import javax.xml.crypto.dsig.Transform;
import javax.xml.stream.events.StartDocument;

import org.omg.CORBA.PRIVATE_MEMBER;

import com.alibaba.fastjson.JSONObject;

import lexicalAnalyze.Lexical;
import lexicalAnalyze.TokenType;
import parsing.*;
import lexicalAnalyze.*;

public class Semantics {
	private ArrayList<JSONObject> arrayList;
	private String filename;
	private Dimension origin;
	private double angle;
	private Dimension scale;
	
	
	public Semantics(String fileName) {
		this.filename = fileName;
		this.origin = new Dimension(0, 0);
		this.angle = 0;
		this.scale = new Dimension(1, 1);
	}
	
	public ArrayList<Dimension> transform() throws IOException {
		Parsing parsing = new Parsing(filename);
		this.arrayList = parsing.Parse();
		ArrayList<Dimension> pixels = new ArrayList<>();
		
		ArrayList<Dimension> line = new ArrayList<>();
		for (JSONObject obj: arrayList) {
			Node[] nodes = (Node[]) obj.get(Parsing.NODE_LIST);
			switch ((TokenType) obj.get(Parsing.STATE_TYPE)) {
			case FOR:
				pixels.addAll(forTransform(nodes));
				break;
				
			case ORIGIN:
				originTransform(nodes);
				break;
				
			case ROT:
				rotTransform(nodes);
				break;
				
			case SCALE:
				scaleTransform(nodes);
				break;

			default:
				break;
			}
		}
		
		return pixels;
	}
	
	public double caculateNode(Node node) {
		if (node instanceof ConstNode) {
			return ((ConstNode) node).getConstValue();
		} else if (node instanceof FunctionNode) {
			FunctionNode functionNode = (FunctionNode) node;
			switch (functionNode.getFunction()) {
			case Lexical.SIN:
				return Math.sin(caculateNode(functionNode.getChild()));
			
			case Lexical.COS:
				return Math.cos(caculateNode(functionNode.getChild()));

			case Lexical.TAN:
				return Math.tan(caculateNode(functionNode.getChild()));
				
			case Lexical.EXP:
				return Math.exp(caculateNode(functionNode.getChild()));

			case Lexical.LN:
				return Math.log(caculateNode(functionNode.getChild()));

			case Lexical.SQRT:
				return Math.sqrt(caculateNode(functionNode.getChild()));

			default:
				return 0;
			}
		} else if (node instanceof OperatorNode) {
			OperatorNode operatorNode = (OperatorNode) node;
			if (operatorNode.getTokenType() == TokenType.PLUS) {
				return caculateNode(operatorNode.getLeftChild()) + caculateNode(operatorNode.getRightChild());
			} else if (operatorNode.getTokenType() == TokenType.MINUS) {
				return caculateNode(operatorNode.getLeftChild()) - caculateNode(operatorNode.getRightChild());
			} else if (operatorNode.getTokenType() == TokenType.MUL) {
				return caculateNode(operatorNode.getLeftChild()) * caculateNode(operatorNode.getRightChild());
			} else if (operatorNode.getTokenType() == TokenType.DIV) {
				return caculateNode(operatorNode.getLeftChild()) / caculateNode(operatorNode.getRightChild());
			} else if (operatorNode.getTokenType() == TokenType.POWER) {
				return Math.pow(caculateNode(operatorNode.getLeftChild()), caculateNode(operatorNode.getRightChild()));
			} else {
				return 0;
			}
		} else if (node instanceof ParameterNode) {
			ParameterNode parameterNode = (ParameterNode) node;
			return parameterNode.getParameterValue().getValue();
		} else {
			return 0;
		}

	}
	
	public ArrayList<Dimension> forTransform(Node[] nodes){
		ArrayList<Dimension> pixels = new ArrayList<>();
		double start = caculateNode(nodes[0]);
		double end = caculateNode(nodes[1]);
		double step = caculateNode(nodes[2]);
		
		if (step <= 0) {
			return pixels;
		}
		
		for (double d = start; d < end; d += step) {
			setLeftValue(nodes[3], d);
			setLeftValue(nodes[4], d);
			double x = caculateNode(nodes[3]);
			double y = caculateNode(nodes[4]);
			
			// Scale transform
			x *= this.scale.getWidth();
			y *= this.scale.getHeight();
			
			// Rot transform
			double radius = Math.sqrt(x * x + y * y);
			double currentAngel = Math.atan2(y, x);
			x = radius * Math.cos(currentAngel + angle);
			y = radius * Math.sin(currentAngel + angle);
			
			// Origin transform
			x += this.origin.getWidth();
			y += this.origin.getHeight();
			
			pixels.add(new Dimension((int) x, (int) y));
		}
		
		return pixels;
	}
	
	public void setLeftValue(Node node, double value) {
		if (node instanceof OperatorNode) {
			OperatorNode operatorNode = (OperatorNode) node;
			setLeftValue(operatorNode.getLeftChild(), value);
			setLeftValue(operatorNode.getRightChild(), value);
		} else if (node instanceof FunctionNode) {
			FunctionNode functionNode = (FunctionNode) node;
			setLeftValue(functionNode.getChild(), value);
		} else if (node instanceof ParameterNode) {
			ParameterNode parameterNode = (ParameterNode) node;
			parameterNode.getParameterValue().setValue(value);
		}
	}
	
	public void originTransform(Node[] nodes) {
		double x = caculateNode(nodes[0]);
		double y = caculateNode(nodes[1]);
		this.origin.setSize(x, y);
	}
	
	public void rotTransform(Node[] nodes) {
		this.angle = caculateNode(nodes[0]);
	}
	
	public void scaleTransform(Node[] nodes) {
		double x = caculateNode(nodes[0]);
		double y = caculateNode(nodes[1]);
		this.scale.setSize(x, y);
	}

	
	public static void main(String[] args) {
		Semantics semantics = new Semantics("for.txt");
		try {
			ArrayList<Dimension> ar = semantics.transform();
			for (Dimension dimension : ar) {
				System.out.println(dimension);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
