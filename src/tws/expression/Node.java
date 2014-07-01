package tws.expression;

import java.util.Arrays;


public abstract class Node
{
	protected static final Node[] EMPTY = new Node[0];
	
	private Expression exp;
	private Node parent;
	private int sourcePos;
	
	Node(Node parent)
	{
		if (parent == null) throw new NullPointerException("parent is null");
		
		this.parent = parent;
		this.exp = parent.getExpression();
		this.sourcePos = parent.getSourcePos();
	}
	
	Node(Expression exp, int sourcePos)
	{
		this.exp = exp;
		this.sourcePos = sourcePos;
	}
	
	public Node getParent()
	{
		return parent;
	}
	
	public Expression getExpression()
	{
		return exp;
	}
	
	public int getSourcePos()
	{
		return sourcePos;
	}

	public Node[] getChildren()
	{
		return EMPTY;
	}
	
	public abstract Argument getArgument();

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + Arrays.toString(getChildren());
	}
}
