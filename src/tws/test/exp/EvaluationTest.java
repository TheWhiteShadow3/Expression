package tws.test.exp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import tws.expression.Argument;
import tws.expression.Config;
import tws.expression.DefaultInvoker;
import tws.expression.EvaluationException;
import tws.expression.Expression;
import tws.expression.Operation;
import tws.expression.Resolver;
import tws.expression.WrapperOperation;

public class EvaluationTest
{
	/** Wert den der Resolver bei 'blub' auflösen soll. */
	private static Object blub = null;
	/** Wert den der Resolver bei 'obj' auflösen soll. */
	private static Object obj = new Object();
	/** Wert den der Resolver bei 'array' auflösen soll. */
	static int[][] array = new int[][] {{1, 2, 3}, {4, 5, 6}};
	
	@BeforeClass
	public static void setup()
	{
		Expression.DEFAULT_CONFIG.debug = true;
		Expression.DEFAULT_CONFIG.resolver = new Resolver()
		{
			@Override
			public Object resolve(String refName, Argument[] args) throws EvaluationException
			{
				if ("blub".equals(refName)) return blub;
				if ("obj".equals(refName)) return obj;
				if ("array".equals(refName)) return array;
				
				throw new EvaluationException("");
			}

			@Override
			public void assign(String name, Argument arg) throws EvaluationException
			{
				
			}
		};
	}
	
	@Test
	public void testArithmetics()
	{
		System.out.println("Mathematische operationen");
		long i;
		double d;
		
		i = new Expression("5 + 2").resolve().asLong();
		assertTrue(i == 7);
		
		i = new Expression("5 - 2").resolve().asLong();
		assertTrue(i == 3);
		
		i = new Expression("5 * 2").resolve().asLong();
		assertTrue(i == 10);
		
		i = new Expression("5 / 2").resolve().asLong();
		assertTrue(i == 2);
		
		d = new Expression("5 / 2.0").resolve().asDouble();
		assertTrue(d == 2.5);
		
		i = new Expression("5 % 2").resolve().asLong();
		assertTrue(i == 1);
		
		i = new Expression("-5 % 2").resolve().asLong();
		assertTrue(i == -1);
		
		i = new Expression("5 + -2").resolve().asLong();
		assertTrue(i == 3);
		
		i = new Expression("5 << 2").resolve().asLong();
		assertTrue(i == 20);
		
		i = new Expression("5 >> 2").resolve().asLong();
		assertTrue(i == 1);
		
		i = new Expression("5 >>> 2").resolve().asLong();
		assertTrue(i == 1);
		
		try
		{
			Operation op = new Expression("- 2").compile();
			fail("Fail: " + op.toString());
		}
		catch(EvaluationException e) { handleException(e); }
		
		try
		{
			Operation op = new Expression("1 .2.3").compile();
			fail("Fail: " + op.toString());
		}
		catch(EvaluationException e) { handleException(e); }
		
		System.out.println();
	}
	
	@Test
	public void testPriorities()
	{
		System.out.println("Prioritäten");
		boolean b;
		long i;
		
		i = (Long) new Expression("1 + 2 * 3").evaluate();
		assertTrue(i == 7);
		
		i = (Long) new Expression("8 - 4 / 2").evaluate();
		assertTrue(i == 6);
		
		i = (Long) new Expression("3 * 4 % 3").evaluate();
		assertTrue(i == 0);
		
		b = (Boolean) new Expression("true | false & true").evaluate();
		assertTrue(b);
		
		System.out.println();
	}
	
	@Test
	public void testBraces()
	{
		System.out.println("Klammern");
		long i;
		
		i = (Long) new Expression("((2)-(1))").evaluate();
		assertTrue(i == 1);
		
		i = (Long) new Expression("2 * (1 + 3)").evaluate();
		assertTrue(i == 8);
		
		try
		{
			Operation op = new Expression("(1 + (2)").compile();
			fail("Fail: " + op.toString());
		}
		catch(EvaluationException e) { handleException(e); }
		
		try
		{
			Operation op = new Expression("(1) + 2)").compile();
			fail("Fail: " + op.toString());
		}
		catch(EvaluationException e) { handleException(e); }
		
		try
		{
			Operation op = new Expression("()").compile();
			fail("Fail: " + op.toString());
		}
		catch(EvaluationException e) { handleException(e); }
		
		System.out.println();
	}
	
