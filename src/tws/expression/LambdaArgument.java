package tws.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stellt eine Lambda Funktion da.
 * Das Argument kann nicht direkt aufgelöst werden, da es nur eine Funktion referenziert.
 * Alle as... Methoden werfen eine <code>UnsupportedOperationException.</code>
 * @author TheWhiteShadow
 */
public class LambdaArgument extends Node implements Argument, Invokable
{
	private Operation op;
	private String[] names = new String[0];
	private LambdaResolver resolver;
	
	LambdaArgument(Expression exp, int sourcePos, Resolver parentResolver)
	{
		super(exp, sourcePos);
		this.resolver = new LambdaResolver(parentResolver);
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
	public Class<?> getType() { return Invokable.class; }
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
	public String asString() { return resolve().asString(); }
	@Override
	public boolean asBoolean() { return resolve().asBoolean(); }
	@Override
	public double asDouble() { return resolve().asDouble(); }
	@Override
	public long asLong() { return resolve().asLong(); }
	@Override
	public Object asObject() { return resolve().asObject(); }
	@Override
	public List<?> asList() { return resolve().asList(); }

	@Override
	public Argument getArgument()
	{
		return this;
	}

	@Override
	public String toString()
	{
		if (op == null) return "{}";
		
		return '{' + op.toString() + '}';
	}
	
	@Override
	public Argument resolve()
	{
		return with((Object[]) null).resolve();
	}

	@Override
	public Operation with(final Object... args)
	{
		return new Operation()
		{
			private Map<String, Object> values = new HashMap<String, Object>();
			{
				for(int i = 0; i < names.length; i++)
				{
					values.put(names[i], args[i]);
				}
			}
			
			@Override
			public Argument resolve() throws EvaluationException
			{
				resolver.set(values);
				
				return op.resolve();
			}
		};
	}

	static class LambdaResolver implements Resolver
	{
		final Resolver parent;
		private final ThreadLocal<Map<String, Object>> threadVariables = new ThreadLocal<Map<String, Object>>();
		
		LambdaResolver(Resolver parent)
		{
			this.parent = parent;
		}

		public void set(Map<String, Object> values)
		{
			threadVariables.set(values);
		}

		@Override
		public Object resolve(String name, Argument[] args) throws EvaluationException
		{
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
	
//	static class LambdaReference extends Reference
//	{
//		Object value;
//		private int index;
//
//		LambdaReference(Node node, String name, int index)
//		{
//			super(node, name);
//			this.index = index;
//		}
//
//		@Override
//		public Argument resolve(boolean recursive) throws EvaluationException
//		{
//			return Config.wrap(this, value, recursive);
//		}
//	}
}
