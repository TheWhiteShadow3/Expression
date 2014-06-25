package tws.expression;

import java.util.List;

/**
 * Ein Argument stellt einen festen Wert da.
 * Der Datentyp des Arguments kann über {@link #getType()} ermittelt werden.
 * Das Interface bietet verschiednene Methoden zur Konvertierung bereit.
 * Wenn der Datentyp nicht konvertiert werden kann, wird eine {@link EvaluationException} geworfen.
 * @author TheWhiteShadow
 */
public interface Argument
{
	/**
	 * Gibt den Typ des Arguments zurück.
	 * Eine <code>null</code>-Referenz wird durch den Typ <code>void.class</code> dargestellt.
	 * @return Den Typ des Arguments. Niemals null.
	 */
	public Class<?> getType();
	
	/**
	 * Gibt an, ob das Argument ein numerischen Typ darstellt.
	 * Numerische Typen sind long und double.
	 * @return <code>true</code>, wenn das Argument einen numerischen Typ darstellt, andernfalls <code>false</code>.
	 */
	public boolean isNumber();
	
	/**
	 * Gibt an, ob das Argument ein String darstellt.
	 * @return <code>true</code>, wenn das Argument einen String darstellt, andernfalls <code>false</code>.
	 */
	public boolean isString();

	/**
	 * Gibt an, ob das Argument ein boolschen Wert darstellt.
	 * @return <code>true</code>, wenn das Argument einen boolschen Wert darstellt, andernfalls <code>false</code>.
	 */
	public boolean isBoolean();
	
	/**
	 * Gibt an, ob das Argument <code>null</code> darstellt.
	 * @return <code>true</code>, wenn das Argument <code>null</code> darstellt, andernfalls <code>false</code>.
	 */
	public boolean isNull();
	
	/**
	 * Gibt an, ob das Argument ein Objekt darstellt.
	 * <p>
	 * <b>Achtung:</b> Bei einem String gibt die Methode false zurück.
	 * </p>
	 * @return <code>true</code>, wenn das Argument einen Objekt darstellt, andernfalls <code>false</code>.
	 */
	public boolean isObject();
	
	/**
	 * Gibt den Wert als <code>String</code> zurück.
	 * @return Das Argument als <code>String</code>.
	 * @throws EvaluationException wenn das Argument nicht in einen <code>String</code> konvertiert werden kann.
	 */
	public String asString();
	
	/**
	 * Gibt den Wert als <code>boolean</code> zurück.
	 * @return Das Argument als <code>boolean</code>.
	 * @throws EvaluationException wenn das Argument nicht in ein <code>boolean</code> konvertiert werden kann.
	 */
	public boolean asBoolean();
	
	/**
	 * Gibt den Wert als <code>double</code> zurück.
	 * @return Das Argument als <code>double</code>.
	 * @throws EvaluationException wenn das Argument nicht in ein <code>double</code> konvertiert werden kann.
	 */
	public double asDouble();
	
	/**
	 * Gibt den Wert als <code>long</code> zurück.
	 * @return Das Argument als <code>long</code>.
	 * @throws EvaluationException wenn das Argument nicht in ein <code>long</code> konvertiert werden kann.
	 */
	public long asLong();
	
	/**
	 * Gibt den Wert als <code>Object</code> zurück.
	 * Die Methode wirft keine Exception, da jeder Datentyp in ein Objekt konvertiert werden kann.
	 * @return Das Argument als <code>Object</code>.
	 */
	public Object asObject();
	
	/**
	 * Gibt den Wert als <code>List</code> zurück.
	 * Datentypen, die keine Liste representieren, werden in eine Liste gekapselt.
	 * <ul>
	 * <li>Bei primitiven Arrays werden die Elemente durch Boxing in Listenfähige Objekte konvertiert.</li>
	 * <li>Ein <code>String</code> representiert eine Liste von Zeichen.</li>
	 * <li>Bei <code>null</code> wird eine leere Liste zurückgegeben.</li>
	 * </ul>
	 * Die Methode wirft keine Exception, da jeder Datentyp in einer Liste abgelegt werden kann.
	 * @return Das Argument als <code>Liste</code>.
	 */
	public List<?> asList();
}
