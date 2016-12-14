package tws.expression;

import java.util.Arrays;

/**
 * Stellt eine Referenz auf ein Objekt da.
 * @author TheWhiteShadow
 */
public class Reference extends Node implements Operation
{
	private final String name;
	private Node[] argNodes;
	
	Reference(Expression exp, int sourcePos, String name)
	{
		super(exp, sourcePos);
		this.name = name;
	}
	
	Reference(Expression exp, int sourcePos, Node[] argNodes)
	{
		super(exp, sourcePos);
		this.name = null;
		this.argNodes = argNodes;
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
		return resolve(true); 
	}
	
	protected Argument resolve(boolean recursive)
	{
		if (name == null)
		{
			return new ListArgument(this, resolveArguments());
		}
		else
		{
			Resolver resolver = getExpression().getConfig().internalResolver;
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
	}
	
	@Override
	public String toString()
	{
		if (name == null) return Arrays.toString(argNodes);
		
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
