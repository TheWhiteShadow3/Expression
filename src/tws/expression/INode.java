package tws.expression;

/**
 * Ein Knoten stellt ein Argument oder eine Operation in einer Expression da.
 * @author TheWhiteShadow
 * @see Argument
 * @see Operation
 */
interface INode
{
	public static final INode[] EMPTY = new INode[0];
	
	/**
	 * Gibt das zum Knoten gehörende Argument zurück. Wenn der Knoten eine Operation enthält, wird diese Aufgelöst.
	 * @return Das Argument des Knoten.
	 */
	public Argument getArgument();

	/**
	 * Gibt die Kindknoten zurück.
	 * @return die Kindknoten.
	 * @see #getParent()
	 */
	public INode[] getChildren();
	
	/**
	 * Gibt die Position des Knoten innerhalb der Expression zurück.
	 * @return Position innerhalb der Expression.
	 */
	public int getSourcePos();

	/**
	 * Gibt die Expression des Knoten zurück.
	 * @return die Expression.
	 */
	public Expression getExpression();

	/**
	 * Gibt den Elternknoten zurück.
	 * @return den Elternknoten.
	 * @see #getChildren()
	 */
	public INode getParent();
}
