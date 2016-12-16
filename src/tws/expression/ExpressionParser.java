package tws.expression;

import java.util.ArrayList;
import java.util.List;

import tws.expression.LambdaArgument.LambdaReference;


public class ExpressionParser
{
	private String string;
	private int lenght;
	int start;
	private int pos;
	private Expression exp;
	private List<Node> args;
	
	public ExpressionParser() {}
	
	public Operation parse(Expression exp)
	{
		this.exp = exp;
		this.string = exp.getSourceString();
		this.lenght = string.length();
		this.start = 0;
		this.pos = 0;
		this.args = new ArrayList<Node>(12);
		
		if (exp.getConfig().debug) System.out.println('"' + string + '"');
		Node node = addNextNode();
		if (pos < lenght)
		{
			throwException("Trash after Expression.", null);
		}
		cleanup();
		
		return nodeToOperation(node);
	}
	
	private Operation nodeToOperation(Node node)
	{
		if (node instanceof Operation)
			return (Operation) node;
		else
			return new WrapperOperation(node.getArgument());
	}
	
	private void cleanup()
	{
		// Objekt-Referenzen aufräumen.
		this.exp = null;
		this.string = null;
		this.args = null;
	}
	
	private Node addNextNode()
	{
		int offset = args.size();
		
		int opCount = 1; // Zeigt an, dass als nächstes ein Operand gesucht wird.
//		char cBrace = 0;
		boolean isString = false;
		start = pos;
		
		char c;
		while(pos < lenght)
		{
			c = string.charAt(pos);
			
			if (c == '\'')
			{
				if (isString && pos+1 < lenght && string.charAt(pos+1) == '\'')
				{
					pos += 2;
					continue;
				}
				
				isString = !isString;
				if (!isString)
				{
					String str = string.substring(start+1, pos++).replaceAll("''", "'");
					args.add(new StringArgument(exp, start, str));
					opCount = 0;
					start = pos;
					continue;
				}
			}
			
			if (isString)
			{
				pos++;
				continue;
			}
			
			if (isWhiteSpace(c))
			{
				pos++;
				start = pos;
				continue;
			}
			
			if (c == '(')
			{
				pos++;
				Node node = args.isEmpty() ? null : args.get(args.size()-1);
				if (opCount == 0)
				{
					if (node instanceof Reference)
					{	// Funktion
						Reference ref = (Reference) node;
						int argStart = args.size();
						addListNodes(')');
						
						Node[] argNodes = new Node[args.size()- argStart];
						for(int i = args.size()-1; i >= argStart; i--)
						{
							argNodes[i - argStart] = args.get(i);
							args.remove(i);
						}
						ref.setArguments(argNodes);
					}
					else
						throw new EvaluationException(node, "Invalid identifier.");
				}
				else if (node instanceof LambdaArgument)
				{	// Lambda
					LambdaArgument lam = (LambdaArgument) node;
					int argStart = args.size();
					addListNodes(')');
					opCount = 1;
					
					String[] names = new String[args.size()- argStart];
					for(int i = args.size()-1; i >= argStart; i--)
					{
						node = args.get(i);
						if (!(node instanceof Reference))
							throwException("Invalid Argument.", null);
						
						Reference ref = (Reference) node;
						
						names[i - argStart] = ref.getName();
						args.remove(i);
					}
					lam.setParams(names);
				}
				else
				{	// Klammer-Gruppierung
					addNextNode();
					opCount = 0;
					if (lenght <= pos || string.charAt(pos++) != ')') throwException("Missing token ')'", null);
				}
			}
			else if (c == '[')
			{
				int braceStart = pos;
				pos++;
				if (opCount == 1)
				{	// Array-Definition
					int argStart = args.size();
					addListNodes(']');
					opCount = 0;
					Node[] argNodes = new Node[args.size()- argStart];
					for(int i = args.size()-1; i >= argStart; i--)
					{
						argNodes[i - argStart] = args.get(i);
						args.remove(i);
					}
					args.add(new Reference(exp, braceStart, argNodes));
				}
				else
				{	// Array-Index
					args.add(new OperationNode(exp, braceStart, Operator.INDEX));
					int argStart = args.size();
					addListNodes(']');
					if (args.size() != argStart+1)
						throw new EvaluationException(args.get(args.size()-1), "Invalid Index.");
				}
			}
			else if (c == '{')
			{	// Lambda
				LambdaArgument lam = new LambdaArgument(exp, pos);
				args.add(lam);
				pos++;
				int lStart = args.size();
				addNextNode();
				opCount = 0;
				
				if (lenght <= pos || string.charAt(pos++) != '}') throwException("Missing token '}'", null);

				String[] names = lam.getParams();
				List<LambdaReference> refs = new ArrayList<LambdaReference>();
				for(int i = lStart; i < args.size(); i++)
				{
					Node node = args.get(i);
					if (node instanceof Reference)
					{
						Reference ref = (Reference) node;
						for(int j = 0; j < names.length; j++)
						{
							if (names[j].equals(ref.getName()))
							{
								LambdaReference lRef = new LambdaReference(node, ref.getName(), j);
								args.set(i, lRef);
								refs.add(lRef);
								break;
							}
						}
					}
				}
				
				lam.setOperation(nodeToOperation(resolveStatement(lStart)), refs);
				args.remove(lStart);
			}
			else if (c == '}')
			{
				if (opCount > 0) throwException("Missing operand.", null);
				return null;
			}
			else if (c == ',' || c == ')' || c == '}' || c == ']')
			{
				Node last = args.isEmpty() ? null : args.get(args.size()-1);
				if (last instanceof Reference) return null;
				
				if (opCount > 0) throwException("Missing operand.", null);
				break;
			}
			else if (isIdentifier(c))
			{
				if (args.size() > 0 && opCount == 0) throwException("Operation expected.", null);
				
				do { pos++; }
				while( pos < lenght && isIdentifier(string.charAt(pos)) );
				
				if (c > 47 && c < 58) // Literal beginnt mit einer Zahl.
				{
					boolean isDouble = false;
					if (pos < lenght)
					{
						char c0 = string.charAt(pos-1);
						char c1 = string.charAt(pos);
						if (c1 == '.' || (c1 == '-' && (c0 == 'e' || c0 == 'E')))
						{
							isDouble = true;
							do { pos++; }
							while( pos < lenght && isIdentifier(string.charAt(pos)) );
						}
					}
					String str = string.substring(start, pos);
					try
					{
					if (isDouble)
						args.add( new FloatArgument(exp, start, Double.parseDouble(str)) );
					else
						args.add( new IntegerArgument(exp, start, Long.parseLong(str)) );
					}
					catch(NumberFormatException e)
					{
						pos = start;
						throwException("Can not parse '" + str + "' to " + ((isDouble) ? "double." : "long."), e);
					}
				}
				else
				{
					resolveKeywords(string.substring(start, pos));
				}
				opCount = 0;
				start = pos;
			}
			else
			{
				if (opCount == 2) throwException("Too many operators.", null);
				
				Operator op = getNextOperator();
				if (args.isEmpty() || opCount == 1)
				{
					if (op == Operator.SUB) op = Operator.NEG;
					else if (op != Operator.NOT)
						throwException("Illegal prefix operator: " + op, null);
				}
				
				args.add(new OperationNode(exp, start, op));
				opCount++;
				start = ++pos;
			}
		}
		if (isString) throwException("Missing String-Terminator.", null);
		
		if (args.isEmpty()) return new NullArgument(exp, 0);
		if (args.get(args.size()-1) instanceof OperationNode)
			throwException("Invalid expression.", null);
		
		return resolveStatement(offset);
	}
	
