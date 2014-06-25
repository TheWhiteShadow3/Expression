package tws.expression;


public class InfixOperation extends Node implements Operation
{
	private OperationNode symbol;
	private Node left;
	private Node right;
	
	InfixOperation(Node left, OperationNode symbol, Node right)
	{
		super(symbol);
		this.symbol = symbol;
		this.left = left;
		this.right = right;
	}
	
	public Argument resolve() throws EvaluationException
	{
		return (Argument) Symbols.resolveTwoArgs(symbol, left, right);
	}

	@Override
	public Node[] getChildren()
	{
		return new Node[] {left, right};
	}
	
	@Override
	public String toString()
	{
		return left.toString() + ' ' + symbol.getOperator().toString() + ' ' + right.toString();
	}
	
	@Override
	public Argument getArgument()
	{
		return resolve();
	}
}