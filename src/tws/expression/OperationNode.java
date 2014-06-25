package tws.expression;


class OperationNode extends Node
{
	private Operator op;

	OperationNode(Expression exp, int sourcePos, Operator op)
	{
		super(exp, sourcePos);
		this.op = op;
	}

	public Operator getOperator()
	{
		return op;
	}
	
	public int getPriority()
	{
		return op.priority;
	}
	
	public boolean isPrefixOperation()
	{
		return op == Operator.NOT || op ==  Operator.NEG;
	}
	
	public boolean isInfixOperation()
	{
		return !isPrefixOperation();
	}

	@Override
	public String toString()
	{
		return op.name();
	}
	
	@Override
	public Argument getArgument()
	{
		return null;
	}
}