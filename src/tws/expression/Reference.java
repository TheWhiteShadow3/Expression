package tws.expression;

import java.util.Arrays;


public class Reference extends Node implements Operation
{
	private String name;
	private Node[] argNodes;
	
	Reference(Expression exp, int sourcePos, String name)
	{
		super(exp, sourcePos);
		this.name = name;
	}
	
	Reference(Node initiator, String name)
	{
		super(initiator);
		this.name = name;
	}
	
	void setArguments(Node[] argNodes)
	{
		this.argNodes = argNodes;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Argument resolve() throws EvaluationException
	{
		Object obj;
		Resolver resolver = getExpression().getConfig().internalResolver;
		try
		{
			if (argNodes == null)
				obj = resolver.resolve(name, null);
			else
			{
				Argument[] args = new Argument[argNodes.length];
				for(int i = 0; i < args.length; i++)
					args[i] = argNodes[i].getArgument();
				obj = resolver.resolve(name, args);
			}
		}
		catch(Exception e)
		{
			throw new EvaluationException(this, "Can not resolve " + name, e);
		}
		
		return Config.wrap(this, obj);
	}
	
	@Override
	public String toString()
	{
		if (argNodes == null)
			return '$' + name;
		else
			return '$' + name + Arrays.toString(argNodes);
	}
	
	@Override
	public Argument getArgument()
	{
		return resolve();
	}
}
