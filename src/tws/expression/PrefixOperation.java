package tws.expression;


public class PrefixOperation extends Node implements Operation
{
	private OperationNode symbol;
	private Node arg;
	
	PrefixOperation(OperationNode symbol, Node arg)
	{
		super(symbol);
		this.symbol = symbol;
		this.arg = arg;
	}

	public OperationNode getSymbol()
	{
		return symbol;
	}
	
	public Argument resolve() throws EvaluationException
	{
		return (Argument) Symbols.resolveOneArg(symbol, arg);
	}
	
	@Override
	public Node[] getChildren()
	{
		return new Node[] {arg};
	}

	@Override
	public String toString()
	{
		return symbol.getOperator().toString() + ' ' + arg.toString();
	}
	
	@Override
	public Argument getArgument()
	{
		return resolve();
	}
}
