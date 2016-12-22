package tws.expression;

import java.util.Arrays;
import java.util.List;

public class Reference extends Node implements Argument
{
	private DynamicOperation obj;

	Reference(Expression exp, int sourcePos, DynamicOperation obj)
	{
		super(exp, sourcePos);
		assert obj != null;
		this.obj = obj;
	}
	
	Reference(INode parent, DynamicOperation obj)
	{
		super(parent);
		assert obj != null;
		this.obj = obj;
	}
	
	@Override
	public Argument getArgument()
	{
		return this;
	}
	
	public DynamicOperation getOperation()
	{
		return obj;
	}

	@Override
	public Class<?> getType() { return obj.getClass(); }
	@Override
	public boolean isNumber() { return false; }
	@Override
	public boolean isString() { return false; }
	@Override
	public boolean isNull() { return false; }
	@Override
	public boolean isBoolean() { return true; }
	@Override
	public boolean isObject() { return true; }
	
	@Override
	public boolean asBoolean()
	{
		return getExpression().getConfig().isTrue(this);
	}

	@Override
	public String asString()
	{
		throw new EvaluationException("Can not cast reference to String.");
	}

	@Override
	public double asDouble()
	{
		return Double.NaN;
	}

	@Override
	public long asLong()
	{
		throw new EvaluationException("Can not cast reference to long.");
	}

	@Override
	public Object asObject()
	{
		return obj;
	}

	@Override
	public List<?> asList()
	{
		return Arrays.asList(obj);
	}

	@Override
	public String toString()
	{
		return '$' + obj.toString();
	}
}
