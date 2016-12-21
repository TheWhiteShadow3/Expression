package tws.expression;

import java.util.Arrays;
import java.util.List;

/**
 * Stellt eine Fließkommazahl da.
 * @author TheWhiteShadow
 */
public class FloatArgument extends Node implements Argument
{
	private double value;

	FloatArgument(Expression exp, int sourcePos,  double value)
	{
		super(exp, sourcePos);
		this.value = value;
	}
	
	FloatArgument(INode parent, double value)
	{
		super(parent);
		this.value = value;
	}

	@Override
	public Class<?> getType() { return double.class; }
	@Override
	public boolean isNumber() { return true; }
	@Override
	public boolean isString() { return false; }
	@Override
	public boolean isNull() { return false; }
	@Override
	public boolean isBoolean() { return false; }
	@Override
	public boolean isObject() { return false; }

	@Override
	public boolean asBoolean()
	{
		return getExpression().getConfig().isTrue(this);
	}

	@Override
	public double asDouble()
	{
		return value;
	}

	@Override
	public long asLong()
	{
		return (long) value;
	}

	@Override
	public String asString()
	{
		return Double.toString(value);
	}
	
	@Override
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

	@Override
	public List<Double> asList()
	{
		return Arrays.asList(asObject());
	}
}
