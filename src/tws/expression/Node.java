package tws.expression;

import java.util.Arrays;

/**
 * Ein Knoten stellt ein Argument oder eine Operation in einer Expression da.
 * @author TheWhiteShadow
 * @see Argument
 * @see Operation
 */
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
	
	/**
	 * Gibt den Elternknoten zurück.
	 * @return den Elternknoten.
	 * @see #getChildren()
	 */
	public Node getParent()
	{
		return parent;
	}
	
	/**
	 * Gibt die Expression des Knoten zurück.
	 * @return die Expression.
	 */
	public Expression getExpression()
	{
		return exp;
	}
	
	/**
	 * Gibt die Position des Knoten innerhalb der Expression zurück.
	 * @return Position innerhalb der Expression.
	 */
	public int getSourcePos()
	{
		return sourcePos;
	}

	/**
	 * Gibt die Kindknoten zurück.
	 * @return die Kindknoten.
	 * @see #getParent()
	 */
	public Node[] getChildren()
	{
		return EMPTY;
	}
	
	/**
	 * Gibt das zum Knoten gehörende Argument zurück. Wenn der Knoten eine Operation enthält, wird diese Aufgelöst.
	 * @return Das Argument des Knoten.
	 */
	public abstract Argument getArgument();
	
	Object getObject()
	{
		return getArgument().asObject();
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + Arrays.toString(getChildren());
	}
}
