package tws.expression;

/**
 * Ein Resolver kann Variablen und Funktionen auflösen.
 * Optional kann er auch Variablen speichern.
 * @author TheWhiteShadow
 */
public interface Resolver
{
	/**
	 * Löst den angegebenen Namen auf. Dieser kann entweder eine Variable oder eine Funktion referenzieren.
	 * @param name Name der Variablen oder Funktion.
	 * @param args <code>null</code>, wenn es sich um eine Variable handelt,
	 * ansonsten ein Array mit den Argumenten der Funktion.
	 * @return Ergebnis der Auflösung.
	 * @throws EvaluationException Wenn der Wert nicht aufgelöst werden konnte.
	 */
	public Object resolve(String name, Argument[] args) throws EvaluationException;
	
	/**
	 * Weißt einer Variable einen Wert zu. Die Methode ist optional.
	 * @param name Name der Variable.
	 * @param arg Wert der Zuweisung.
	 * @throws EvaluationException
	 */
	public void assign(String name, Argument arg) throws EvaluationException;
}
