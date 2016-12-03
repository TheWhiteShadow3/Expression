package tws.expression;

import java.util.ArrayList;
import java.util.List;


/**
 * Stellt eine Liste da.
 * @author TheWhiteShadow
 */
public class ListArgument extends Node implements Argument
{
	private Argument[] arguments;
	
	ListArgument(Node parent, Argument[] arguments)
	{
		super(parent);
		this.arguments = arguments;
	}
	
//	ListArgument(Node parent, List<?> list)
//	{
//		super(parent);
//		this.arguments = new ArrayList<Argument>(list.size());
//		for(int i = 0; i < list.size(); i++)
//		{
//			arguments.add( Config.wrap(this, list.get(i), false) );
//		}
//	}
	
	@Override
	public Argument getArgument()
	{
		return this;
	}
	
	@Override
	public Class<?> getType() { return List.class; }
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
	public String asString()
	{
		return asList().toString();
	}

	@Override
	public boolean asBoolean()
	{
		return getExpression().getConfig().isTrue(this);
	}

	@Override
	public double asDouble()
	{
		return Double.NaN;
	}

	@Override
	public long asLong()
	{
		throw new EvaluationException("Can not cast array to long.");
	}

	@Override
	public List<?> asObject()
	{
		return asList();
	}

	@Override
	public List<Object> asList()
	{
		List<Object> list = new ArrayList<Object>(arguments.length);
		for(int i = 0, n = arguments.length; i < n; i++)
		{
			list.add(arguments[i].asObject());
		}
		return list;
	}
	
	public int size()
	{
		return arguments.length;
	}
	
	public Argument[] getValues()
	{
		return arguments;
	}
	
	@Override
	public String toString()
	{
		return asString();
	}
}
