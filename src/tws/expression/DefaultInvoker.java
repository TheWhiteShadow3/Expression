package tws.expression;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Der Default-Invoker für das Auflösen von Feldern und Methoden in Ausdrücken.
 * <p>
 * Wenn in dem reciever der Methode {@link #invoke} ein Class-Objekt gekapselt ist, wird dieser als statisches Feld oder Methode behandelt.
 * </p>
 * @author TheWhiteShadow
 * @see Config#invoker
 */
public class DefaultInvoker implements Invoker
{
	@Override
	public Object invoke(Argument reciever, String name, Argument[] args) throws Exception
	{
		Class clazz = reciever.getType();
		if (clazz.isPrimitive())
			clazz = reciever.asObject().getClass();
		
		Object obj = ((Node) reciever).getObject();
		boolean debug = reciever instanceof Node && ((Node) reciever).getExpression().getConfig().debug;
		// Statischer Call
		if (obj instanceof Class)
		{
			clazz = (Class) obj;
			obj = null;
		}
		
		if (args == null)
		{
			Field field = findField(clazz, name);
			if (debug) System.out.println("Get: " + field);
			
			return field.get(obj);
		}
		else
		{
			if (args.length == 1) try
			{
				Field field = findField(clazz, name);
					
				if (debug) System.out.println("Set: " + field);
				
				field.set(obj, convertArgument(field.getType(), args[0]));
				return null;
			}
			catch(NoSuchFieldException e) {}
			
			Method method = findMethod(clazz, name, args);
			if (method == null)
				throw new NoSuchMethodException(name);
			
			if (debug) System.out.println("Invoke: " + method);
			
			return callMethod(method, obj, args);
		}
	}
	
	private Field findField(Class clazz, String name) throws NoSuchFieldException
	{
		return clazz.getField(name);
	}

	private Method findMethod(Class clazz, String callName, Argument[] args)
	{
		Method method = null;
		METHOD_LOOP:
		for (Method m : clazz.getMethods())
		{
			if (!m.getName().equals(callName)) continue;
			
			Class[] paramTypes = m.getParameterTypes();
			if (args.length != paramTypes.length) continue;
			
			for (int i = 0; i < args.length; i++)
			{
				if (!canConvertArgument(paramTypes[i], args[i])) continue METHOD_LOOP;
			}
			method = m;
			break;
		}
		
		return method;
	}

	private Object callMethod(Method method, Object obj, Argument[] args)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		try
		{
			if (args.length > 0)
			{
				Class[] paramTypes = method.getParameterTypes();
				Object[] argArray = new Object[args.length];
				for (int i = 0; i < argArray.length; i++)
				{
					argArray[i] = convertArgument(paramTypes[i], args[i]);
				}
			return method.invoke(obj, argArray);
		}
		else
		{
				return method.invoke(obj);
			}
		}
		catch(InvocationTargetException e)
		{
			if (e.getCause() instanceof EvaluationException)
				throw (EvaluationException) e.getCause();
			
			throw e;
		}
	}
	
	private boolean canConvertArgument(Class targetType, Argument arg)
	{
		if (targetType.isPrimitive())
		{
			if (targetType == boolean.class)
			{
				if (arg.isBoolean()) return true;
			}
			else
			{
				if (targetType == char.class)
				{
					if (arg.isString()) return true;
					if (arg.getType() == double.class) return false;
				}
				if (arg.isNumber()) return true;
				return false;
			}
		}
		else if (targetType.isInterface() && arg instanceof LambdaArgument)
		{
			return true;
		}
		else
		{
			if (targetType == Boolean.class) return canConvertArgument(boolean.class, arg);
			if (targetType == Byte.class) return canConvertArgument(byte.class, arg);
			if (targetType == Short.class) return canConvertArgument(short.class, arg);
			if (targetType == Character.class) return canConvertArgument(char.class, arg);
			if (targetType == Integer.class) return canConvertArgument(int.class, arg);
			if (targetType == Long.class) return canConvertArgument(long.class, arg);
			if (targetType == Float.class) return canConvertArgument(float.class, arg);
			if (targetType == Double.class) return canConvertArgument(double.class, arg);
			if (targetType == Object.class) return true;
			if (targetType.isArray())
			{
				if (arg.getType() == List.class) return true;
				return canConvertArgument(targetType.getComponentType(), arg);
			}
			if (targetType.isAssignableFrom(arg.getType())) return true;
		}
		return false;
	}
	
	private Object convertArgument(Class targetType, Argument arg)
	{
		if (targetType == byte.class || targetType == Byte.class) return (Byte.valueOf((byte) arg.asLong()));
		if (targetType == short.class || targetType == Short.class) return (Short.valueOf((short) arg.asLong()));
		if (targetType == int.class || targetType == Integer.class) return (Integer.valueOf((int) arg.asLong()));
		if (targetType == float.class || targetType == Float.class) return (Float.valueOf((float) arg.asDouble()));
		if (targetType == char.class || targetType == Character.class)
		{
			String str = arg.asString();
			if (str.length() != 1)
				throw new IllegalArgumentException("Can not cast string with length other then one into char.");
			return Character.valueOf(str.charAt(0));
		}
		if (targetType.isArray())
		{
			if (arg.getType() == List.class)
			{
				return arg.asList().toArray();
			}
			Object array = Array.newInstance(targetType.getComponentType(), 1);
			Array.set(array, 0, convertArgument(targetType.getComponentType(), arg));
			return array;
		}
		
		if (targetType.isInterface() && arg instanceof LambdaArgument)
		{
			final LambdaArgument lambda = (LambdaArgument) arg;
			Object proxy = Proxy.newProxyInstance(targetType.getClassLoader(), new Class[] {targetType}, new InvocationHandler()
			{
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
				{
					return convertArgument(method.getReturnType(), lambda.with(args).resolve());
				}
			});
			return proxy;
		}
		
		return arg.asObject();
	}
}
