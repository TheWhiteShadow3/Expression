package tws.expression;


/**
 * Ermöglicht das Auswerten von Ausdrücken, die als Strings übergeben werden.
 * Es können mathematische-, logische-, String- und Array-Operationen, Vergleiche, Variablen und Funktionen benutzt werden.
 * Ausdrücke können kompiliert werden um mit verschiedenen Parametern zu einem späteren Zeitpunkt aufgelöst zu werden.
 * Das Verhalten bei unterschiedlichen Typen, kann konfiguriert werden um z.B. Zahlen statt boolsche Werte zu verwenden.
 * <p>Beispiel:
 * <pre>
 * new Expression("1 + 2").evaluate() == 3
 * new Expression("1 &lt; 2").evaluate() == true</pre>
 * </p>
 * <b>Operatoren</b>, die benutzt werden können sortiert nach der Priorität:
 * <pre>
 * .  Funktion oder Eigenschaft eines Objekts (Funktioniert nur mit einem {@link Invoker})
 * [] Index
 * !  Nicht
 * %  Modulo
 * *  Multiplikation
 * /  Division
 * +  Addition, String-Konkatenation 
 * -  Minus
 * &lt;&lt; links-Shift
 * &gt;&gt; rechts-Shift
 * &gt;  Gößer
 * &lt;  Kleiner
 * &gt;= Gößer gleich
 * &lt;= Kleiner gleich
 * =  Gleich
 * != Ungleich
 * &amp;  Und
 * |  Oder
 * ^  Exklusiv-Oder
 * := Zuweisung</pre>
 * </p>
 * <p>
 * <b>Variablen und Funktionen</b> werden durch einen {@link Resolver} ersetzt, der in der Konfiguration gesetzt werden kann.
 * Die drei vorbelegten Schlüsselwörter true, false und null können nicht als Namen
 * für Variablen oder Funktionen benutzt werden.
 * <br>Beispiel:<br>
 * <pre>
 * Expression.DEFAULT_CONFIG.resolver = new Resolver() {
	public Object resolve(String refName, Argument[] args) throws EvaluationException {
		return 5;
	}
 * };
 * new Expression("var * 2").evaluate() == 10</pre>
 * </p>
 * <p>
 * Variablen können für einen späteren Zugriff gespeichert werden.
 * Dazu kann der interne Kontainer mit {@link Config#useVariables} an geschaltet werden oder
 * ein externer Resolver benutzt werden.
 * <pre>
 * new Expression("var := 5").evaluate() == 5;
 * new Expression("var").evaluate() == 5</pre>
 * Die Gültigkeit der Variablen hängt direkt mit dem Config-Objekt zusammen.
 * Nur Expressions mit gleichem Config-Objekt sehen die gleichen Variablen.
 * </p>
 * <p>
 * Im Zusammenhang mit Variablenzugriffen kann auf Elemente von <b>Arrays und Listen</b> zugegriffen werden.
 * Die Syntax für Mehrdimensionale Arrays entspricht dabei der Form in Java.
 * </p>
 * <i>Sei array1D ein 1-Dimensionales Array oder eine Liste vom Typ {@link java.util.List}:</i>
 * <pre>new Expression("array1D[1]").evaluate();</pre>
 * <i>Sei array2D ein 2-Dimensionales Array oder eine verschachtelte Liste vom Typ {@link java.util.List}:</i>
 * <pre>new Expression("array2D[1][2]").evaluate();</pre>
 * <p>Das Definieren und Zuweisen einzelner Array-Elemente ist ebenfalls möglich.
 * <pre>new Expression("array[2] := [1, 2, [3, 4]]").evaluate();</pre>
 * </p>
 * @author TheWhiteShadow
 * @see Config
 */
public class Expression
{
	/**
	 * Die Default-Konfiguration.
	 */
	public static final Config DEFAULT_CONFIG = new Config();
	
	private static ThreadLocal<ExpressionParser> parserPool = new ThreadLocal<ExpressionParser>()
	{
		@Override
		protected ExpressionParser initialValue()
		{
			return new ExpressionParser();
		}
	};
	
	private Config config;
	private String sourceString;
	
	/**
	 * Erstellt einen neuen Ausdruck mit der angegebenen Konfiguration.
	 * @param sourceString String, der den Ausdruck enthält.
	 * @param config Konfiguration, die für den Ausdruck verwendet werden soll.
	 */
	public Expression(String sourceString, Config config)
	{
		if (config == null) throw new NullPointerException("config is null");
		if (sourceString == null) throw new NullPointerException("sourceString is null");
		
		this.sourceString = sourceString;
		this.config = config;
	}
	
	/**
	 * Erstellt einen neuen Ausdruck mit der Standard-Konfiguration.
	 * @param sourceString String, der den Ausdruck enthält.
	 */
	public Expression(String sourceString)
	{
		this(sourceString, DEFAULT_CONFIG);
	}
	
	public Config getConfig()
	{
		return config;
	}
	
	public String getSourceString()
	{
		return sourceString;
	}

	/**
	 * Kompiliert den Ausdruck zu einer Operation. Konstante Teilausdrücke werden dabei ausgewertet.
	 * Variablen und Funktionen werden nicht aufgelöst.
	 * @return Der kompilierte Ausdruck.
	 * @throws EvaluationException Wenn der Ausdruck ungültig ist.
	 * @see #resolve()
	 * @see #evaluate()
	 */
	public Operation compile() throws EvaluationException
	{
		ExpressionParser parser = parserPool.get();
		
		return parser.parse(this);
	}
	
	/**
	 * Löst den Ausdruck auf.
	 * @return Das Ergebnis des Ausdrucks.
	 * @throws EvaluationException Wenn der Ausdruck ungültig ist oder nicht aufgelöst werden kann.
	 * @see #compile()
	 * @see #evaluate()
	 */
	public Argument resolve() throws EvaluationException
	{
		return resolve(true);
	}

	/**
	 * Löst den Ausdruck auf und konvertiert das Ergebnis in den entsprechenen Datentyp.
	 * Die Methode ist äquivalent zum Aufruf:
	 * <pre>expression.resolve().asObject();</pre>
	 * @return Das Ergebnis des Ausdrucks.
	 * @throws EvaluationException Wenn der Ausdruck ungültig ist oder nicht aufgelöst werden kann.
	 * @see #compile()
	 * @see #resolve()
	 */
	public Object evaluate() throws EvaluationException
	{
		return resolve().asObject();
	}
	
	/**
	 * Löst den Ausdruck auf.
	 * Der Parameter bestimmt im Fall, in dem das Ergebnis eine Funktion ist,
	 * ob diese ohne Argumente aufgerufen, und das Ergebnis zurückgegeben werden soll..
	 * @param full Vollständige Auflösung.
	 * @return Das Ergebnis des Ausdrucks.
	 * @throws EvaluationException Wenn der Ausdruck ungültig ist oder nicht aufgelöst werden kann.
	 * @see #compile()
	 * @see #evaluate()
	 */
	public Argument resolve(boolean full) throws EvaluationException
	{
		Argument arg = compile().resolve();
		if (full && arg instanceof Reference)
			return ((Reference) arg).getOperation().call(null);
		else
			return arg;
	}
}