	private Node resolveStatement(int offset)
	{
		if (exp.getConfig().debug) System.out.print(args.subList(offset, args.size()));
		while(args.size() > offset+1)
		{
			int index = -1;
			Object e;
			int maxPrio = -1;
			for(int i = offset; i < args.size(); i++)
			{
				e = args.get(i);
				if (e instanceof OperationNode)
				{
					int curPrio = ((OperationNode) e).getPriority();
					if (curPrio > maxPrio)
					{
						index = i;
						maxPrio = curPrio;
					}
				}
			}
			
			precompileOperation(index);
		}
		if (exp.getConfig().debug) System.out.println(": " + args.get(offset));
		
		return args.get(offset);
	}
	
//	private void preCompileArrayOperation(int index)
//	{
//		ArrayOperation op = (ArrayOperation) args.get(index);
//		
//		if (op.getParent() instanceof Reference) return;
//	}
	
	private void precompileOperation(int index)
	{
		OperationNode op = (OperationNode) args.get(index);
		if (op.isPrefixOperation())
		{
			Node arg = args.get(index+1);
			if (op.getSourcePos() < arg.getSourcePos()-1)
				throw new EvaluationException(arg, "Prefix-Operator must not seperate from operand.");
			
			if (arg instanceof Operation)
				args.set(index, new PrefixOperation(op, arg));
			else
				args.set(index, Symbols.resolveOneArg(op, arg) );
			
			args.remove(index+1);
		}
		else if (op.isInfixOperation())
		{
			Node left = args.get(index-1);
			Node right = args.get(index+1);
			if (op.getOperator() == Operator.DOT || op.getOperator() == Operator.INDEX)
			{
				if (isWhiteSpace(string.charAt(op.getSourcePos()-1)))
					throw new EvaluationException(op, op.getOperator().name() + "-Operator must not seperate from operand.");
			}
			
			if (left instanceof Operation || right instanceof Operation)
				args.set(index-1, new InfixOperation(left, op, right));
			else
				args.set(index-1, Symbols.resolveTwoArgs(op, left, right) );
			
			args.remove(index+1);
			args.remove(index);
		}
		else
		{
			throw new UnsupportedOperationException("Operation not implemented.");
		}
	}
	
