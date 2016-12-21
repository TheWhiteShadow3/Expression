package tws.expression;

import java.util.Arrays;

/**
 * Ein Knoten stellt ein Argument oder eine Operation in einer Expression da.
 * @author TheWhiteShadow
 * @see Argument
 * @see Operation
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
	
	/**
	 * Gibt den Elternknoten zurück.
	 * @return den Elternknoten.
	 * @see #getChildren()
	 */
	@Override
	public INode getParent()
	{
		return parent;
	}
	
	/**
	 * Gibt die Expression des Knoten zurück.
	 * @return die Expression.
	 */
	@Override
	public Expression getExpression()
	{
		return exp;
	}
	
	/**
	 * Gibt die Position des Knoten innerhalb der Expression zurück.
	 * @return Position innerhalb der Expression.
	 */
	@Override
	public int getSourcePos()
	{
		return sourcePos;
	}

	/**
	 * Gibt die Kindknoten zurück.
	 * @return die Kindknoten.
	 * @see #getParent()
	 */
	@Override
	public INode[] getChildren()
	{
		return EMPTY;
	}
	
	/**
	 * Gibt das zum Knoten gehörende Argument zurück. Wenn der Knoten eine Operation enthält, wird diese Aufgelöst.
	 * @return Das Argument des Knoten.
	 */
	@Override
	public abstract Argument getArgument();

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + Arrays.toString(getChildren());
	}
}
