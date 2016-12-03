package tws.expression;

import java.util.ArrayList;
import java.util.List;


/**
 * Stellt einen String da.
 * @author TheWhiteShadow
 */
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

	@Override
	public Class<?> getType() { return String.class; }
	@Override
	public boolean isNumber() { return false; }
	@Override
	public boolean isString() { return true; }
	@Override
	public boolean isNull() { return false; }
	@Override
	public boolean isBoolean() { return false; }
	@Override
	public boolean isObject() { return false; }
	
	@Override
	public boolean asBoolean()
	{
		return getExpression().getConfig().isTrue(this);
	}

	@Override
	public String asString()
	{
		return value;
	}

	@Override
	public double asDouble()
	{
		return Double.parseDouble(value);
	}

	@Override
	public long asLong()
	{
		return Long.parseLong(value);
	}

	@Override
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
	
	@Override
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
