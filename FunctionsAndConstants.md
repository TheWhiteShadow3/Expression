# Constants, variables, and functions

## Create your own Resolver
The number of constants and functions can be extended arbitrarily with a separate Resolver.
For this purpose, a class must be created that implements the Resolver interface.

### Example:
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

Note that for constants the second argument '*args*' is always null.
In the case of a function without arguments, an empty array is passed.

The new resolver must still be passed to a Config object.
The self-defined function can now be used in all expressions with this Config object.

```Java
Config config = new Config();
config.debug = true;
config.resolver = new MyResolver();

new Expression("fac(6)", config).evaluate();
```

Please note that always the internal resolver is called first.
Thus all predefined functions are no longer forwarded.
Will mann change the behavior - *z.B. To implement your own mathematical functions* - you can set the corresponding properties to false in the Config object.

```Java
config.usePredefinedContants = false;
config.usePredefinedFunctions = false;
```

This does not resolve any predefined constants or functions intenr, and redirect all calls to your own resolver.

## Manage variables yourself

A separate resolver can also be used to define and resolve variables.
To do this, the UseVariables property must be set to false in the Config object *(default)*.

```Java
config.useVariables = false;
```

### Example:
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

What is hidden in detail behind **get_variable** and **set_variable** depends on the implementation.
The internal resolver operates e.g. with a HashMap.