package tws.expression;

import java.util.Collections;
import java.util.HashMap;
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
		if (args != null && config.usePredefinedFunctions)
		{
			if ("min".equals(name)) return min(args);
			if ("max".equals(name)) return max(args);
			if ("sin".equals(name)) return Math.sin(getSingleArgument(args).asDouble());
			if ("cos".equals(name)) return Math.cos(getSingleArgument(args).asDouble());
			if ("tan".equals(name)) return Math.tan(getSingleArgument(args).asDouble());
			if ("asin".equals(name)) return Math.asin(getSingleArgument(args).asDouble());
			if ("acos".equals(name)) return Math.acos(getSingleArgument(args).asDouble());
			if ("atan".equals(name)) return Math.atan(getSingleArgument(args).asDouble());
			if ("sinh".equals(name)) return Math.sinh(getSingleArgument(args).asDouble());
			if ("cosh".equals(name)) return Math.cosh(getSingleArgument(args).asDouble());
			if ("tanh".equals(name)) return Math.tanh(getSingleArgument(args).asDouble());
			if ("abs".equals(name)) return Math.abs(getSingleArgument(args).asDouble());
			if ("sign".equals(name)) return Math.signum(getSingleArgument(args).asDouble());
			if ("exp".equals(name)) return Math.exp(getSingleArgument(args).asDouble());
			if ("log".equals(name)) return Math.log(getSingleArgument(args).asDouble());
			if ("log10".equals(name)) return Math.log10(getSingleArgument(args).asDouble());
			if ("pow".equals(name))
			{
				if (args.length != 2) throw new EvaluationException("invalid Number of Arguments.");
				return Math.pow(args[0].asDouble(), args[1].asDouble());
			}
		}
		else if (args == null)
		{
			if (config.usePredefinedContants)
			{
				if ("PI".equals(name)) return PI;
				if ("E".equals(name)) return E;
			}
			if (config.useVariables && variables != null && variables.containsKey(name))
			{
				return variables.get(name);
			}
		}
		Resolver r = config.resolver;
		if (r == null) throw new EvaluationException("No resolver defined.");
		
		return r.resolve(name, args);
	}

	private Argument getSingleArgument(Argument[] args)
	{
		if (args.length != 1) throw new EvaluationException("invalid Number of Arguments.");
		return args[0];
	}
	
	private Argument min(Argument[] args)
	{
		if (args.length == 0) throw new EvaluationException("invalid Number of Arguments.");
		
		Argument min = args[0];
		for(int i = 1; i < args.length; i++)
		{
			if (min.asDouble() < args[i].asDouble()) min = args[i];
		}
		return min;
	}
	
	private Argument max(Argument[] args)
	{
		if (args.length == 0) throw new EvaluationException("invalid Number of Arguments.");
		
		Argument max = args[0];
		for(int i = 1; i < args.length; i++)
		{
			if (max.asDouble() > args[i].asDouble()) max = args[i];
		}
		return max;
	}

	@Override
	public void assign(String name, Argument value) throws EvaluationException
	{
		if (config.useVariables)
		{
			assign(name, value.asObject());
		}
		else
		{
			Resolver r = config.resolver;
			if (r == null) throw new EvaluationException("No resolver defined.");
			
			r.assign(name, value);
		}
	}

	public void assign(String name, Object value) throws EvaluationException
	{
		if (variables == null) variables = new HashMap<String, Object>();
		variables.put(name, value);
	}
	
	public Map<String, Object> getVariables()
	{
		return Collections.unmodifiableMap(variables);
	}

//	@Override
//	public Object invoke(Argument reciever, String name, Argument[] args) throws Exception
//	{
//		Invoker r = config.invoker;
//		if (r == null) throw new EvaluationException("No invoker defined.");
//		
//		return r.invoke(reciever, name, args);
//	}
}