	private Operator getNextOperator()
	{
		if (string.length() == pos+1)
		{
			pos++;
			throwException("Invalid expression.", null);
		}
		
		char c = string.charAt(pos);
		switch(c)
		{
			case '+': return Operator.ADD;
			case '-': return Operator.SUB;
			case '*': return Operator.MUL;
			case '/': return Operator.DIV;
			case '%': return Operator.MOD;
			case ',': return Operator.COMMA;
			case '.': return Operator.DOT;
			case '!':
			{
				c = string.charAt(pos+1);
				switch(c)
				{
					case '=': pos++; return Operator.NOT_EQUAL;
					default: return Operator.NOT;
				}
			}
			case '=':
			{
				if ('=' == string.charAt(pos+1))
				{
					pos++;
					System.err.println("Warning! Equal-Operator is '='");
				}
				return Operator.EQUAL;
			}
			case '<':
			{
				c = string.charAt(pos+1);
				switch(c)
				{
					case '=': pos++; return Operator.LESS_EQUAL;
					case '<': pos++; return Operator.SHIFT_LEFT;
					default: return Operator.LESS;
				}
			}
			case '>':
			{
				c = string.charAt(pos+1);
				switch(c)
				{
					case '=': pos++; return Operator.GREATER_EQUAL;
					case '>': pos++;
					{
						if (string.charAt(pos+1) == '>')
						{
							pos++;
							return Operator.USHIFT_RIGHT;
						}
						else
							return Operator.SHIFT_RIGHT;
					}
					default: return Operator.GREATER;
				}
			}
			case '|':
			{
				if ('|' == string.charAt(pos+1))
				{
					pos++;
					System.err.println("Warning! Or-Operator is '|'");
				}
				return Operator.OR;
			}
			case '&':
			{
				if ('&' == string.charAt(pos+1))
				{
					pos++;
					System.err.println("Warning! And-Operator is '&'");
				}
				return Operator.AND;
			}
			case '^':
			{
				if ('^' == string.charAt(pos+1))
				{
					pos++;
					System.err.println("Warning! Exor-Operator is '^'");
				}
				return Operator.EXOR;
			}
			case ':':
			{
				c = string.charAt(pos+1);
				if (c == '=')
				{
					pos++;
					return Operator.ASSIGN;
				}
				break;
			}
		}
		throwException("Invalid Expression", null);
		return null;
	}
	
	private void addListNodes(char endKey)
	{
		char c = string.charAt(pos);
		// empty list?
		if (c == endKey)
		{
			pos++;
			start = pos;
			return;
		}
		do
		{
			addNextNode();
			if (lenght <= pos) throwException("Missing token '" + endKey + "'", null);
			c = string.charAt(pos++);
		}
		while(c == ',');
		if (c != endKey)
			throwException("Missing token '" + endKey + "'", null);
	}
	
	private void resolveKeywords(String name)
	{
		// Schlüsselwörter, die vorkompiliert werden sollen.
		if ("true".equals(name))
			args.add(new BooleanArgument(exp, start, true));
		else if ("false".equals(name))
			args.add(new BooleanArgument(exp, start, false));
		else if ("null".equals(name))
			args.add(new NullArgument(exp, start));
		else
			// Kein Schlüsselwort
			args.add(new Reference(exp, start, name));
	}
	
	private boolean isIdentifier(char c)
	{
		return (c > 47 && c < 58)		// Zahl
				|| (c > 64 && c < 91)	// Groß A-Z
				|| (c > 96 && c < 123)	// Klein a-z
				|| (c == '_') || (c == '$'); // Sonderzeichen
	}
	
	private boolean isWhiteSpace(char c)
	{
		return (c == ' ' || c == '\n' || c == '\t');
	}
	
	private void throwException(String message, Exception e) throws EvaluationException
	{
		throw new EvaluationException(string, pos, message, e);
	}
}
