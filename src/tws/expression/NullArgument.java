package tws.expression;

import java.util.Collections;
import java.util.List;


public class NullArgument extends Node implements Argument
{
	NullArgument(Expression exp, int sourcePos)
	{
		super(exp, sourcePos);
	}
	
	NullArgument(Node parent)
	{
		super(parent);
	}

	public Class<?> getType() { return void.class; }
	public boolean isNumber() { return false; }
	public boolean isString() { return false; }
	public boolean isNull() { return true; }
	public boolean isBoolean() { return false; }
	public boolean isObject() { return false; }
	
	public boolean asBoolean()
	{
		return getExpression().getConfig().isTrue(this);
	}

	public String asString()
	{
		getExpression().getConfig().checkNullToString();
		return "";
	}

	public double asDouble()
	{
		return Double.NaN;
	}

	public long asLong()
	{
		throw new EvaluationException("Can not cast null to long.");
	}

	public Object asObject()
	{
		return null;
	}
	
	@Override
	public String toString()
	{
		return "null";
	}
	
	@Override
	public Argument getArgument()
	{
		return this;
	}
	
	public List<?> asList()
	{
		return Collections.EMPTY_LIST;
	}
}
