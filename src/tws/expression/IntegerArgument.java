package tws.expression;

import java.util.Arrays;
import java.util.List;


public class IntegerArgument extends Node implements Argument
{
	private long value;

	IntegerArgument(Expression exp, int sourcePos,  long value)
	{
		super(exp, sourcePos);
		this.value = value;
	}
	
	IntegerArgument(Node parent, long value)
	{
		super(parent);
		this.value = value;
	}

	public Class<?> getType() { return long.class; }
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
		return value;
	}

	public String asString()
	{
		return Long.toString(value);
	}

	public Long asObject()
	{
		return Long.valueOf(value);
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
	
	public List<Long> asList()
	{
		return Arrays.asList(asObject());
	}
}
