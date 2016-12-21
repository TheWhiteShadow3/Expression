package tws.expression;


/**
 * Stellt eine Operation mit zwei Operanten da.
 * @author TheWhiteShadow
 */
public class InfixOperation extends Node implements Operation
{
	private OperationNode symbol;
	private INode left;
	private INode right;
	
	InfixOperation(INode left, OperationNode symbol, INode right)
	{
		super(symbol);
		this.symbol = symbol;
		this.left = left;
		this.right = right;
	}

	public OperationNode getSymbol()
	{
		return symbol;
	}

	@Override
	public Argument resolve() throws EvaluationException
	{
		return Symbols.resolveTwoArgs(symbol, left, right);
	}

	@Override
	public INode[] getChildren()
	{
		return new INode[] {left, right};
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
