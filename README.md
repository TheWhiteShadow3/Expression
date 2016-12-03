# Java Expression Library by TheWhiteShadow
Copyright (C) 2017 by TheWhiteShadow

See [german version of this document](./README_de.md)

The **Expression** library allows you to evaluate expressions that are passed as strings.
Mathematical, logical, string, and array operations, comparisons, variables, and functions can be used.
Expressions can be compiled to resolve with different parameters at a later time.
The behavior for different types may be configured to, e.g. Numbers instead of boolean values.

### Example:
```Java
// Returns a long value of 3.
new Expression("1 + 2").evaluate();
// Returns a boolean value of 3.
new Expression("1 < 2").evaluate();
```

```Java
// Compile the expression without evaluating it.
Expression exp = new Expression("2 * pow(var, 2)").compile();

// ... (set var) ...

// resolve the expression as an object,
Object obj = exp.evaluate ();
// or as a flexible argument to convert.
Argument arg = exp.resolve ();

// Convert the result
long l = arg.asLong();
double d = arg.asDouble();
String s = arg.toString();
// ...
```
*When compiling, expressions are already truncated as much as possible without resolving variables or calling functions.*

## Debugging
The detailed error information makes debugging easy.
The expression string and the position of the error are displayed as far as possible.
### Example "(1 + (2)"
```
tws.expression.EvaluationException: Missing token ')'
(1 + (2)
        ^
```

## Operators
Which can be used sorted by priority:
 * .  function or property of an object (works only with an Invoker)
 * [] index
 * !  not
 * %  modulo
 * *  multiplication
 * /  division
 * +  addition, string concatenation
 * -  minus
 * << left-shift
 * >> right shift
 * >>> Unsigned right shift
 * >  greater then
 * <  less then
 * >= greater then or equal
 * <= less than or equal
 * =  equal
 * != not equal
 * &  And
 * |  Or
 * ^  exclusive Or
 * := assignment

## Predefined constants

 * PI 3.141592653589793
 * E  2.718281828459045

## Predefined functions

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

*Unless otherwise specified, the functions are the same as those in the java.lang.Math class.*

## Variables
Variables are replaced by a *Resolver*, which can be set in the configuration.
The three predefined keywords `true`, `false` and `null` can not be used as names
For variables or functions.

### Example:
```Java
Expression.DEFAULT_CONFIG.resolver = new Resolver() {
	public Object resolve(String refName, Argument[] args) throws EvaluationException {
		return 5;
	}
};
new Expression("var2").evaluate() == 10
```

Variables can be stored for later access.
The internal container can be switched on with *Config#useVariables* or
An external resolver can be used.

### Example:
```Java
new Expression("var := 5").evaluate() == 5;
new Expression("var").evaluate() == 5
```

The validity of the variables is directly related to the Config object.
Only expressions with the same Config object see the same variables.

### Example:
```Java
Config config = new Config ();
Config.assign("myInt", new Integer(1024));
new Expression("myInt").resolve(); // -> Error!
new Expression("myInt", config).resolve();
```

## Own constants and functions
You can also define your own constants and functions.

See: [Functions and constants](./FunctionsAndConstants.md)

## arrays and lists
In the context of variable access, you can access elements of **arrays and lists**.
The syntax for multi-dimensional arrays is the same as in Java.

### Example:
```Java
// Let array1D be a 1-dimensional array or a list of type java.util.List:
new Expression("array1D[1]"). Evaluate ();
// Let array2D be a 2-dimensional array or a nested list of type java.util.List:
new Expression("array2D[1][2]") evaluate ();
// Defining and assigning individual array and list elements is also possible.
new Expression("array[2]: = [1, 2, [3, 4]]").
```

## Fields and methods
In the Config object, an invoker can be set to access object properties or to call methods.
A default class * DefaultInvoker * already exists for this.

### Example:
```Java
New Expression( "[1, 2, 3].size()", config).resolve () == 3
```
For more examples, see the unit test class: [EvaluationTest] (test/src/tws/test/EvaluationTest.java)
