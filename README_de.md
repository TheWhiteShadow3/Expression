# Java Expression-Library by TheWhiteShadow
Copyright (C) 2017 by TheWhiteShadow

See [english version of this document](./README.md)

Die **Expression**-Library ermöglicht das Auswerten von Ausdrücken, die als Strings übergeben werden.
Es können mathematische-, logische-, String- und Array-Operationen, Vergleiche, Variablen und Funktionen benutzt werden.
Ausdrücke können kompiliert werden um mit verschiedenen Parametern zu einem späteren Zeitpunkt aufgelöst zu werden.
Das Verhalten bei unterschiedlichen Typen, kann konfiguriert werden um z.B. Zahlen statt boolsche Werte zu verwenden.

### Beispiel:
```Java
// Gibt eine Long mit dem Wert 3 zurück.
new Expression("1 + 2").evaluate();
// Gibt eine Boolean mit dem Wert 3 zurück.
new Expression("1 < 2").evaluate();
```

```Java
// Kompiliere den Ausdruck ohne ihn auszuwerten.
Expression exp = new Expression("2 * pow(var, 2)").compile();

//... (var setzen) ...

// Werte den Ausdruck als Objekt aus,
Object obj = exp.evaluate();
// oder als flexibel zu konvertierendes Argument.
Argument arg = exp.resolve();

// Konvertiere das Ergebnis
long l = arg.asLong();
double d = arg.asDouble();
String s = arg.toString();
//...
```
*Beim Kompilieren werden Ausdrücke bereits so weit wie möglich gekürzt ohne Variablen aufzulösen oder Funktionen aufzurufen.*

## Debuging
Durch die detailierten Fehlerinformationen lassen sich Expressions leicht debuggen.
Es wird so weit vorhanden, immer der Ausdruck und die Position des Fehlers angezeigt.
### Beispiel "(1 + (2)"
```
tws.expression.EvaluationException: Missing token ')'
(1 + (2)
        ^
```

## Operatoren
die benutzt werden können sortiert nach der Priorität:
 * .  Funktion oder Eigenschaft eines Objekts (Funktioniert nur mit einem Invoker)
 * [] Index
 * !  Nicht
 * %  Modulo
 * *  Multiplikation
 * /  Division
 * +  Addition, String-Konkatenation 
 * -  Minus
 * << links-Shift
 * >> rechts-Shift
 * >>> Vorzeichenloser rechts-Shift
 * >  Gößer
 * <  Kleiner
 * >= Gößer gleich
 * <= Kleiner gleich
 * =  Gleich
 * != Ungleich
 * &  Und
 * |  Oder
 * ^  Exklusiv-Oder
 * := Zuweisung

## Vordefinierte Konstanten

 * PI 3.141592653589793
 * E  2.718281828459045

## Vordefinierte Funktonen

 * min
 * max
 * sin
 * cos
 * tan
 * asin
 * acos
 * atan
 * atan2
 * sinh
 * cosh
 * tanh
 * hypot
 * abs
 * sign (Math.signum)
 * exp
 * log
 * log10
 * round
 * floor
 * ceil
 * rad (Math.toRadians)
 * deg (Math.toDegrees)
 * pow
 * rand

*Wenn nichts anderes angegeben ist, entsprechen die Funktionen denen in der java.lang.Math Klasse.*

## Variablen
Variablen werden durch einen *Resolver* ersetzt, der in der Konfiguration gesetzt werden kann.
Die drei vorbelegten Schlüsselwörter `true`, `false` und `null` können nicht als Namen
für Variablen oder Funktionen benutzt werden.

### Beispiel:
```Java
Expression.DEFAULT_CONFIG.resolver = new Resolver() {
	public Object resolve(String refName, Argument[] args) throws EvaluationException {
		return 5;
	}
};
new Expression("var2").evaluate() == 10
```

Variablen können für einen späteren Zugriff gespeichert werden.
Dazu kann der interne Kontainer mit *Config#useVariables* an geschaltet werden oder
ein externer Resolver benutzt werden.

### Beispiel:
```Java
new Expression("var := 5").evaluate() == 5;
new Expression("var").evaluate() == 5
```

Die Gültigkeit der Variablen hängt direkt mit dem Config-Objekt zusammen.
Nur Expressions mit gleichem Config-Objekt sehen die gleichen Variablen.

### Beispiel:
```Java
Config config = new Config();
config.assign("myInt", new Integer(1024));
new Expression("myInt").resolve(); // --> Fehler!
new Expression("myInt", config).resolve();
```

## Eigene Konstanten und Funktionen
Es können auch eigene Konstanten und Funktionen definiert werden.

Siehe hierfür: [Funktionen und Konstanten](./FunctionsAndConstants_de.md)

## Arrays und Listen
Im Zusammenhang mit Variablenzugriffen kann auf Elemente von **Arrays und Listen** zugegriffen werden.
Die Syntax für Mehrdimensionale Arrays entspricht dabei der Form in Java.

### Beispiel:
```Java
// Sei array1D ein 1-Dimensionales Array oder eine Liste vom Typ java.util.List:
new Expression("array1D[1]").evaluate();
// Sei array2D ein 2-Dimensionales Array oder eine verschachtelte Liste vom Typ java.util.List:
new Expression("array2D[1][2]").evaluate();
// Das Definieren und Zuweisen einzelner Array- und Listen-Elemente ist ebenfalls möglich.
new Expression("array[2] := [1, 2, [3, 4]]").evaluate();
```

## Felder und Methoden
Im Config-Objekt kann ein Invoker gesetzt werden um auf Objekt-Eigenschaften zuzugreifen oder Methoden aufzurufen.
Eine Standard-Klasse *DefaultInvoker* ist dafür bereits vorhanden.

### Beispiel:
```Java
new Expression("[1, 2, 3].size()", config).resolve() == 3
```

Für mehr Beispiele siehe die Unit Testklasse: [EvaluationTest](test/src/tws/test/EvaluationTest.java)
