package tws.expression;

import java.util.Arrays;

/**
 * Stellt eine Referenz auf ein Objekt da.
 * @author TheWhiteShadow
 */
public class Reference extends Node implements Operation
{
	private final String name;
	private INode[] argNodes;
	private Resolver resolver;
	
	Reference(Expression exp, int sourcePos, String name, Resolver resolver)
	{
		super(exp, sourcePos);
		assert name != null;
		assert resolver != null;
		this.name = name;
		this.resolver = resolver;
	}
	
//	Reference(Node initiator, String name, Resolver resolver)
//	{
//		super(initiator);
//		this.name = name;
//		this.resolver = resolver;
//	}

	void setArguments(INode[] argNodes)
	{
		this.argNodes = argNodes;
	}
	
	@Override
	public INode[] getChildren()
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
		return resolve(true); 
	}
	
	protected Argument resolve(boolean recursive)
	{
		try
		{
			Object obj = resolver.resolve(name, resolveArguments());

			return Config.wrap(this, obj, recursive);
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
		return resolve(false);
	}
}
