package tws.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class InternalResolver implements Resolver
{
	private static final Double PI = Math.PI;
	private static final Double E = Math.E;
	
	private final Config config;
	private Map<String, Object> variables;
	
	InternalResolver(Config config)
	{
		this.config = config;
	}

	@Override
	public Object resolve(String name, Argument[] args) throws EvaluationException
	{
		if (args != null)
		{
			if (config.usePredefinedFunctions)
			{
				if ("min".equals(name)) return min(args);
				if ("max".equals(name)) return max(args);
				if ("asc".equals(name)) return sort(args, true);
				if ("desc".equals(name)) return sort(args, false);
						
				if (args.length == 0)
				{
					if ("rand".equals(name)) return Math.random();
				}
				else if (args.length == 1)
				{
					double d = args[0].asDouble();
					
					if ("sin".equals(name)) return Math.sin(d);
					if ("cos".equals(name)) return Math.cos(d);
					if ("tan".equals(name)) return Math.tan(d);
					if ("asin".equals(name)) return Math.asin(d);
					if ("acos".equals(name)) return Math.acos(d);
					if ("atan".equals(name)) return Math.atan(d);
					if ("sinh".equals(name)) return Math.sinh(d);
					if ("cosh".equals(name)) return Math.cosh(d);
					if ("tanh".equals(name)) return Math.tanh(d);
					if ("abs".equals(name)) return Math.abs(d);
					if ("sign".equals(name)) return Math.signum(d);
					if ("exp".equals(name)) return Math.exp(d);
					if ("log".equals(name)) return Math.log(d);
					if ("log10".equals(name)) return Math.log10(d);
					if ("floor".equals(name)) return Math.floor(d);
					if ("round".equals(name)) return Math.round(d);
					if ("ceil".equals(name)) return Math.ceil(d);
					if ("rad".equals(name)) return Math.toRadians(d);
					if ("deg".equals(name)) return Math.toDegrees(d);
				}
				else if (args.length == 2)
				{
					double d1 = args[0].asDouble();
					double d2 = args[1].asDouble();
					
					if ("pow".equals(name)) return Math.pow(d1, d2);
					if ("atan2".equals(name)) return Math.atan2(d1, d2);
					if ("hypot".equals(name)) return Math.hypot(d1, d2);
				}
			}
			if (config.useVariables && variables != null)
			{
				Object obj = variables.get(name);
				if (obj instanceof DynamicOperation)
				{
					DynamicOperation func = (DynamicOperation) obj;
					return func.call(args);
				}
			}
		}
		else
		{
			if (config.usePredefinedContants)
			{
				if ("PI".equals(name)) return PI;
				if ("E".equals(name)) return E;
			}
			synchronized (this)
			{
				if (config.useVariables && variables != null && variables.containsKey(name))
				{
					return variables.get(name);
				}
			}
		}
		Resolver r = config.resolver;
		if (r == null) throw new EvaluationException("No resolver defined.");
		
		return r.resolve(name, args);
	}

	private List<Argument> toList(Argument[] args)
	{
		if (args.length == 1)
		{
			// Vermeide unnötiges ein-/auspacken bei ListArgument
			if (args[0] instanceof ListArgument)
			{
				return ((ListArgument) args[0]).getValues();
			}
			else if (args[0] instanceof ObjectArgument)
			{
				ObjectArgument arg =  (ObjectArgument) args[0];
				List list = arg.asList();
				List<Argument> arguments = new ArrayList<Argument>(list.size());
				for(int i = 0; i < list.size(); i++)
				{
					arguments.add(Config.wrap(arg, list.get(i), true));
				}
				return arguments;
			}
		}
		return Arrays.asList(args);
	}
	
	private ListArgument sort(Argument[] args, boolean asc)
	{
		if (args.length == 0) throw new EvaluationException("Invalid number of arguments.");
		
		INode parent = null;
		if (args[0] instanceof INode)
			parent = ((INode) args[0]).getParent();
		
		Argument[] array = toList(args).toArray(args);
		
		Comparator<Argument> c;
		if (asc)
			c = ArgumentComperator.INSTANCE;
		else
			c = Collections.reverseOrder(ArgumentComperator.INSTANCE);
		
		Arrays.sort(array, c);
		return new ListArgument(parent, array);
	}
	
	private Argument min(Argument[] args)
	{
		if (args.length == 0) throw new EvaluationException("Invalid number of arguments.");
		
		List<Argument> list = toList(args);
		Argument min = list.get(0);
		for(int i = 1; i < list.size(); i++)
		{
			if (Symbols.compareNumeric(min, min, list.get(i)) == 1) min = list.get(i);
		}
		return min;
	}
	
	private Argument max(Argument[] args)
	{
		if (args.length == 0) throw new EvaluationException("Invalid number of arguments.");

		List<Argument> list = toList(args);
		Argument max = list.get(0);
		for(int i = 1; i < list.size(); i++)
		{
			if (Symbols.compareNumeric(max, max, list.get(i)) == -1) max = list.get(i);
		}
		return max;
	}

//	@Override
//	public void assign(String name, Argument value) throws EvaluationException
//	{
//		assign(name, (Object) value);
//	}

	@Override
	public void assign(String name, Object value) throws EvaluationException
	{
		if (config.useVariables)
		{
			synchronized (this)
			{
				if (variables == null) variables = new HashMap<String, Object>();
				variables.put(name, value);
			}
		}
		else
		{
			Resolver r = config.resolver;
			if (r == null) throw new EvaluationException("No resolver defined.");
			
			r.assign(name, value);
		}
	}
	
	public Map<String, Object> getVariables()
	{
		return Collections.unmodifiableMap(variables);
	}
	
	private static class ArgumentComperator implements Comparator<Argument>
	{
		static final ArgumentComperator INSTANCE = new ArgumentComperator();
		
		@Override
		public int compare(Argument a1, Argument a2)
		{
			return Symbols.compareNumeric(a1, a1, a2);
		}
	}
}
