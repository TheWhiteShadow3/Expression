package tws.expression.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import tws.expression.Config;
import tws.expression.Config.NullCast;
import tws.expression.DefaultInvoker;
import tws.expression.EvaluationException;
import tws.expression.Expression;


public class ExpressionServer implements Runnable
{
	private ServerSocket socket;
	
	public ExpressionServer(String host, int port) throws IOException
	{
		this.socket = new ServerSocket(port, 0, InetAddress.getByName(host));
		
		System.setSecurityManager(new ExpressionSecurityManager());
	}

	class Worker extends Thread
	{
//		private Socket con;
		private BufferedReader in;
		private PrintWriter out;
		private Config config;
		
		public Worker(Socket con)
		{
//			this.con = con;
			try
			{
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				out = new PrintWriter(con.getOutputStream(), false);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
//			System.out.println("Verbunden mit " + con.getInetAddress().getHostAddress());
			
			initConfig();
		}
		
		private void initConfig()
		{
			this.config = new Config();
			config.nullCast = NullCast.TO_EMPTY_STRING;
			config.useVariables = true;
			config.invoker = new DefaultInvoker();
		}

		@Override
		public void run()
		{
			String line;
			String result;
			try
			{
				while((line = in.readLine()) != null)
				{
					if (line.isEmpty()) result = "";
					else try
					{
						result = new Expression(line, config).evaluate().toString();
					}
					catch(EvaluationException e)
					{
						out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
						result = "";//e.toString();
					}
					out.println(result);
					out.flush();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
//			System.out.println("Getrennt von " + con.getInetAddress().getHostAddress());
		}
	}

	@Override
	public void run()
	{
		try
		{
			while(true)
			{
				Socket con = socket.accept();
				Thread worker = new Worker(con);
				worker.start();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		System.out.println("start server");
		Thread thread = new Thread(new ExpressionServer(null, 1234));
		thread.setDaemon(true);
		thread.start();
		
		// start Client
		new WebClient().start();
	}
}
