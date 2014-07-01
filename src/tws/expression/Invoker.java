package tws.expression;

/**
 * Ein Invoker kann Felder und  Methoden auflösen.
 * Optional kann er auch Variablen speichern.
 * @author TheWhiteShadow
 * @see Config
 * @see Resolver
 */
public interface Invoker
{
	/**
	 * Fragt ein Feld ab oder Ruft eine Methode auf.
	 * @param reciever Empfänger des Aufrufs.
	 * @param name Name der Eigenschaft oder Funktion.
	 * @param args Ein Array mit den Argumenten der Funktion.
	 * @return Der Inhalt des Feldes oder das Ergebnis der Methode.
	 * @throws Exception Wenn der Aufruf fehlt schlägt.
	 */
	public Object invoke(Argument reciever, String name, Argument[] args) throws Exception;
}
