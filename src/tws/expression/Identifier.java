package tws.expression;


public class Identifier extends Node implements DynamicOperation
{
	private String name;
	private Resolver resolver;
	
	Identifier(Expression exp, int sourcePos, String name, Resolver resolver)
	{
		super(exp, sourcePos);
		this.name = name;
		this.resolver = resolver;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public Argument getArgument()
	{
		return resolve();
	}
	
	@Override
	public Argument resolve() throws EvaluationException
	{
		return call(null);
	}
	
	@Override
	public Argument call(Argument[] args)
	{
		try
		{
			Object obj = resolver.resolve(name, args);

			return Config.wrap(this, obj, false);
		}
		catch(Exception e)
		{
			throw new EvaluationException(this, "Can not resolve " + name, e);
		}
	}

	@Override
	public String toString()
	{
		return '$' + name;
	}
}