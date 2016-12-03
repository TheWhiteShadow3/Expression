package tws.expression;

import java.util.Arrays;
import java.util.List;

/**
 * Stellt einen boolschen Wert da.
 * @author TheWhiteShadow
 */
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

	@Override
	public Class<?> getType() { return boolean.class; }
	@Override
	public boolean isNumber() { return false; }
	@Override
	public boolean isString() { return false; }
	@Override
	public boolean isNull() { return false; }
	@Override
	public boolean isBoolean() { return true; }
	@Override
	public boolean isObject() { return false; }

	@Override
	public boolean asBoolean()
	{
		return value;
	}

	@Override
	public String asString()
	{
		return Boolean.toString(value);
	}

	@Override
	public double asDouble()
	{
		getExpression().getConfig().checkBooleanToNumber();
		return value ? 1d : 0d;
	}

	@Override
	public long asLong()
	{
		getExpression().getConfig().checkBooleanToNumber();
		return value ? 1 : 0;
	}
	
	@Override
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

	@Override
	public List<Boolean> asList()
	{
		return Arrays.asList(asObject());
	}
}
