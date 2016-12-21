package tws.expression;

public interface Invokable
{
	public Operation with(final Object... args);

	public Argument resolve();
}
