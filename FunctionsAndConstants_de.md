# Konstanten, Variablen und Funktionen

## Eigenen Resolver erstellen
Die Anzahl der Konstanten und Funktionen kann mit einem eigenen Resolver beliebig erweitert werden.
Hierfür muss eine Klasse erstellt werden die das Resolver-Interface implementiert.

### Beispiel:
```Java
class MyResolver implements Resolver {
	public Object resolve(String name, Argument[] args) throws EvaluationException {
		if (args == null) {
			if ("const".equals(name)) return MY_CONSTANT;
			// ...
		}
		else {
			if ("fac".equals(name)) return factorial(args);
			// ...
		}
	}
	
	private long factorial(Argument[] args) {
		if (args.length != 1) throw new EvaluationException("invalid number of arguments.");
		
		long f = 1;
		long n = args[0].asLong();
		while (n > 1) {
			f *= n;
			n--;
		}
		return f;
	}
}
```

Zu Beachten ist, dass bei Konstanten das zweite Argument '*args*' immer null ist.
Bei einer Funktion ohne Argumente wird dagegen ein leeres Array übergeben.

Im Anschluss muss der neue Resolver noch einem Config-Objekt übergeben werden.
In allen Expressions mit diesem Config-Objekt kann jetzt die selbst definierte Funktion benutzt werden.

```Java
Config config = new Config();
config.debug = true;
config.resolver = new MyResolver();

new Expression("fac(6)", config).evaluate();
```

Zu Beachten ist, dass immer zuerst der interne Resolver aufgerufen wird.
Somit werden alle vordefinierten Funktionen nicht mehr weiter geleitet.
Will mann das Verhalten ändern - *z.B. um eigene Mathematische Funktionen zu implementieren* - kann man im Config-Objekt die entsprechenden Eigenschaften auf false setzen.

```Java
config.usePredefinedContants = false;
config.usePredefinedFunctions = false;
```

Dadurch werden keine vordefinierten Konstanten oder Funktionen mehr intern aufgelöst und alle Aufrufe an den eigenen Resolver weitergeletet.

##Variablen selbst verwalten

Ein eigener Resolver kann auch benutzt werden um Variablen zu definieren und aufzulösen.
Hierfür muss im Config-Objekt die Eigenschaft useVariables auf false gesetzt bleiben *(default)*.

```Java
config.useVariables = false;
```

### Beispiel:
```Java
class MyResolver implements Resolver {
	public Object resolve(String name, Argument[] args) throws EvaluationException {
		if (args == null)
			return get_variable(name);
		}
	}
	
	public void assign(String name, Argument value) throws EvaluationException {
		set_variable(name, value);
	}
}
```

Was sich im Einzelnen hinter **get_variable** und **set_variable** verbirgt, hängt von der Implementierung ab.
Der interne Resolver arbeitet z.B. mit einer HashMap.