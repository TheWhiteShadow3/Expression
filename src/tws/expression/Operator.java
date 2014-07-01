package tws.expression;

/**
 * Ein Operator bestimmt den Typ der Operation auf ein oder mehrere Daten.
 * @author TheWhiteShadow
 */
public enum Operator
{
	INDEX(11),
	DOT(11),
	
	NOT(10),
	NEG(10),
	
	MOD(9),
	MUL(9),
	DIV(9),
	ADD(8),
	SUB(8),
	
	SHIFT_LEFT(7),
	SHIFT_RIGHT(7),
	USHIFT_RIGHT(7),
	
	GREATER(6),
	LESS(6),
	GREATER_EQUAL(6),
	LESS_EQUAL(6),
	
	EQUAL(5),
	NOT_EQUAL(5),
	
	AND(4),
	OR(3),
	EXOR(2),
	
	ASSIGN(1),
	COMMA(0);
	
	public final int priority;
	
	Operator(int priority)
	{
		this.priority = priority;
	}
}
