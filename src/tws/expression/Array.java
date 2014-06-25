package tws.expression;

import java.util.ArrayList;
import java.util.List;

public class Array extends Node implements Argument
{
	private Node[] nodes;
	
	Array(Expression exp, int sourcePos, Node[] nodes)
	{
		super(exp, sourcePos);
		this.nodes = nodes;
	}
	
	Array(Node parent, Node[] nodes)
	{
		super(parent);
		this.nodes = nodes;
	}
	
	@Override
	public Node[] getChildren()
	{
		return nodes;
	}
	
	@Override
	public Argument getArgument()
	{
		return this;
	}
	
	public Class<?> getType() { return List.class; }
	public boolean isNumber() { return false; }
	public boolean isString() { return false; }
	public boolean isNull() { return false; }
	public boolean isBoolean() { return true; }
	public boolean isObject() { return false; }

	public String asString()
	{
		getExpression().getConfig().checkNullToString();
		return asList().toString();
	}

	public boolean asBoolean()
	{
		return getExpression().getConfig().isTrue(this);
	}

	public double asDouble()
	{
		return Double.NaN;
	}

	public long asLong()
	{
		throw new EvaluationException("Can not cast array to long.");
	}

	public List<?> asObject()
	{
		return asList();
	}

	public List<?> asList()
	{
		List<Object> list = new ArrayList<Object>(nodes.length);
		for(Node n : nodes)
		{
			list.add(n.getArgument().asObject());
		}
		return list;
	}
}
