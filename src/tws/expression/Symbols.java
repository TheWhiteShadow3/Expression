package tws.expression;

import java.text.Collator;
import java.util.List;


class Symbols
{
	static Node resolveTwoArgs(OperationNode op, Node left, Node right)
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
			
			case AND: 			return and(op, left.getArgument(), right.getArgument());
			case OR: 			return or(op, left.getArgument(), right.getArgument());
			case EXOR: 			return exor(op, left.getArgument(), right.getArgument());
			
			case DOT:			return dot(left, right);
			case ASSIGN:		return assign(left, right.getArgument());
			case INDEX:			return index(left, right);
			
			default:
				throw new EvaluationException(op, "Invalid Operation: " + op.getOperator());
		}
	}

	static Node resolveOneArg(OperationNode op, Node node)
	{
		switch(op.getOperator())
		{
			case NEG: return neg(op, node.getArgument());
			case NOT: return not(op, node.getArgument());
			
			default:
				throw new EvaluationException(op, "Invalid Operation: " + op.getOperator());
		}
	}

	private static Node add(Node node, Argument left, Argument right)
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
		
		throw new EvaluationException(node, "Invalid Type for Operation: ADD");
	}

	private static Node sub(Node node, Argument left, Argument right)
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
		
		throw new EvaluationException(node, "Invalid Type for Operation: SUB");
	}
	
	private static Node mul(Node node, Argument left, Argument right)
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
		
		throw new EvaluationException(node, "Invalid Type for Operation: MUL");
	}
	
	private static Node div(Node node, Argument left, Argument right)
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
		
		throw new EvaluationException(node, "Invalid Type for Operation: DIV");
	}

	private static Node mod(Node node, Argument left, Argument right)
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
		
		throw new EvaluationException(node, "Invalid Type for Operation: DIV");
	}

	private static Node shiftLeft(Node node, Argument left, Argument right)
	{
		if (left.isNumber() && right.isNumber())
		{
			long li = left.asLong();
			long ri = right.asLong();
			return new IntegerArgument(node, li << ri);
		}
		
		throw new EvaluationException(node, "Invalid Type for Operation: SHIFT_LEFT");
	}

	private static Node shiftRight(Node node, Argument left, Argument right)
	{
		if (left.isNumber() && right.isNumber())
		{
			long li = left.asLong();
			long ri = right.asLong();
			return new IntegerArgument(node, li >> ri);
		}
		
		throw new EvaluationException(node, "Invalid Type for Operation: SHIFT_RIGHT");
	}
	
	private static Node ushiftRight(Node node, Argument left, Argument right)
	{
		if (left.isNumber() && right.isNumber())
		{
			long li = left.asLong();
			long ri = right.asLong();
			return new IntegerArgument(node, li >>> ri);
		}
		
		throw new EvaluationException(node, "Invalid Type for Operation: USHIFT_RIGHT");
	}
	
	private static Node equal(Node node, Argument left, Argument right)
	{
		return new BooleanArgument(node, compareBoolean(node, left, right));
	}
	
	private static Node notEqual(Node node, Argument left, Argument right)
	{
		return new BooleanArgument(node, !compareBoolean(node, left, right));
	}

	private static Node less(Node node, Argument left, Argument right)
	{
		return new BooleanArgument(node, compareNumeric(node, left, right) < 0);
	}

	private static Node lessEqual(Node node, Argument left, Argument right)
	{
		return new BooleanArgument(node, compareNumeric(node, left, right) <= 0);
	}

	private static Node greater(Node node, Argument left, Argument right)
	{
		return new BooleanArgument(node, compareNumeric(node, left, right) > 0);
	}

	private static Node greaterEqual(Node node, Argument left, Argument right)
	{
		return new BooleanArgument(node, compareNumeric(node, left, right) >= 0);
	}
	
	private static boolean compareBoolean(Node node, Argument left, Argument right)
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
		
		return compareNumeric(node, left, right) == 0;
	}
	
	private static int compareNumeric(Node node, Argument left, Argument right)
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
		
		throw new EvaluationException(node, "Invalid Type for relational Operation.");
	}

	private static Node and(Node node, Argument left, Argument right)
	{
		return new BooleanArgument(node, left.asBoolean() && right.asBoolean());
	}

	private static Node or(Node node, Argument left, Argument right)
	{
		return new BooleanArgument(node, left.asBoolean() || right.asBoolean());
	}

	private static Node exor(Node node, Argument left, Argument right)
	{
		return new BooleanArgument(node, left.asBoolean() ^ right.asBoolean());
	}
	
	private static Node neg(Node node, Argument arg)
	{
		if (arg.getType() == double.class)
		{
			return new FloatArgument(node, -arg.asDouble());
		}
		
		if (arg.isNumber())
		{
			return new IntegerArgument(node, -arg.asLong());
		}
		
		throw new EvaluationException("Invalid Type for Operation: NEG");
	}
	
	private static Node not(Node node, Argument arg)
	{
		return new BooleanArgument(node, !arg.asBoolean());
	}

	private static Node assign(Node left, Argument arg)
	{
		if (left instanceof Reference)
		{
			String var = ((Reference) left).getName();

			left.getExpression().getConfig().assign(var, arg);
			return (Node) arg;
		}
		else if (left instanceof InfixOperation)
		{
			InfixOperation op = (InfixOperation) left;
			Node[] nodes = left.getChildren();
			Object reciever = nodes[0].getArgument().asObject();
			
			if (op.getSymbol().getOperator() == Operator.INDEX)
			{
				int index = (int) nodes[1].getArgument().asLong();
				assignToArray(left, reciever, index, arg);
				return (Node) arg;
			}
			// TODO: Das Setzen von Feldern ist noch nicht implementiert. Würde aber an dieser Stelle stehen.
//			else if (op.getSymbol().getOperator() == Operator.DOT)
//			{
//				Reference ref = (Reference) nodes[1];
//				assignToField(left, reciever, ref.getName(), ref.getArgument());
//				return (Node) arg;
//			}
		}
		throw new EvaluationException(left, "Invalid Type for Operation: ASSIGN");
	}
	
	private static void assignToArray(Node node, Object array, int index, Argument arg)
	{
		if (array.getClass().isArray())
		{
			java.lang.reflect.Array.set(array, index, arg.asObject());
		}
		else if (array instanceof List)
		{
			((List) array).set(index, arg.asObject());
		}
		else throw new EvaluationException(node, "Invalid reference type " + array.getClass().getName());
	}

	private static Node index(Node left, Node right)
	{
		int index = (int) right.getArgument().asLong();
		try
		{
			if (left instanceof Reference)
			{
				return (Node) Config.wrap(left, ((Reference) left).resolve().asList().get(index));
			}
			else if (left instanceof Array)
			{
				return ((Array) left).getChildren()[index];
			}
			else
			{
				List<?> list = left.getArgument().asList();
				return (Node) Config.wrap(left, list.get(index));
			}
		}
		catch(IndexOutOfBoundsException e)
		{
			throw new EvaluationException(right, "Invalid Index.", e);
		}
	}
	
	private static Node dot(Node recieverNode, Node call)
	{
		Argument reciever = recieverNode.getArgument();
		if (!(call instanceof Reference))
			throw new EvaluationException(call, "Can not invoke " + call);
		
		Reference ref = (Reference) call;
		String name = ref.getName();
		Argument[] args = ref.resolveArguments();
		try
		{
			Config config = recieverNode.getExpression().getConfig();
			Object result = config.internalResolver.invoke(reciever, name, args);
			return (Node) Config.wrap(recieverNode, result);
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
			for (int i = 0; i < args.length; i++)
			{
				if (i > 0) builder.append(", ");
				builder.append(args[i].getType());
			}
			builder.append(')');
			builder.append(" not found for ").append(reciever.getType().getName());
			
			throw new EvaluationException(call, builder.toString(), e);
		}
		catch (Exception e)
		{
			throw new EvaluationException(call, "Can not invoke " + name, e);
		}
	}
}
