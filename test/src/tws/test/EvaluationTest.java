package tws.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
	
	/** Wert den der Resolver bei 'list' auflösen soll. */
	static List<String> list = new ArrayList<String>();
	
	/** Wert den der Resolver bei 'carray' auflösen soll. */
	static char[] carray = "Rose".toCharArray();
	
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
				if ("list".equals(refName)) return list;
				if ("carray".equals(refName)) return carray;
				
				throw new EvaluationException("Resolver error! Can not resolve '" + refName + "'.");
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
		
		d = new Expression("2E-16").resolve().asDouble();
		assertEquals(2E-16, d, 0);
		
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
		
		try
		{
			Operation op = new Expression(".2").compile();
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
		
		b = (Boolean) new Expression("true | true & false").evaluate();
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
		
		i = (Long) new Expression("2 * (1 + 3 )").evaluate();
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
		
		try
		{
			Operation op = new Expression("]").compile();
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
		// Wenn ein Wrapper benutzt wird, existiert keine echte Operation mehr im Ausdruck, da dieser vollständig aufgelöst wurde.
		assertTrue(op instanceof WrapperOperation);
		
		Config config = new Config();
		config.useVariables = true;
		int[] arr = new int[] {1, 2};
		config.assign("arr", arr);
		
		op = new Expression("arr", config).compile();
		arr[1] = 4;
		Argument a = op.resolve();
		arr[1] = 6;
		assertEquals(4L, a.asList().get(1)); 
		
		System.out.println();
	}
	
	@Test
	public void testBooleanBehavor()
	{
		System.out.println("Boolean Verhalten");
		Config config = new Config();
		config.debug = true;
		config.resolver = Expression.DEFAULT_CONFIG.resolver;
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
		
		config.booleanBehavor = Config.BooleanBehavor.IS_NOT_NULL;
		
		assertFalse(new Expression("null", config).resolve().asBoolean());
		assertTrue(new Expression("array", config).resolve().asBoolean());
		
		System.out.println();
	}
	
	@Test
	public void testFunktions()
	{
		System.out.println("Variablen und Funktionen");
		
		double d;
		d = new Expression("min(2, 7, 5, 1)").resolve().asDouble();
		assertEquals(1, d, 0.);
		
		d = new Expression("max([1, 2, 3])").resolve().asDouble();
		assertEquals(3, d, 0.);
		
		d = new Expression("sin(PI)").resolve().asDouble();
		assertEquals(0, d, 2E-16);
		
		blub = "pantsu";
		String str = (String) new Expression("'atashi no ' + blub()").evaluate();
		assertEquals("atashi no " + blub, str);
		
		Config config = new Config();
		config.debug = true;
		config.useVariables = true;
		
		int[] array = new int[] {6, 4, 1, 3};
		config.assign("l", array);
		
		List list = new Expression("asc(l)", config).resolve().asList();
		assertEquals(1L, list.get(0));
		assertEquals(6L, list.get(3));
		
		assertEquals(6, array[0]);
		assertEquals(3, array[3]);
		
		System.out.println();
	}
	
	@Test
	public void testNull()
	{
		System.out.println("Ausdrücke mit NULL");
		Config config = new Config();
		config.debug = true;
		Object result;
		long l;
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
		
		config.nullCast = Config.NullCast.TO_EMPTY_STRING;
		
		result = new Expression("'test' + null", config).evaluate();
		assertEquals("test", result);
		
		//Definitionsgemäß OK, da der Oder-Zweig nicht aufgelöst wird.
		b = new Expression("true | null").resolve().asBoolean();
		assertTrue(b);
		
		try
		{
			result = new Expression("null | true").compile();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		config.nullCast = Config.NullCast.TO_EMPTY_LIST;
		
		result = new Expression("null", config).resolve().asList();
		assertEquals(Collections.EMPTY_LIST, result);
		
		config.nullCast = Config.NullCast.TO_ZERO;
		
		l = new Expression("null", config).resolve().asLong();
		assertEquals(0L, l);
		
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
		
		list = new Expression("[]", config).resolve().asList();
		assertEquals(0, list.size());
		
		boolean b = new Expression("array[1][0] > 3", config).resolve().asBoolean();
		assertTrue(b);
		
		// Seit Version 0.2 ist es erlaubt, ein Array zu definieren.
		list = new Expression("[1, 2]", config).resolve().asList();
		assertEquals(1L, list.get(0));
		
		result = new Expression("'test'[2]", config).resolve().asString();
		assertEquals("s", result);
		
		list = new Expression("[1, 2+4, 3]", config).resolve().asList();
		assertEquals(6L, list.get(1));

		// Seit 0.4 gehen auf Maps.
		Map map = new HashMap<String, Object>();
		map.put("k", "v");
		config.useVariables = true;
		config.assign("map", map);
		
		result = new Expression("map['k']", config).evaluate();
		assertEquals("v", result);
		
		try
		{
			result = new Expression("array [0]", config).compile();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		try
		{
			result = new Expression("array[1, 0] > 3", config).compile();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		Operation op = new Expression("[0, 1][3]", config).compile();
		try
		{
			op.resolve();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }

	}
	
	@Test
	public void testVariablenAssignment()
	{
		System.out.println("Assignment");
		Config config = new Config();
		config.debug = true;
		config.resolver = Expression.DEFAULT_CONFIG.resolver;
		config.useVariables = true;
		
		Object result;
		List list;
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
		assertTrue(b);
		
		b = new Expression("abc[0][1]", config).resolve().asBoolean();
		assertTrue(b);
		
		// Seit 0.3 funktioniert die Zuweisung zu einem int-array.
		list = new Expression("array[0][0] := 7", config).resolve().asList();
		assertEquals(7L, list.get(0));
		assertEquals(7L, array[0][0]);
		
		list = new Expression("carray[0] := 'P'", config).resolve().asList();
		assertEquals("Pose", new String(carray));
	}
	
	@Test
	public void testInvocation()
	{
		System.out.println("Invocation");
		Config config = new Config();
		config.debug = true;
		config.useVariables = true;
		Object result;
		
		Operation op = new Expression("'abc'.length()", config).compile();
		try
		{
			result = op.resolve();
			fail("Fail: " + result);
		}
		catch(EvaluationException e) { handleException(e); }
		
		config.invoker = new DefaultInvoker();
		
		long l = op.resolve().asLong();
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
		
		// Auch statische Felder müssen von einem Objekt aus aufgerufen werden!
		result = new Expression("pantsu.SHIRO_PANTSU := pantsu", config).resolve().asObject();
		assertEquals(config.getVariables().get("pantsu"), result);
	}
	
	@Test
	public void testThreading() throws InterruptedException, ExecutionException
	{
		System.out.println("Threading");
		final Config config = new Config();
		config.debug = true;
		config.resolver = Expression.DEFAULT_CONFIG.resolver;
		config.invoker = new DefaultInvoker();
		
		// Ein paar Testausdrücke, die alle 5 zurück geben. (Zur leichteren Auswertung)
		final String[] expressions = new String[] {"3*5 - 10", "'125'[2]", "'shiro'.length()", "v := 5", "array[1][1]"};
		List<Future<Argument>> resultCache = new ArrayList<Future<Argument>>(25);
		
		ExecutorService service = Executors.newFixedThreadPool(4);
		for(int i = 0; i < 25; i++)
		{
			final int index = i % 5;
			resultCache.add(service.submit(new Callable<Argument>()
			{
				@Override
				public Argument call() throws Exception
				{
					return new Expression(expressions[index], config).resolve();
				}
			}));
		}
		
		for(int i = 0; i < 24; i++)
		{
			assertEquals(5L, resultCache.get(i).get().asLong());
		}
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
		if (e.getCause() != null)
			System.out.println(e.getCause());
	}
}
