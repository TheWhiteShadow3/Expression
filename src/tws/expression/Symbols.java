package tws.expression;

import java.lang.reflect.Array;
import java.text.Collator;
import java.util.List;
import java.util.Map;


class Symbols
{
	static Argument resolveTwoArgs(OperationNode op, INode left, INode right)
	{
		switch(op.getOperator())
		{
			case ADD: 			return add(op, left.getArgument(), right.getArgument());
			case SUB: 			return sub(op, left.getArgument(), right.getArgument());
			case MUL: 			return mul(op, left.getArgument(), right.getArgument());
			case DIV: 			return div(op, left.getArgument(), right.getArgument());
			case MOD: 			return mod(op, left.getArgument(), right.getArgument());
			
			case SHIFT_LEFT: 	return shiftLeft(op, left.getArgument(), right.getArgument());
			case SHIFT_RIGHT: 	return shiftRight(op, left.getArgument(), right.getArgument());
			case USHIFT_RIGHT: 	return ushiftRight(op, left.getArgument(), right.getArgument());	
			case EQUAL: 		return equal(op, left.getArgument(), right.getArgument());
			case NOT_EQUAL: 	return notEqual(op, left.getArgument(), right.getArgument());
			case LESS: 			return less(op, left.getArgument(), right.getArgument());
			case LESS_EQUAL: 	return lessEqual(op, left.getArgument(), right.getArgument());
			case GREATER: 		return greater(op, left.getArgument(), right.getArgument());
			case GREATER_EQUAL: return greaterEqual(op, left.getArgument(), right.getArgument());
			
			case AND: 			return and(op, left, right);
			case OR: 			return or(op, left, right);
			case EXOR: 			return exor(op, left, right);
			
			case DOT:			return dot(left, right);
			case ASSIGN:		return assign(left, right);
			case INDEX:			return index(left, right);
			
			default:
				throw new EvaluationException(op, "Invalid Operation: " + op.getOperator());
		}
	}

	static Argument resolveOneArg(OperationNode op, INode node)
	{
		switch(op.getOperator())
		{
			case NEG: return neg(op, node.getArgument());
			case NOT: return not(op, node.getArgument());
			
			default:
				throw new EvaluationException(op, "Invalid Operation: " + op.getOperator());
		}
	}

	private static Argument add(INode node, Argument left, Argument right)
	{
		if (left.getType() == String.class || right.getType() == String.class ||
			left.getType() == Character.class || right.getType() == Character.class)
		{
			String ls = left.asString();
			String rs = right.asString();
			return new StringArgument(node, ls + rs);
		}
		
		if (left.getType() == double.class || right.getType() == double.class)
		{
			double ld = left.asDouble();
			double rd = right.asDouble();
			return new FloatArgument(node, ld + rd);
		}
		
		if (left.isNumber() && right.isNumber())
		{
			long li = left.asLong();
			long ri = right.asLong();
			return new IntegerArgument(node, li + ri);
		}
		
		if (left.isObject() && node.getExpression().getConfig().useOperationOverloading)
		{
			return invoke(node, "add", left, right);
		}
		
		throw new EvaluationException(node, "Invalid Type for Operation: ADD");
	}

	private static Argument sub(INode node, Argument left, Argument right)
	{
		if (left.getType() == double.class || right.getType() == double.class)
		{
			double ld = left.asDouble();
			double rd = right.asDouble();
			return new FloatArgument(node, ld - rd);
		}
		
		if (left.isNumber() && right.isNumber())
		{
			long li = left.asLong();
			long ri = right.asLong();
			return new IntegerArgument(node, li - ri);
		}
		
		if (left.isObject() && node.getExpression().getConfig().useOperationOverloading)
		{
			return invoke(node, "sub", left, right);
		}
		
		throw new EvaluationException(node, "Invalid Type for Operation: SUB");
	}
	
