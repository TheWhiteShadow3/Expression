package tws.expression;

/**
 * Ein Resolver kann Variablen und Funktionen auflösen.
 * Optional kann er auch Variablen speichern.
 * @author TheWhiteShadow
 * @see Config
 * @see Invoker
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
	 * Weißt einer Variable einen Wert zu.
	 * <p>
	 * <i>Die Methode ist optional.</i><br>
	 * Damit die Methode überhaupt aufgerufen wird, muss {@link Config#useVariables} auf <code>false</code> sein.
	 * </p>
	 * @param name Name der Variable.
	 * @param value Wert der Zuweisung.
	 * @throws EvaluationException Wenn der Wert nicht zugewiesen werden konnte.
	 */
	public void assign(String name, Object value) throws EvaluationException;
}
