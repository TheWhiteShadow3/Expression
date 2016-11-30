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

	@Override
	public Class<?> getType() { return void.class; }
	@Override
	public boolean isNumber() { return false; }
	@Override
	public boolean isString() { return false; }
	@Override
	public boolean isNull() { return true; }
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
	public String asString()
	{
		checkNullCast(Config.NullCast.TO_EMPTY_STRING, String.class);
		return "";
	}

	@Override
	public double asDouble()
	{
		return Double.NaN;
	}

	@Override
	public long asLong()
	{
		checkNullCast(Config.NullCast.TO_ZERO, long.class);
		return 0;
	}

	@Override
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
	
	@Override
	public List<?> asList()
	{
		checkNullCast(Config.NullCast.TO_EMPTY_LIST, List.class);
		return Collections.EMPTY_LIST;
	}
	
	private void checkNullCast(int cast, Class<?> type)
	{
		if ((getExpression().getConfig().nullCast & cast) == 0)
			throw new EvaluationException(this, "Can not cast null to " + type.getSimpleName() + "."); 
	}
}
