package tws.expression;

/**
 * Ein Invoker wertet eine Variable oder Funktion in Bezug auf einen Empfänger auf, der über den "DOT"-Operator verknüpft ist.
 * Er kann somit Felder lesen und setzen sowie Methoden von Objekten aufrufen.
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
	 * @param args Ein Array mit den Argumenten der Funktion oder dem neuen Wert der Eigenschaft.
	 * @return Der Inhalt des Feldes oder das Ergebnis der Methode.
	 * @throws Exception Wenn der Aufruf fehlt schlägt.
	 */
	public Object invoke(Argument reciever, String name, Argument[] args) throws Exception;
}