	@Test
	public void testBoolean()
	{
		System.out.println("Booleans");
		boolean b;
		
		// Konstanten
		b = (Boolean) new Expression("true").evaluate();
		assertTrue(b);
		
		b = (Boolean) new Expression("false").evaluate();
		assertFalse(b);
		
		// Negation
		b = (Boolean) new Expression("!true").evaluate();
		assertFalse(b);
		
		// Gleich/Ungleich
		b = (Boolean) new Expression("1 = 1").evaluate();
		assertTrue(b);
		
		b = (Boolean) new Expression("1 != 2").evaluate();
		assertTrue(b);
		
		b = (Boolean) new Expression("!false").evaluate();
		assertTrue(b);
		
		// Größer/Kleiner
		b = (Boolean) new Expression("2 < 1").evaluate();
		assertFalse(b);
		
		b = (Boolean) new Expression("1 < 2").evaluate();
		assertTrue(b);
		
		b = (Boolean) new Expression("1 > 2").evaluate();
		assertFalse(b);
		
		b = (Boolean) new Expression("2 > 1").evaluate();
		assertTrue(b);
		
		// Größer/Kleinr gleich
		b = (Boolean) new Expression("2 <= 3").evaluate();
		assertTrue(b);
		
		b = (Boolean) new Expression("2 >= 3").evaluate();
		assertFalse(b);
		
		// ODER
		b = (Boolean) new Expression("false | false").evaluate();
		assertFalse(b);
		
		b = (Boolean) new Expression("false | true").evaluate();
		assertTrue(b);
		
		// UND
		b = (Boolean) new Expression("false & true").evaluate();
		assertFalse(b);
		
		b = (Boolean) new Expression("true & true").evaluate();
		assertTrue(b);

		// Exklusiv-ODER
		b = (Boolean) new Expression("false ^ false").evaluate();
		assertFalse(b);
		
		b = (Boolean) new Expression("true ^ true").evaluate();
		assertFalse(b);
		
		b = (Boolean) new Expression("false ^ true").evaluate();
		assertTrue(b);
		
		System.out.println();
	}
	
	@Test
	public void testStrings()
	{
		System.out.println("Strings");
		String s;
		
		s = (String) new Expression("'&b = 5 - 7^2 ''\" '").evaluate();
		assertEquals("&b = 5 - 7^2 '\" ", s);
		
		s = (String) new Expression("'Hallo ' + 'Welt!'").evaluate();
		assertEquals("Hallo Welt!", s);
		
		s = (String) new Expression("'Hallo ' + 'Welt! ' + (75 - 5)").evaluate();
		assertEquals("Hallo Welt! 70", s);
		
		s = (String) new Expression("null").evaluate();
		assertNull(s);
		
		try
		{
			Operation op = new Expression("'test").compile();
			fail("Fail: " + op.toString());
		}
		catch(EvaluationException e) { handleException(e); }
		
		System.out.println();
	}
	
	@Test
	public void testCompilation()
	{
		System.out.println("Compilation");
		Operation op = new Expression("blub").compile();
		
		System.out.println(op);
		
		blub = 5L;
		assertEquals(blub, op.resolve().asLong());
		
		blub = 10L;
		assertEquals(blub, op.resolve().asLong());
		
		Argument arg = op.resolve();
		
		blub = 15L;
		assertEquals(10L, arg.asLong());
		
		op = new Expression("4 + 2 - (4 * 7) % -2").compile();
		// Wenn ein Wrapper benutzt wird, existiert keine echte Operation mehr im Ausdruck.
		assertTrue(op instanceof WrapperOperation);
		
		System.out.println();
	}
	
