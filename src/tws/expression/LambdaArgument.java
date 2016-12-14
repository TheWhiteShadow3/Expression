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
public class LambdaArgument extends Node implements Argument
{
	private Operation op;
	private String[] names = new String[0];
	private Map<String, Object> values = new HashMap<String, Object>();
	
	LambdaArgument(Expression exp, int sourcePos)
	{
		super(exp, sourcePos);
	}

	void setNames(String[] names)
	{
		this.names = names;
	}

	String[] getNames()
	{
		return names;
	}

	void setOperation(Operation op)
	{
		this.op = op;
	}
	
	@Override
	public Class<?> getType() { return Operation.class; }
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
	public Object asObject() { throw new UnsupportedOperationException(); }
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

	public int getArgumentCount()
	{
		return names.length;
	}

	public Argument resolveWith(Object[] args)
	{
		try
		{
			for(int i = 0; i < names.length; i++)
			{
				values.put(names[i], args[i]);
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			throw new EvaluationException(this, "Too many Parameters defined.", e);
		}
		return op.resolve();
	}
	
	private Object get(String name)
	{
		return values.get(name);
	}

	static class LambdaReference extends Reference
	{
		private LambdaArgument lambda;

		LambdaReference(LambdaArgument lambda, int sourcePos, String name)
		{
			super(lambda.getExpression(), sourcePos, name);
			this.lambda = lambda;
		}

		@Override
		public Argument resolve(boolean recursive) throws EvaluationException
		{
			return Config.wrap(this, lambda.get(getName()), recursive);
		}
	}
}
