package tws.expression;


/**
 * Eine Wrapper-Klasse. Kappselt ein Argument in eine Operation.
 * Das Argument kann über {@link #resolve()} extrahiert werden. Die Methode wirft niemals eine Exception.
 * @author TheWhiteShadow
 */
public class WrapperOperation implements Operation
{
	private Argument value;
	
	WrapperOperation(Argument value)
	{
		this.value = value;
	}
	
	public Argument resolve() throws EvaluationException
	{
		return value;
	}

	@Override
	public String toString()
	{
		return value.toString();
	}
}