	private static Argument mul(INode node, Argument left, Argument right)
	{
		if (left.getType() == double.class || right.getType() == double.class)
		{
			double ld = left.asDouble();
			double rd = right.asDouble();
//			System.out.println("Float MUL: " + ld + " * " + rd);
			return new FloatArgument(node, ld * rd);
		}
		
		if (left.isNumber() && right.isNumber())
		{
			long li = left.asLong();
			long ri = right.asLong();
//			System.out.println("Integer MUL: " + li + " * " + ri);
			return new IntegerArgument(node, li * ri);
		}
		
		if (left.isObject() && node.getExpression().getConfig().useOperationOverloading)
		{
			return invoke(node, "mul", left, right);
		}
		
		throw new EvaluationException(node, "Invalid Type for Operation: MUL");
	}
	
	private static Argument div(INode node, Argument left, Argument right)
	{
		if (left.getType() == double.class || right.getType() == double.class)
		{
			double ld = left.asDouble();
			double rd = right.asDouble();
//			System.out.println("Float DIV: " + ld + " / " + rd);
			return new FloatArgument(node, ld / rd);
		}
		
		if (left.isNumber() && right.isNumber())
		{
			long li = left.asLong();
			long ri = right.asLong();
//			System.out.println("Integer DIV: " + li + " / " + ri);
			return new IntegerArgument(node, li / ri);
		}
		
		if (left.isObject() && node.getExpression().getConfig().useOperationOverloading)
		{
			return invoke(node, "div", left, right);
		}
		
		throw new EvaluationException(node, "Invalid Type for Operation: DIV");
	}

	private static Argument mod(INode node, Argument left, Argument right)
	{
		if (left.getType() == double.class || right.getType() == double.class)
		{
			double ld = left.asDouble();
			double rd = right.asDouble();
			return new FloatArgument(node, ld % rd);
		}
		
		if (left.isNumber() && right.isNumber())
		{
			long li = left.asLong();
			long ri = right.asLong();
			return new IntegerArgument(node, li % ri);
		}
		
		if (left.isObject() && node.getExpression().getConfig().useOperationOverloading)
		{
			return invoke(node, "mod", left, right);
		}
		
		throw new EvaluationException(node, "Invalid Type for Operation: DIV");
	}

	private static Argument shiftLeft(INode node, Argument left, Argument right)
	{
		if (left.isNumber() && right.isNumber())
		{
			long li = left.asLong();
			long ri = right.asLong();
			return new IntegerArgument(node, li << ri);
		}
		
		throw new EvaluationException(node, "Invalid Type for Operation: SHIFT_LEFT");
	}

	private static Argument shiftRight(INode node, Argument left, Argument right)
	{
		if (left.isNumber() && right.isNumber())
		{
			long li = left.asLong();
			long ri = right.asLong();
			return new IntegerArgument(node, li >> ri);
		}
		
		throw new EvaluationException(node, "Invalid Type for Operation: SHIFT_RIGHT");
	}
	
	private static Argument ushiftRight(INode node, Argument left, Argument right)
	{
		if (left.isNumber() && right.isNumber())
		{
			long li = left.asLong();
			long ri = right.asLong();
			return new IntegerArgument(node, li >>> ri);
		}
		
		throw new EvaluationException(node, "Invalid Type for Operation: USHIFT_RIGHT");
	}
	
	private static Argument equal(INode node, Argument left, Argument right)
	{
		return new BooleanArgument(node, compareBoolean(node, left, right));
	}
	
	private static Argument notEqual(INode node, Argument left, Argument right)
	{
		return new BooleanArgument(node, !compareBoolean(node, left, right));
	}

	private static Argument less(INode node, Argument left, Argument right)
	{
		return new BooleanArgument(node, compareNumeric(node, left, right) < 0);
	}

	private static Argument lessEqual(INode node, Argument left, Argument right)
	{
		return new BooleanArgument(node, compareNumeric(node, left, right) <= 0);
	}

	private static Argument greater(INode node, Argument left, Argument right)
	{
		return new BooleanArgument(node, compareNumeric(node, left, right) > 0);
	}

	private static Argument greaterEqual(INode node, Argument left, Argument right)
	{
		return new BooleanArgument(node, compareNumeric(node, left, right) >= 0);
	}
	
