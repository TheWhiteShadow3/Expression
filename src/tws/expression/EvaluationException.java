package tws.expression;

import java.util.Arrays;


/**
 * Wird geworfen, wenn beim Kompilieren oder Auflösen eines Ausdrucks ein Fehler entsteht.
 * @author TheWhiteShadow
 */
public class EvaluationException extends RuntimeException
{
	private static final long serialVersionUID = 4964830263576341968L;

	public EvaluationException(String message)
	{
		super(message);
	}
	
	public EvaluationException(String message, Exception e)
	{
		super(message, e);
	}
	
	public EvaluationException(Node node, String message, Throwable cause)
	{
		super(createMessage(node.getExpression().getSourceString(), node.getSourcePos(), message), cause);
	}
	
	public EvaluationException(String sourceString, int pos, String message, Throwable cause)
	{
		super(createMessage(sourceString, pos, message), cause);
	}
	
	public EvaluationException(Node node, String message)
	{
		super(createMessage(node.getExpression().getSourceString(), node.getSourcePos(), message));
	}

	public EvaluationException(String sourceString, int pos, String message)
	{
		super(createMessage(sourceString, pos, message));
	}

	private static String createMessage(String sourceString, int pos, String message)
	{
		StringBuilder builder = new StringBuilder(256);
		builder.append(message);
		builder.append('\n').append(sourceString);
		
		char[] chars = Arrays.copyOf(sourceString.toCharArray(), sourceString.length() + 1);
		Arrays.fill(chars, ' ');
		chars[pos] = '^';
		
		builder.append('\n').append(chars);
		return builder.toString();
	}
}
