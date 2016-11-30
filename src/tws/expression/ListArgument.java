package tws.expression;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class ListArgument<T> extends Node implements Argument
{
	private Object seq;
	
	ListArgument(Expression exp, int sourcePos, Node[] nodes)
	{
		super(exp, sourcePos);
		this.seq = nodes;
	}
	
	ListArgument(Node parent, Node[] nodes)
	{
		super(parent);
		this.seq = nodes;
	}
	
	ListArgument(Node parent, Object seq)
	{
		super(parent);
		this.seq = seq;
	}
	
	@Override
	public Node[] getChildren()
	{
		if (seq instanceof Node[])
			return (Node[]) seq;
		else
			return super.getChildren();
	}
	
	@Override
	public Argument getArgument()
	{
		return this;
	}
	
	@Override
	public Class<?> getType() { return List.class; }
	@Override
	public boolean isNumber() { return false; }
	@Override
	public boolean isString() { return false; }
	@Override
	public boolean isNull() { return false; }
	@Override
	public boolean isBoolean() { return false; }
	@Override
	public boolean isObject() { return true; }

	@Override
	public String asString()
	{
		return asList().toString();
	}

	@Override
	public boolean asBoolean()
	{
		return getExpression().getConfig().isTrue(this);
	}

	@Override
	public double asDouble()
	{
		return Double.NaN;
	}

	@Override
	public long asLong()
	{
		throw new EvaluationException("Can not cast array to long.");
	}

	@Override
	public List<T> asObject()
	{
		return asList();
	}

	@Override
	public List<T> asList()
	{
		if (seq instanceof List)
			return (List<T>) seq;
		else
		{
			Object[] wrapper = new Object[size()];
			for(int i = 0; i < wrapper.length; i++)
			{
				Object obj = Array.get(seq, i);
				if (obj instanceof Node)
					obj = ((Node) obj).getArgument().asObject();
				wrapper[i] = obj;
			}
			return (List<T>) Arrays.asList(wrapper);
		}
	}
	
	public int size()
	{
		if (seq instanceof List)
			return ((List) seq).size();
		else
			return Array.getLength(seq);
	}
	
	public T get(int index)
	{
		if (seq instanceof List)
			return (T) ((List) seq).get(index);
		else
			return (T) Array.get(seq, index);
	}
	
	public void set(int index, T value)
	{
		if (seq instanceof List)
			((List) seq).set(index, value);
		else
			Array.set(seq, index, value);
	}
	
	public void set(int index, Argument arg)
	{
		if (seq instanceof List)
		{
			((List) seq).set(index, arg.asObject());
		}
		else if (seq.getClass().isArray())
		{
			Class<?> cls = seq.getClass().getComponentType();
			
			Object value;
			if (cls == int.class)
				value = (int) arg.asLong();
			else if (cls == float.class)
				value = (float) arg.asDouble();
			else if (cls == char.class && arg.asString().length() == 1)
			{
				String str = arg.asString();
				if (str.length() != 1)
					throw new IllegalArgumentException("Can not cast string with length other then one into char.");
				value = str.charAt(0);
			}
			else
				value = arg.asObject();
			
			Array.set(seq, index, value);
		}
		else throw new EvaluationException(this, "Invalid reference type " + seq.getClass().getName());
	}
	
	@Override
	public String toString()
	{
		return asString();
	}
}
