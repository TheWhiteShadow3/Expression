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

	private Argument min(Argument[] args)
	{
		if (args.length == 0) throw new EvaluationException("Invalid number of arguments.");
		
		Argument min = args[0];
		for(int i = 1; i < args.length; i++)
		{
			if (min.asDouble() > args[i].asDouble()) min = args[i];
		}
		return min;
	}
	
	private Argument max(Argument[] args)
	{
		if (args.length == 0) throw new EvaluationException("Invalid number of arguments.");
		
		Argument max = args[0];
		for(int i = 1; i < args.length; i++)
		{
			if (max.asDouble() < args[i].asDouble()) max = args[i];
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
}