	@Test
	public void testBooleanBehavor()
	{
		System.out.println("Boolean Verhalten");
		Config config = new Config();
		config.debug = true;
		config.booleanBehavor = Config.BooleanBehavor.ONLY_BOOLEAN;
		
		new Expression("true", config).resolve().asBoolean();
		
		try
		{
			new Expression("2", config).resolve().asBoolean();
			fail("Fail");
		}
		catch(EvaluationException e) { handleException(e); }
		
		try
		{
			new Expression("'blub'", config).resolve().asBoolean();
			fail("Fail");
		}
		catch(EvaluationException e) { handleException(e); }
		
		config.booleanBehavor = Config.BooleanBehavor.POSITIVE_NUMBERS;
		
		assertTrue(new Expression("2", config).resolve().asBoolean());
		assertFalse(new Expression("-2", config).resolve().asBoolean());
		assertFalse(new Expression("0", config).resolve().asBoolean());
		
		config.booleanBehavor = Config.BooleanBehavor.NON_ZERO_NUMBERS;
		
		assertTrue(new Expression("2", config).resolve().asBoolean());
		assertTrue(new Expression("-2", config).resolve().asBoolean());
		assertFalse(new Expression("0", config).resolve().asBoolean());
		
		config.booleanBehavor = Config.BooleanBehavor.NON_EMPTY_STRINGS;
		
		assertTrue(new Expression("'blub'", config).resolve().asBoolean());
		assertFalse(new Expression("''", config).resolve().asBoolean());
		
		try
		{
			new Expression("null", config).resolve().asBoolean();
			fail("Fail");
		}
		catch(EvaluationException e) { handleException(e); }
		
		config.booleanBehavor = Config.BooleanBehavor.BOOLEAN_TO_NUMBER;
		
		assertEquals(1, new Expression("true", config).resolve().asLong());
		assertEquals(0, new Expression("false", config).resolve().asLong());
		
		config.nullBehavor = Config.NullBehavor.TO_FALSE;
		
		assertFalse(new Expression("null", config).resolve().asBoolean());
		
		System.out.println();
	}
	
	@Test
	public void testFunktions()
	{
		System.out.println("Variablen und Funktionen");
		blub = "pantsu";
		String str = (String) new Expression("'atashi no ' + blub()").evaluate();
		assertEquals("atashi no " + blub, str);
		
		Double d = (Double) new Expression("sin(PI)").evaluate();
		assertTrue(Math.abs(d) < 0.0000001);
		
		System.out.println();
	}
	
