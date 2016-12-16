package tws.expression.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class WebClient
{
	public void start() throws UnknownHostException, IOException
	{
		System.out.println("start client");
		Socket clientSocket = new Socket("localhost", 1234);
		
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		InputStream in;
		PrintWriter out;
		try
		{
			in = clientSocket.getInputStream();
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			
			String line;
			int count;
			byte[] buffer = new byte[1024];
			while((line = console.readLine()) != null)
			{
				out.println(line);
				
				count = in.read(buffer);
				
				System.out.write(buffer, 0, count);
				System.out.println();
			}
			System.out.println("close");
			clientSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
