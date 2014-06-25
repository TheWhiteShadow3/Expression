package tws.expression;

import java.util.ArrayList;
import java.util.List;


public class StringArgument extends Node implements Argument
{
	private String value;
	
	StringArgument(Expression exp, int sourcePos,  String value)
	{
		super(exp, sourcePos);
		if (value == null) throw new NullPointerException("string is null");
		this.value = value;
	}
	
	StringArgument(Node initiator, String value)
	{
		super(initiator);
		if (value == null) throw new NullPointerException("string is null");
		this.value = value;
	}

	StringArgument(Node initiator, char c)
	{
		super(initiator);
		this.value = String.valueOf(c);
	}

	public Class<?> getType() { return String.class; }
	public boolean isNumber() { return false; }
	public boolean isString() { return true; }
	public boolean isNull() { return false; }
	public boolean isBoolean() { return false; }
	public boolean isObject() { return false; }
	
	public boolean asBoolean()
	{
		return getExpression().getConfig().isTrue(this);
	}

	public String asString()
	{
		return value;
	}

	public double asDouble()
	{
		return Double.parseDouble(value);
	}

	public long asLong()
	{
		return Long.parseLong(value);
	}

	public String asObject()
	{
		return asString();
	}

	@Override
	public String toString()
	{
		return '"' + value + '"';
	}
	
	@Override
	public Argument getArgument()
	{
		return this;
	}
	
	public List<Character> asList()
	{
		List<Character> list = new ArrayList<Character>(value.length());
		for (int i = 0; i < value.length(); i++)
		{
			list.add(Character.valueOf(value.charAt(i)));
		}
		return list;
	}
}
