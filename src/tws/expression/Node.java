package tws.expression;

import java.util.Arrays;

/**
 * Ein Knoten stellt ein Argument oder eine Operation in einer Expression da.
 * @author TheWhiteShadow
 * @see Argument
 * @see Operation
 * @see DynamicOperation
 */
abstract class Node implements INode
{
	private Expression exp;
	private INode parent;
	private int sourcePos;
	
	Node(INode parent)
	{
		assert parent != null;
		
		this.parent = parent;
		this.exp = parent.getExpression();
		this.sourcePos = parent.getSourcePos();
	}
	
	Node(Expression exp, int sourcePos)
	{
		assert exp != null;

		this.exp = exp;
		this.sourcePos = sourcePos;
	}

	@Override
	public INode getParent()
	{
		return parent;
	}
	
	void setParent(INode parent)
	{
		this.parent = parent;
	}

	@Override
	public Expression getExpression()
	{
		return exp;
	}

	@Override
	public int getSourcePos()
	{
		return sourcePos;
	}

	@Override
	public INode[] getChildren()
	{
		return EMPTY;
	}

	@Override
	public abstract Argument getArgument();

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + Arrays.toString(getChildren());
	}
}
