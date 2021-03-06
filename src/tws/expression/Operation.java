package tws.expression;

/**
 * Eine Operation ist ein kompilierter Ausdruck, der noch nicht Aufgelöst wurde.
 * @author TheWhiteShadow
 */
public interface Operation
{
	/**
	 * Gibt das Ergebnis der Operation zurück.
	 * Wenn es sich um eine {@link DynamicOperation} handelt, kann das Ergebnis von vorherigen Aufrufen abweichen,
	 * wenn beispielsweise Variablen über einen {@link Resolver} aufgelöst werden müssen.
	 * @return Das Ergebnis der Operation.
	 * @throws EvaluationException wenn die Operation nicht aufgelöst werden kann.
	 */
	public Argument resolve() throws EvaluationException;
}
