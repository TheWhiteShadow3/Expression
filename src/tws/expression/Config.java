package tws.expression;

import java.text.Collator;
import java.util.Map;


/**
 * Konfiguration für Ausdrücke.
 * Die Konfiguration bestimmt:
 * <ul>
 * <li>Relationale Vergleiche bei Strings</li>
 * <li>das Verhalten bei Typkonvertierung und inkomplatiblen Datentypen.</li>
 * <li>Ob und wie Variablen und funktionenen aufgelöst werden sollen.</li>
 * </ul>
 * Außerdem verwaltet die Konfiguration den internen Variablen-Pool.
 * <p>
 * Eine Default Instanz der Klasse befindet sich in {@link Expression#DEFAULT_CONFIG}
 * </p>
 * @author TheWhiteShadow
 */
public final class Config
{
	/**
	 * Definiert Konstanten, die die Interpretation und Konvertierung von boolschen Werten bestimmen.
	 */
	public static final class BooleanBehavor
	{
		/**
		 * Nur der boolsche Wert <code>true</code> wird als Wahr behandelt und der Wert <code>false</code> als nicht Wahr.
		 * Nicht boolsche Typen werfen eine Ausahme. Dieser Wert kann nicht mit anderen Werten kombiniert werden.
		 */
		public static final int ONLY_BOOLEAN	 	= 0;
		/**
		 * Alle positiven Zahlen werden als Wahr behandelt.
		 * Nicht boolsche oder numerische Typen werfen eine Ausahme.
		 */
		public static final int POSITIVE_NUMBERS 	= 1;
		/** 
		 * Alle Zahlen mit Ausnahme der 0 werden als Wahr behandelt.
		 * Nicht boolsche oder numerische Typen werfen eine Ausahme.
		 */
		public static final int NON_ZERO_NUMBERS 	= 2;
		/**
		 * Alle Zeichenketten mit mehr als 0 Zeichen werden als Wahr behandelt.
		 * Nicht boolsche oder String Typen werfen eine Ausahme.
		 */
		public static final int NON_EMPTY_STRINGS 	= 4;
		/**
		 * Konvertiert den boolschen Wert <code>false</code> bei Bedarf zu 0 und den Wert <code>true</code> zu 1.
		 */
		public static final int BOOLEAN_TO_NUMBER	= 8;

	}
	
	/**
	 * Definiert Konstanten, die die Konvertierung von <code>null</code> bestimmen.
	 */
	public static final class NullBehavor
	{
		public static final int NONE	= 0;
		/**
		 * Konvertiert <code>null</code> bei Bedarf zu einer leeren Zeichenkette.
		 */
		public static final int TO_EMPTY_STRING	= 1;
		/**
		 * Konvertiert <code>null</code> bei Bedarf zum boolschen Wert false.
		 */
		public static final int TO_FALSE		= 2;
	}
	
	/**
	 * Schaltet die Debug-Ausgaben ein.
	 */
	public boolean debug = false;
	
	/**
	 * Eine Kombintion aus Werten der Klasse {@link BooleanBehavor}, 
	 * die die Interpretation und Konvertierung von boolschen Werten bestimmt.
	 * Default ist {@link BooleanBehavor#ONLY_BOOLEAN}
	 */
	public int booleanBehavor = BooleanBehavor.ONLY_BOOLEAN;
	
	/**
	 * Eine Kombintion aus Werten der Klasse {@link NullBehavor}, die die Konvertierung von null bestimmt.
	 * Default ist {@link NullBehavor#NONE}
	 */
	public int nullBehavor = NullBehavor.NONE;
	
	/**
	 * Gibt an, ob vordefinierte Konstanten ausgewertet werden sollen.
	 * Default ist <code>true</code>.
	 * <p>
	 * Vordefiniert sind {@link Math#PI} und {@link Math#E}.
	 * </p>
	 */
	public boolean usePredefinedContants = true;
	
	/**
	 * Gibt an, ob vordefinierte Funktionen ausgewertet werden sollen.
	 * Default ist <code>true</code>.
	 */
	public boolean usePredefinedFunctions = true;

	/**
	 * Gibt an, ob intern ein Variablen-pool angelegt werden soll.
	 * Wenn ja, werden keine Zuweisungen mehr am Resolver gesendet.
	 * Default ist <code>false</code>.
	 */
	public boolean useVariables = false;
	
	public boolean useInvocations = false;
	
	/**
	 * Der Resolver, der für das Auflösen von Variablen und Funktionen benutzt werden soll,
	 * so wie option auch für das Setzten von Variablen.
	 * Default ist <code>null</code>.
	 */
	public Resolver resolver;
	
	/**
	 * Der Collator, der für String-Vergleiche benutzt werden soll.
	 * Default ist <code>Collator.getInstance()</code>.
	 */
	public Collator stringCollator = Collator.getInstance();
	
	/** Interner Resolver für vordefinierte Variablen und Funktionen. */
	final InternalResolver internalResolver;

	
	/**
	 * Erstellt eine neue Konfiguration für eine Expression mit Standardwerten.
	 */
	public Config()
	{
		this.internalResolver = new InternalResolver(this);
	}
	