	private static boolean compareBoolean(INode node, Argument left, Argument right)
	{
		if (left.isNull()) return right.isNull();
		
		if (left.isBoolean())
		{
			return right.isBoolean() && left.asBoolean() == right.asBoolean();
		}
		
		if (left.isString() || left.isObject())
		{
			return (right.isString() || right.isObject()) && left.asObject().equals(right.asObject());
		}
		
		if (left.isObject() && node.getExpression().getConfig().useOperationOverloading)
		{
			return invoke(node, "equal", left, right).asBoolean();
		}
		
		return compareNumeric(node, left, right) == 0;
	}
	
	static int compareNumeric(INode node, Argument left, Argument right)
	{
		if (left.getType() == double.class || right.getType() == double.class)
		{
			double ld = left.asDouble();
			double rd = right.asDouble();
			if (ld > rd) return 1;
			if (ld < rd) return -1;
			return 0;
		}
		
		if (left.isNumber() && right.isNumber())
		{
			long li = left.asLong();
			long ri = right.asLong();
			if (li > ri) return 1;
			if (li < ri) return -1;
			return 0;
		}
		
		if (left.isString() && right.isString())
		{
			String ls = left.asString();
			String rs = right.asString();
			
			Collator collator = node.getExpression().getConfig().stringCollator;
			return collator.compare(ls, rs);
		}
		
		if (Comparable.class.isAssignableFrom(left.getType()))
		{
			return ((Comparable) left.asObject()).compareTo(right.asObject());
		}
		
		throw new EvaluationException(node, "Invalid Type for relational Operation.");
	}

	private static Argument and(INode node, INode left, INode right)
	{
		return new BooleanArgument(node, left.getArgument().asBoolean() && right.getArgument().asBoolean());
	}

	private static Argument or(INode node, INode left, INode right)
	{
		return new BooleanArgument(node, left.getArgument().asBoolean() || right.getArgument().asBoolean());
	}

	private static Argument exor(INode node, INode left, INode right)
	{
		return new BooleanArgument(node, left.getArgument().asBoolean() ^ right.getArgument().asBoolean());
	}
	
	private static Argument neg(INode node, Argument arg)
	{
		if (arg.getType() == double.class)
		{
			return new FloatArgument(node, -arg.asDouble());
		}
		
		if (arg.isNumber())
		{
			return new IntegerArgument(node, -arg.asLong());
		}

		if (arg.isObject() && node.getExpression().getConfig().useOperationOverloading)
		{
			return invoke(node, "neg", arg, null);
		}
		
		throw new EvaluationException("Invalid Type for Operation: NEG");
	}
	
	private static Argument not(INode node, Argument arg)
	{
		return new BooleanArgument(node, !arg.asBoolean());
	}

	private static Argument assign(INode left, INode right)
	{
		Argument arg = right.getArgument();
		Object value = Config.unwrap(arg);

		if (left instanceof Identifier)
		{
			String var =  ((Identifier) left).getName();
			left.getExpression().getConfig().assign(var, value);
			return arg;
		}
		else if (left instanceof InfixOperation)
		{
			InfixOperation op = (InfixOperation) left;
			INode[] nodes = left.getChildren();

			if (op.getSymbol().getOperator() == Operator.INDEX)
			{
				Argument obj = nodes[0].getArgument();
				Object collection = obj.asObject();
				
				if (collection.getClass().isArray())
				{
					int index = getIndex(nodes[1], Array.getLength(collection));
					Class<?> cls = collection.getClass().getComponentType();
					
					if (cls == int.class)
						value = (int) right.getArgument().asLong();
					else if (cls == float.class)
						value = (float) right.getArgument().asDouble();
					else if (cls == char.class && right.getArgument().asString().length() == 1)
					{
						String str = right.getArgument().asString();
						if (str.length() != 1)
							throw new EvaluationException(right, "Can not cast string with length other then one into char.");
						value = str.charAt(0);
					}
					
					Array.set(collection, index, value);
				}
				else if (collection instanceof List)
				{
					List list = (List) collection;
					int index = getIndex(nodes[1], list.size());
					list.set(index, value);
				}
				else if (collection instanceof Map)
				{
					Object key = nodes[1].getArgument().asObject();
					((Map) collection).put(key, value);
				}
				else
					throw new EvaluationException(left, "Invalid list type " + collection.getClass().getName() + ".");
				
				return arg;
			}
			else if (op.getSymbol().getOperator() == Operator.DOT)
			{
//				((FunctionCall) nodes[1]).setArguments(new INode[] {right});
				dot(nodes[0], nodes[1]);
				return arg;
			}
		}
		throw new EvaluationException(left, "Invalid Type for Operation: ASSIGN");
	}

