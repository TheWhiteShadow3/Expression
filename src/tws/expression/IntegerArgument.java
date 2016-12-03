package tws.expression;

import java.util.Arrays;
import java.util.List;


/**
 * Stellt eine Integer Zahl da.
 * @author TheWhiteShadow
 */
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

	@Override
	public Class<?> getType() { return long.class; }
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
		return value;
	}

	@Override
	public String asString()
	{
		return Long.toString(value);
	}

	@Override
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
	
	@Override
	public List<Long> asList()
	{
		return Arrays.asList(asObject());
	}
}
