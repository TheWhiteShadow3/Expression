package tws.expression;


/**
 * Stellt eine Operation mit einem Operanten da.
 * @author TheWhiteShadow
 */
public class PrefixOperation extends Node implements Operation
{
	private OperationNode symbol;
	private INode arg;
	
	PrefixOperation(OperationNode symbol, INode arg)
	{
		super(symbol);
		this.symbol = symbol;
		this.arg = arg;
	}

	public OperationNode getSymbol()
	{
		return symbol;
	}
	
	@Override
	public Argument resolve() throws EvaluationException
	{
		return Symbols.resolveOneArg(symbol, arg);
	}
	
	@Override
	public INode[] getChildren()
	{
		return new INode[] {arg};
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
