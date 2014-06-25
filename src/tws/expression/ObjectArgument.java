package tws.expression;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ObjectArgument extends Node implements Argument
{
	private Object obj;
	
	ObjectArgument(Expression exp, int sourcePos, Object obj)
	{
		super(exp, sourcePos);
		this.obj = obj;
	}
	
	ObjectArgument(Node parent, Object obj)
	{
		super(parent);
		this.obj = obj;
	}

	public Class<?> getType() { return (obj == null) ? void.class : obj.getClass(); }
	public boolean isNumber() { return false; }
	public boolean isString() { return false; }
	public boolean isNull() { return false; }
	public boolean isBoolean() { return false; }
	public boolean isObject() { return true; }
	
	public boolean asBoolean()
	{
		return getExpression().getConfig().isTrue(this);
	}

	public String asString()
	{
		if (obj == null)
		{
			getExpression().getConfig().checkNullToString();
			return "";
		}
		return obj.toString();
	}

	public double asDouble()
	{
		return Double.NaN;
	}

	public long asLong()
	{
		throw new EvaluationException("Can not cast object to long.");
	}

	public Object asObject()
	{
		return obj;
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
	
	public List<?> asList()
	{
		if (obj == null)
		{
			return Collections.EMPTY_LIST;
		}
		if (obj.getClass().isArray())
		{
			if (obj.getClass().getComponentType().isPrimitive())
			{	// Wrapper für primitive Typen, da diese nicht in ein Objekt-Array konvertiert werden können.
				Object[] wrapper = new Object[Array.getLength(obj)];
				for(int i = 0; i < wrapper.length; i++)
					wrapper[i] = Array.get(obj, i);
				return Arrays.asList(wrapper);
			}
			return Arrays.asList((Object[]) obj);
		}
		if (obj instanceof List)
		{
			return (List<Object>) obj;
		}
		return Arrays.asList(obj);
	}
}
