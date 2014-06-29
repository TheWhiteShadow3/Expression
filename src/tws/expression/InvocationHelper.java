package tws.expression;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class InvocationHelper
{
	private InvocationHelper() {}
	
	public static Node resolve(Node left, Node call)
	{
		Argument reciever = left.getArgument();
		Class clazz = reciever.getType();
		
		if (!(call instanceof Reference))
			throw new EvaluationException(call, "Can not invoke " + call);
		
		Object result;
		Reference ref = (Reference) call;
		Argument[] args = ref.resolveArguments();
		if (args == null)
		{
			try
			{
				Field field = findField(clazz, ref.getName());
				result = field.get(reciever.asObject());
			}
			catch (NoSuchFieldException e)
			{
				throw new EvaluationException(call, "Field " + ref.getName() + " not found for " + clazz.getName(), e);
			}
			catch (Exception e)
			{
				throw new EvaluationException(call, "Can not invoke " + ref.getName(), e);
			}
		}
		else
		{
			Method method = findMethod(clazz, ref.getName(), args);
			if (method == null)
			{
				StringBuilder builder = new StringBuilder();
				builder.append("Method ").append(ref.getName());
				builder.append('(');
				for (int i = 0; i < args.length; i++)
				{
					if (i > 0) builder.append(", ");
					builder.append(args[i].toString());
				}
				builder.append(')');
				builder.append(" not found for ").append(clazz.getName());
				throw new EvaluationException(builder.toString());
			}
			
			try
			{
				if (left.getExpression().getConfig().debug)
					System.out.println("Invoke: " + method);
				result = callMethod(method, reciever, args);
			}
			catch (Exception e)
			{
				throw new EvaluationException(call, "Can not invoke " + ref.getName(), e);
			}
		}
		return (Node) Config.wrap(left, result);
	}
	
	private static Field findField(Class clazz, String name) throws SecurityException, NoSuchFieldException
	{
		return clazz.getField(name);
	}

	private static Method findMethod(Class clazz, String callName, Argument[] args)
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
//			if (args.length == 0) break;
		}
		
		return method;
	}

	private static Object callMethod(Method method, Argument reciever, Argument[] args)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		if (args.length > 0)
		{
			Class[] paramTypes = method.getParameterTypes();
			Object[] argArray = new Object[args.length];
			for (int i = 0; i < argArray.length; i++)
			{
				argArray[i] = convertArgument(paramTypes[i], args[i]);
			}
			return method.invoke(reciever.asObject(), argArray);
		}
		else
		{
			return method.invoke(reciever.asObject());
		}
	}
	
	private static boolean canConvertArgument(Class targetType, Argument arg)
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
			if (targetType.isArray() && arg.getType() == List.class) return true;
			if (targetType.isAssignableFrom(arg.getType())) return true;
		}
		return false;
	}
	
	private static Object convertArgument(Class targetType, Argument arg)
	{
		if (targetType.isArray()) return arg.asList().toArray();
		
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
//		if (targetType == double.class || targetType == Double.class) return (Double.valueOf(arg.asDouble()));
		
		return arg.asObject();
	}
}
