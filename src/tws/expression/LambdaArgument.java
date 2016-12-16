package tws.expression;

import java.util.List;

/**
 * Stellt eine Lambda Funktion da.
 * Das Argument kann nicht direkt aufgelöst werden, da es nur eine Funktion referenziert.
 * Alle as... Methoden werfen eine <code>UnsupportedOperationException.</code>
 * @author TheWhiteShadow
 */
public class LambdaArgument extends Node implements Argument
{
	private Operation op;
	private String[] names = new String[0];
	private List<LambdaReference> refs;
	
	LambdaArgument(Expression exp, int sourcePos)
	{
		super(exp, sourcePos);
	}

	public String[] getParams()
	{
		return names;
	}

	public void setParams(String[] names)
	{
		this.names = names;
	}

	void setOperation(Operation op, List<LambdaReference> refs)
	{
		this.op = op;
		this.refs = refs;
	}
	
	@Override
	public Class<?> getType() { return LambdaArgument.class; }
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
	Object getObject()
	{
		return this;
	}

	@Override
	public String toString()
	{
		if (op == null) return "{}";
		
		return '{' + op.toString() + '}';
	}
	
	public Argument resolve()
	{
		return with((Object[]) null).resolve();
	}

	public Operation with(final Object... args)
	{
		return new Operation()
		{
			private Object[] arguments = args;
			
			@Override
			public Argument resolve() throws EvaluationException
			{
				for(LambdaReference r : refs)
				{
					r.value = arguments[r.index];
				}
				
				return op.resolve();
			}
		};
	}

	static class LambdaReference extends Reference
	{
		Object value;
		private int index;

		LambdaReference(Node node, String name, int index)
		{
			super(node, name);
			this.index = index;
		}

		@Override
		public Argument resolve(boolean recursive) throws EvaluationException
		{
			return Config.wrap(this, value, recursive);
		}
	}
}
