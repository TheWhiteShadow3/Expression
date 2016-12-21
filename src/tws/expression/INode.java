package tws.expression;

public interface INode
{
	public static final INode[] EMPTY = new INode[0];
	
	public Argument getArgument();

	public INode[] getChildren();

	public int getSourcePos();

	public Expression getExpression();

	public INode getParent();
}
