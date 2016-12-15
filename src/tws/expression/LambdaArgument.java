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
	public boolean isNull() { return true; }
	@Override
	public boolean isBoolean() { return false; }
	@Override
	public boolean isObject() { return false; }

	@Override
	public String asString() { throw new UnsupportedOperationException(); }
	@Override
	public boolean asBoolean() { throw new UnsupportedOperationException(); }
	@Override
	public double asDouble() { throw new UnsupportedOperationException(); }
	@Override
	public long asLong() { throw new UnsupportedOperationException(); }
	@Override
	public Object asObject() { return this; }
	@Override
	public List<?> asList() { throw new UnsupportedOperationException(); }

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
