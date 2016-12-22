package tws.expression;

/**
 * Eine dynamische Operation ist ein kompilierter Ausdruck mit Parametern, der noch nicht Aufgelöst wurde.
 * @author TheWhiteShadow
 */
public interface DynamicOperation extends Operation
{
	/**
	 * Gibt das Ergebnis der Operation zurück.
	 * Das Ergebnis kann von vorherigen Aufrufen abweichen, wenn beispielsweise Variablen über einen {@link Resolver} aufgelöst werden müssen.
	 * @param args Argumente der Operation.
	 * @return Das Ergebnis der Operation.
	 * @throws EvaluationException wenn die Operation nicht aufgelöst werden kann.
	 */
	public Argument call(Argument[] args) throws EvaluationException;
}
