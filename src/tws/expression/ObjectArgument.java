package tws.expression;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Stellt ein Objekt da.
 * @author TheWhiteShadow
 */
public class ObjectArgument extends Node implements Argument
{
	private Object obj;
	
	ObjectArgument(Expression exp, int sourcePos, Object obj)
	{
		super(exp, sourcePos);
		this.obj = obj;
	}
	
	ObjectArgument(INode parent, Object obj)
	{
		super(parent);
		assert obj != null;
		this.obj = obj;
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
	public boolean isBoolean() { return false; }
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
		return obj.toString();
	}

	@Override
	public double asDouble()
	{
		return Double.NaN;
	}

	@Override
	public long asLong()
	{
		throw new EvaluationException("Can not cast object to long.");
	}

	@Override
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
	
	@Override
	public List<?> asList()
	{
		if (obj == null)
		{
			return Collections.EMPTY_LIST;
		}
		else if (obj.getClass().isArray())
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
		else if (obj instanceof List)
		{
			return (List<Object>) obj;
		}
		return Arrays.asList(obj);
	}
}
