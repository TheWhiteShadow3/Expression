package tws.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Stellt eine Liste da.
 * @author TheWhiteShadow
 */
public class ListArgument extends Node implements Argument
{
	private List<INode> argNodes;
	boolean resolved;
	
	ListArgument(INode parent, Argument[] arguments)
	{
		super(parent);
		this.argNodes = (List) Arrays.asList(arguments);
		this.resolved = true;
	}
	
	ListArgument(Expression exp, int sourcePos, INode[] argNodes)
	{
		super(exp, sourcePos);
		this.argNodes = Arrays.asList(argNodes);
	}
	
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
		List<Argument> args = getValues();
		
		List<Object> list = new ArrayList<Object>(size());
		for(int i = 0, n = size(); i < n; i++)
		{
			list.add(args.get(i).asObject());
		}
		return list;
	}
	
	public int size()
	{
		return argNodes.size();
	}
	
	public List<Argument> getValues()
	{
		if (!resolved)
		{
			for(int i = 0; i < size(); i++)
			{
				argNodes.set(i, argNodes.get(i).getArgument());
			}
			resolved = true;
		}
		return (List) argNodes;
	}
	
	@Override
	public String toString()
	{
		return asString();
	}
}