	@Test
	public void testNull()
	{
		System.out.println("Ausdrücke mit NULL");
		Config config = new Config();
		config.debug = true;
		Object result;
		boolean b;
		
		result = new Expression("null").evaluate();
		assertNull(result);
		
		result = new Expression("2.5 + null").evaluate();
		assertTrue( ((Double) result).isNaN() );
		
		try
		{
			result = new Expression("1 + null").compile();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		try
		{
			result = new Expression("'test' + null").compile();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		config.nullBehavor = Config.NullBehavor.TO_EMPTY_STRING;
		
		result = new Expression("'test' + null", config).evaluate();
		assertEquals("test", result);
		
		//XXX: Ob das so Wünschenswert ist, ist fraglich
		b = new Expression("true | null").resolve().asBoolean();
		assertTrue(b);
		
		try
		{
			result = new Expression("null | true").compile();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		config.nullBehavor = Config.NullBehavor.TO_FALSE;
		
		b = new Expression("true & null", config).resolve().asBoolean();
		assertFalse(b);
		
		System.out.println();
	}
	
	@Test
	public void testObjects()
	{
		System.out.println("Objekte");
		Config config = new Config();
		config.debug = true;
		config.resolver = Expression.DEFAULT_CONFIG.resolver;
		Object result;
		
		boolean b = new Expression("obj != null").resolve().asBoolean();
		assertTrue(b);

		result = new Expression("obj").evaluate();
		assertEquals(obj, result);
		
		config.nullBehavor = Config.NullBehavor.TO_FALSE;
		
		b = new Expression("null | obj", config).resolve().asBoolean();
		assertTrue(b);
	}
	
	@Test
	public void testArrays()
	{
		System.out.println("Arrays");
		Config config = new Config();
		config.debug = true;
		config.resolver = Expression.DEFAULT_CONFIG.resolver;
		Object result;
		List<?> list;
		
		boolean b = new Expression("array[1][0] > 3", config).resolve().asBoolean();
		assertTrue(b);
		
		// Seit Version 0.2 ist es erlaubt, ein Array zu definieren.
		list = new Expression("[1, 2]", config).resolve().asList();
		assertEquals(1L, list.get(0));
		
		result = new Expression("'test'[2]", config).resolve().asString();
		assertEquals("s", result);
		
		try
		{
			result = new Expression("array[1, 0] > 3", config).compile();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		try
		{
			result = new Expression("[0, 1][3]", config).compile();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		long l = new Expression("[1, 2+4, 3][1]", config).resolve().asLong();
		assertEquals(6, l);
	}
	
	@Test
	public void testVariablenAssignment()
	{
		System.out.println("Assignment");
		Config config = new Config();
		config.debug = true;
		config.useVariables = true;
		
		Object result;
		boolean b;
		
		try
		{
			result = new Expression("1 := 5", config).resolve();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		try
		{
			result = new Expression("abc", config).resolve();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		b = new Expression("abc := true", config).resolve().asBoolean();
		assertTrue(b);
		
		b = new Expression("abc", config).resolve().asBoolean();
		assertTrue(b);
		
		b = new Expression("xyz := abc", config).resolve().asBoolean();
		assertTrue(b);
		
		result = new Expression("abc := [[1 ,2, 3]]", config).resolve().asObject();
		assertEquals(2L, ((List<List<?>>) result).get(0).get(1));
		
		b = new Expression("abc[0][1] := xyz", config).resolve().asBoolean();
		
		b = new Expression("abc[0][1]", config).resolve().asBoolean();
		assertTrue(b);
	}
	
	@Test
	public void testInvocation()
	{
		System.out.println("Invocation");
		Config config = new Config();
		config.debug = true;
		config.useVariables = true;
		Object result;
		
		try
		{
			result = new Expression("'abc'.length()", config).resolve();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		config.invocator = new DefaultInvoker();
		
		long l = new Expression("'abc'.length()", config).resolve().asLong();
		assertEquals(3, l);
		
		String str = new Expression("'abc'.charAt(0)", config).resolve().asString();
		assertEquals("a", str);
		
		// 'c' entspricht der Zahl 99 im ascii-code.
		l = new Expression("'abc'.indexOf(99)", config).resolve().asLong();
		assertEquals(2, l);
		
		l = new Expression("[1, 2, 3].size()", config).resolve().asLong();
		assertEquals(3, l);
		
		l = new Expression("'abc'.substring(1).substring(1).length()", config).resolve().asLong();
		assertEquals(1, l);
		
		try
		{
			result = new Expression("'abc'.5", config).compile();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		try
		{
			result = new Expression("'abc'.indefOf()", config).resolve();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		config.assign("pantsu", new Pantsu("shiro", "aoi"));
		
		result = new Expression("pantsu.colors()[1]", config).resolve().asString();
		assertEquals("aoi", result);
		
		try
		{
			result = new Expression("pantsu.colors", config).resolve();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		result = new Expression("pantsu.SHIRO_PANTSU.colors()", config).resolve().asList().get(0);
		assertEquals("shiro", result);
		
		// Zur Implementierung fürs Setzen von Feldern Siehe tws.expression.Symbol.java
		try
		{
			result = new Expression("pantsu.SHIRO_PANTSU := null", config).resolve();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
	}
	
	// Klasse für die Invoke-Testreihe
	public static class Pantsu
	{
		public static Pantsu SHIRO_PANTSU = new Pantsu("shiro");
		private String[] colors;
		
		public Pantsu(String... colors)
		{
			this.colors = colors;
		}

		public String[] colors()
		{
			return colors;
		}
	}
	
	private void handleException(EvaluationException e)
	{
		System.out.println();
		System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
	}
}
