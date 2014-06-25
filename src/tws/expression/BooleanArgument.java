package tws.expression;

import java.util.Arrays;
import java.util.List;


public class BooleanArgument extends Node implements Argument
{
	private boolean value;
	
	BooleanArgument(Expression exp, int sourcePos, boolean value)
	{
		super(exp, sourcePos);
		this.value = value;
	}
	
	BooleanArgument(Node parent, boolean value)
	{
		super(parent);
		this.value = value;
	}

	public Class<?> getType() { return boolean.class; }
	public boolean isNumber() { return false; }
	public boolean isString() { return false; }
	public boolean isNull() { return false; }
	public boolean isBoolean() { return true; }
	public boolean isObject() { return false; }

	public boolean asBoolean()
	{
		return value;
	}

	public String asString()
	{
		return Boolean.toString(value);
	}

	public double asDouble()
	{
		getExpression().getConfig().checkBooleanToNumber();
		return value ? 1d : 0d;
	}

	public long asLong()
	{
		getExpression().getConfig().checkBooleanToNumber();
		return value ? 1 : 0;
	}
	
	public Boolean asObject()
	{
		return Boolean.valueOf(value);
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

	public List<Boolean> asList()
	{
		return Arrays.asList(asObject());
	}
}