	private static Argument index(INode left, INode right)
	{
		try
		{
			Object collection = left.getArgument().asObject();
			Object result;
			if (collection.getClass().isArray())
			{
				int index = getIndex(right, Array.getLength(collection));
				result = Array.get(collection, index);
			}
			else if (collection instanceof List)
			{
				List list = (List) collection;
				int index = getIndex(right, list.size());
				result = list.get(index);
			}
			else if (collection instanceof Map)
			{
				Object key = right.getArgument().asObject();
				result = ((Map) collection).get(key);
			}
			else if (collection instanceof String)
			{
				String str = (String) collection;
				int index = getIndex(right, str.length());
				return new StringArgument(left, str.charAt(index));
			}
			else
				throw new EvaluationException(left, "Invalid list type " + collection.getClass().getName() + ".");
			
			return Config.wrap(left, result, false);
		}
		catch(IndexOutOfBoundsException e)
		{
			throw new EvaluationException(right, "Invalid Index.", e);
		}
	}
	
	private static int getIndex(INode indexNode, int arraySize)
	{
		int index = (int) indexNode.getArgument().asLong();
		if (index < 0)
			index = arraySize - index;
		return index;
	}
	
	private static Argument dot(INode recieverNode, INode call)
	{
		Argument reciever = recieverNode.getArgument();
		Object callObject = (call instanceof FunctionCall) ? ((FunctionCall) call).getTarget() : call;
		if (!(callObject instanceof Identifier))
			throw new EvaluationException(call, "Can not invoke " + call);
		
		Identifier id = (Identifier) callObject;
		String name = id.getName();
		Argument[] args = null;
		if (call instanceof FunctionCall)
		{
			args = ((FunctionCall) call).resolveArguments();
		}
		try
		{
			Config config = recieverNode.getExpression().getConfig();
			Object result = config.invoke(reciever, name, args);
			if (result instanceof Operation)
				return ((Operation) result).resolve();
			else
				return Config.wrap(recieverNode, result, false);
		}
		catch (NoSuchFieldException e)
		{
			throw new EvaluationException(call,
					"Field '" + name + "' not found for " + reciever.getType().getName(), e);
		}
		catch (NoSuchMethodException e)
		{
			StringBuilder builder = new StringBuilder();
			builder.append("Method ").append(name);
			builder.append('(');
			if (args != null)
			{
				for (int i = 0; i < args.length; i++)
				{
					if (i > 0) builder.append(", ");
					builder.append(args[i].getType());
				}
			}
			builder.append(')');
			builder.append(" not found for ").append(reciever.getType().getName());
			
			throw new EvaluationException(call, builder.toString(), e);
		}
		catch (Exception e)
		{
			throw new EvaluationException(call, "Can not invoke " + name + " on " + reciever, e);
		}
	}
	
	private static Argument invoke(INode node, String method, Argument reciever, Argument arg)
	{
		try
		{
			Argument[] args = (arg != null) ? new Argument[] {arg} : new Argument[0];
			Config config = node.getExpression().getConfig();
			Object result = config.invoke(reciever, method, args);
			if (result instanceof Operation)
				return ((Operation) result).resolve();
			else
				return Config.wrap(node, result, false);
		}
		catch (NoSuchMethodException e)
		{
			StringBuilder builder = new StringBuilder();
			builder.append("Method ").append(method);
			builder.append('(');
			if (arg != null) builder.append(arg.getType());
			builder.append(')');
			builder.append(" not found for ").append(reciever.getType().getName());
			
			throw new EvaluationException(node, builder.toString(), e);
		}
		catch (Exception e)
		{
			throw new EvaluationException(node, "Can not invoke " + method + " on " + reciever, e);
		}
	}
}
