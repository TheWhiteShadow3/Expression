package tws.expression;

import java.util.Arrays;
import java.util.List;


public class FloatArgument extends Node implements Argument
{
	private double value;

	FloatArgument(Expression exp, int sourcePos,  double value)
	{
		super(exp, sourcePos);
		this.value = value;
	}
	
	FloatArgument(Node parent, double value)
	{
		super(parent);
		this.value = value;
	}

	public Class<?> getType() { return double.class; }
	public boolean isNumber() { return true; }
	public boolean isString() { return false; }
	public boolean isNull() { return false; }
	public boolean isBoolean() { return false; }
	public boolean isObject() { return false; }

	public boolean asBoolean()
	{
		return getExpression().getConfig().isTrue(this);
	}

	public double asDouble()
	{
		return value;
	}

	public long asLong()
	{
		return (long) value;
	}

	public String asString()
	{
		return Double.toString(value);
	}
	
	public Double asObject()
	{
		return Double.valueOf(value);
	}

	@Override
	public String toString()
	{
		return asString();
	}
	
	@Override
	public Argument getArgument()
	{
		return this;
	}

	public List<Double> asList()
	{
		return Arrays.asList(asObject());
	}
}
