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
	
	@Override
	public Node[] getChildren()
	{
		return argNodes;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Argument[] resolveArguments()
	{
		if (argNodes == null) return null;
		
		Argument[] args = new Argument[argNodes.length];
		for(int i = 0; i < args.length; i++)
			args[i] = argNodes[i].getArgument();
		return args;
	}
	
	@Override
	public Argument resolve() throws EvaluationException
	{
		Resolver resolver = getExpression().getConfig().internalResolver;
		try
		{
			Object obj = resolver.resolve(name, resolveArguments());
			return Config.wrap(this, obj);
		}
		catch(Exception e)
		{
			throw new EvaluationException(this, "Can not resolve " + name, e);
		}
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