	/**
	 * Erstellt eine neue Konfiguration für eine Expression mit den Werten aus einer vorhandenen Konfiguration.
	 * @param config Konfiguration, die kopiert werden soll.
	 */
	public Config(Config config)
	{
		this.internalResolver = config.internalResolver;
		this.booleanBehavor = config.booleanBehavor;
		this.nullBehavor = config.nullBehavor;
		this.usePredefinedContants = config.usePredefinedContants;
		this.usePredefinedFunctions = config.usePredefinedFunctions;
		this.resolver = config.resolver;
		this.stringCollator = config.stringCollator;
	}
	
	/**
	 * Bestimtm ob ein übergebenes Argument als true ausgewertet werden soll.
	 * @param arg Argument
	 * @return <code>true</code>, wenn das Argument den Wert Wahr darstellt, andernfalls <code>false</code>.
	 * @throws EvaluationException Wenn das Argument nicht in ein boolschen Wert konvertiert werden kann.
	 */
	public boolean isTrue(Argument arg) throws EvaluationException
	{
		if (arg.isBoolean()) return arg.asBoolean();

		if (arg.isNumber())
		{
			if ((booleanBehavor & (BooleanBehavor.POSITIVE_NUMBERS | BooleanBehavor.NON_ZERO_NUMBERS)) == 0)
				throw new EvaluationException("Can not cast number to boolean.");
			
			if ( (booleanBehavor & BooleanBehavor.POSITIVE_NUMBERS) != 0 && arg.asDouble() > 0) return true;
			if ( (booleanBehavor & BooleanBehavor.NON_ZERO_NUMBERS) != 0 && arg.asDouble() != 0) return true;
			return false;
		}
		
		if (arg.isString())
		{
			if ((booleanBehavor & BooleanBehavor.NON_EMPTY_STRINGS) != 0) return arg.asString().length() > 0;
			
			throw new EvaluationException("Can not cast String to boolean.");
		}
		
		if (arg.isNull())
		{
			if ((nullBehavor & NullBehavor.TO_FALSE) != 0) return false;
			
			throw new EvaluationException("Can not cast null to boolean."); 
		}
		
		if (arg instanceof ObjectArgument && (nullBehavor & NullBehavor.TO_FALSE) != 0)
			return true;
		
		throw new EvaluationException("Can not cast " + arg.getType().getName() + " to boolean."); 
	}
	
	/**
	 * Setzt eine Variable.
	 * Die Variable kann nur gesetzt werden, wenn {@link #useVariables} <code>true</code> ist
	 * oder ein externer Resolver es unterstützt.
	 * @param name Name der Variablen.
	 * @param value Wert, den die Variable erhalten soll.
	 * @throws EvaluationException Wenn das Argument nicht zugewiesen werden konnte.
	 * @see #resolver
	 */
	public void assign(String name, Argument value) throws EvaluationException
	{
		internalResolver.assign(name, value);
	}
	
	/**
	 * Setzt eine Variable.
	 * Die Variable kann nur gesetzt werden, wenn {@link #useVariables} <code>true</code> ist
	 * oder ein externer Resolver es unterstützt.
	 * @param name Name der Variablen.
	 * @param value Wert, den die Variable erhalten soll.
	 * @throws EvaluationException Wenn das Argument nicht zugewiesen werden konnte.
	 * @see #resolver
	 */
	public void assign(String name, Object value) throws EvaluationException
	{
		assign(name, wrap(null, value));
	}
	
	/**
	 * Gibt eine Ansicht auf alle gesetzten Variablen zurück.
	 * @return Map, der gesetzten Variablen.
	 */
	public Map<String, Object> getVariables()
	{
		return internalResolver.getVariables();
	}

	/**
	 * Überprüft, ob boolsche Werte in Zahlen konvertiert werden sollen.
	 * @throws EvaluationException Wenn boolsche Werte nicht in Zahlen konvertiert werden sollen.
	 */
	public void checkBooleanToNumber()
	{
		if ((booleanBehavor & BooleanBehavor.BOOLEAN_TO_NUMBER) == 0)
			throw new EvaluationException("Can not cast boolean to number."); 
	}
	
	/**
	 * Überprüft, ob <code>null</code> in eine leere Zeichenkette konvertiert werden sollen.
	 * @throws EvaluationException Wenn <code>null</code> nicht in eine leere Zeichenkette konvertiert werden sollen.
	 */
	public void checkNullToString()
	{
		if ((nullBehavor & NullBehavor.TO_EMPTY_STRING) == 0)
			throw new EvaluationException("Can not cast null to String."); 
	}

	/**
	 * Kapselt ein Objekt in ein {@link Argument}.
	 * <p>
	 * Für jedes Objekt gilt:
	 * <pre>wrap(obj).asObject().equals(obj);</pre>
	 * </p>
	 * @param parent Eltern-Knoten, im Baum. Kann <code>null</code> sein.
	 * @param obj Das Objekt, welches gekapselt werden soll.
	 * @return Ein Argument, mit dem gekapselten Objekt.
	 */
	public static Argument wrap(Node parent, Object obj)
	{
		if (obj == null) return new NullArgument(parent);
		if (obj instanceof Argument) return (Argument) obj;
		
		if (obj instanceof Double || obj instanceof Float)
			return new FloatArgument(parent, ((Number) obj).doubleValue());
		
		if (obj instanceof Number)
			return new IntegerArgument(parent, ((Number) obj).longValue());
		
		if (obj instanceof Boolean)
			return new BooleanArgument(parent, ((Boolean) obj).booleanValue());
		
		if (obj instanceof String)
			return new StringArgument(parent, (String) obj);
		
		return new ObjectArgument(parent, obj);
	}
}
