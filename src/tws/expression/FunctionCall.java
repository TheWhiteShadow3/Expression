package tws.expression;

import java.util.Arrays;

/**
 * Stellt eine Referenz auf ein Objekt da.
 * @author TheWhiteShadow
 */
public class FunctionCall extends Node implements Operation
{
	private final DynamicOperation target;
	private INode[] argNodes;
	
	FunctionCall(Expression exp, int sourcePos, DynamicOperation target)
	{
		super(exp, sourcePos);
		assert target != null;
		this.target = target;
	}
	
	FunctionCall(INode initiator, DynamicOperation target)
	{
		super(initiator);
		assert target != null;
		this.target = target;
	}

	void setArguments(INode[] argNodes)
	{
		this.argNodes = argNodes;
	}
	
	@Override
	public INode[] getChildren()
	{
		return argNodes;
	}
	
//	public String getName()
//	{
//		return name;
//	}
	
	public DynamicOperation getTarget()
	{
		return target;
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
		return target.call(resolveArguments());
		
//		try
//		{
//			Object obj = resolver.resolve(name, resolveArguments());
//
//			return Config.wrap(this, obj, recursive);
//		}
//		catch(Exception e)
//		{
//			throw new EvaluationException(this, "Can not resolve " + name, e);
//		}
	}
	
	@Override
	public String toString()
	{
		if (argNodes == null)
			return target.toString();
		else
			return target.toString() + Arrays.toString(argNodes);
	}
	
	@Override
	public Argument getArgument()
	{
		return resolve(false);
	}
}
