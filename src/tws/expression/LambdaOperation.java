package tws.expression;

import java.util.HashMap;
import java.util.Map;

/**
 * Stellt eine Lambda Funktion da.
 * @author TheWhiteShadow
 */
public class LambdaOperation extends Node implements DynamicOperation
{
	private Operation op;
	private String[] names = new String[0];
	private LambdaResolver resolver;
	
	LambdaOperation(Expression exp, int sourcePos, Resolver parentResolver)
	{
		super(exp, sourcePos);
		this.resolver = new LambdaResolver(this, parentResolver);
	}

	public Resolver getResolver()
	{
		return resolver;
	}

	public String[] getParams()
	{
		return names;
	}

	public void setParams(String[] names)
	{
		this.names = names;
	}

	void setOperation(Operation op)
	{
		this.op = op;
	}

	@Override
	public Argument getArgument()
	{
		return new Reference(this, this);
	}

	@Override
	public String toString()
	{
		if (op == null) return "{}";
		
		return '{' + op.toString() + '}';
	}
	
	public Argument resolve()
	{
		return call(null);
	}

	@Override
	public Argument call(Argument[] args)
	{
		Map<String, Object> values = new HashMap<String, Object>();
		if (args != null)
		{
			for(int i = 0; i < names.length; i++)
			{
				values.put(names[i], args[i]);
			}
		}
		resolver.set(values);
		
		return op.resolve();
		
//		return new Operation()
//		{
//			private 
//			{
//
//			}
//			
//			@Override
//			public Argument resolve() throws EvaluationException
//			{
//				resolver.set(values);
//				
//				return op.resolve();
//			}
//		};
	}

	static class LambdaResolver implements Resolver
	{
		private final Resolver parent;
		private final ThreadLocal<Map<String, Object>> threadVariables = new ThreadLocal<Map<String, Object>>();
		private LambdaOperation lambda;
		
		LambdaResolver(LambdaOperation lambda, Resolver parent)
		{
			this.lambda = lambda;
			this.parent = parent;
		}

		public void set(Map<String, Object> values)
		{
			threadVariables.set(values);
		}

		@Override
		public Object resolve(String name, Argument[] args) throws EvaluationException
		{
			if (name == null)
			{
				return lambda.call(args);
			}
			
			Map<String, Object> vars = threadVariables.get();
			if (vars.containsKey(name))
			{
				return vars.get(name);
			}
			else
			{
				return parent.resolve(name, args);
			}
		}

		@Override
		public void assign(String name, Object arg) throws EvaluationException
		{
			this.threadVariables.get().put(name, arg);
		}
	}
}
