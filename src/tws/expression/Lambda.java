package tws.expression;

import java.util.List;

@Deprecated
public class Lambda extends Node implements Operation
{
	private Operation op;
	private String[] names = new String[0];
	private List<LambdaReference> refs;
	
	Lambda(Expression exp, int sourcePos)
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
	public Argument getArgument()
	{
		return new ObjectArgument(this, this);
	}

	@Override
	public Argument resolve() throws EvaluationException
	{
		return op.resolve();
	}
	
	@Override
	public String toString()
	{
		if (op == null) return "{}";
		
		return '{' + op.toString() + '}';
	}

	public Operation with(final Object[] args)
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
