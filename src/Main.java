////////////////////////////////////////////////////////////////////////////////
// Try CODE VS5.0
// author: Leonardone @ NEETSDKASU
/* /////////////////////////////////////////////////////////////////////////////
The MIT License (MIT)

Copyright (c) 2016 Leonardone @ NEETSDKASU

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/ /////////////////////////////////////////////////////////////////////////////

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("ManualPlay");
		System.out.flush();
		
		TurnScanner scanner = new TurnScanner(System.in);
		ServerConnector conn = new ServerConnector();
		
		Thread thread =  new Thread(conn);
		thread.start();
		
		for (int loop = 0; loop < 300; loop++)
		{
			String input = scanner.getTurnState();
			
			if (input == null) break;
			
			conn.setInput(input);
			
			String output = conn.getOutput();
			
			System.out.print(output);
			System.out.flush();
			
		}
		
		conn.close();
		thread.join();
		System.err.println("finished Main");
	}
}

class ServerConnector implements Runnable, Closeable, AutoCloseable
{
	public static final String  ADDR = null;
	public static final int     PORT = 45459;
	
	private final    AtomicBoolean recvInput  = new AtomicBoolean(false);
	private volatile String        input      = "";
	private final    AtomicBoolean recvOutput = new AtomicBoolean(false);
	private volatile String        output     = "";
	private volatile boolean       running    = true;
	private volatile boolean       failure    = false;
	private final    Socket        soc;
	
	public ServerConnector() throws Exception
	{
		soc = new Socket(ADDR, PORT);
	}
	
	public void close() throws IOException
	{
		running = false;
		soc.close();
	}
	
	public void setInput(String input)
	{
		this.input = input;
		recvInput.set(true);
	}
	
	public String getOutput()
	{
		while (recvOutput.compareAndSet(true, false) == false)
			if (failure) break;
		return output;
	}
	
	public void run()
	{
		try
		{
			PrintWriter           out  = new PrintWriter(soc.getOutputStream());
			BufferedReader        in   = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintWriter           buf  = new PrintWriter(baos);
			
			int count = 0;
			
			while (running)
			{
				count++;
				System.err.printf("%3d: --------------------- %s"
					, count, System.lineSeparator());
				
				if (recvInput.get() == false) continue;
				
				System.err.println("sending input...");
				
				out.print(input);
				out.flush();
				
				System.err.println("sent input.");
				
				System.err.println("waiting reciving output...");
				
				baos.reset();
				
				String line = in.readLine();
				
				if (line == null)
				{
					System.err.println("null poge");
					throw new NullPointerException("null poge");
				}
				
				System.err.println("recieving output...");
				
				buf.println(line);
				
				int n = Integer.parseInt(line);
				for (int i = 0; i < n; i++)
				{
					buf.println(in.readLine());
				}
				buf.flush();
				
				System.err.println("recieved output.");
				
				while (recvOutput.get())
					if (running == false) return;
				
				output = baos.toString();
				
				recvOutput.set(true);
				recvInput.set(false);
				
				System.err.println("end of turn.");
			}
			
		}
		catch (Exception ex)
		{
			output = "2" + System.lineSeparator() + System.lineSeparator() + System.lineSeparator();
			failure = true;
			ex.printStackTrace();
		}
	}
}

class TurnScanner
{
	BufferedReader         in;
	ByteArrayOutputStream  baos;
	PrintWriter            out;
	
	public TurnScanner(InputStream is)
	{
		in = new BufferedReader(new InputStreamReader(is));
		baos = new ByteArrayOutputStream();
		out = new PrintWriter(baos);
	}
	
	private void scanFieldState() throws Exception
	{
		String line;
		int count;
		
		line = in.readLine(); out.println(line); // Ninja power
		
		line = in.readLine(); out.println(line); // Field Size
		
		String[] rowcol = line.split(" ");
		count = Integer.parseInt(rowcol[0]);
		
		for (int i = 0; i < count; i++)
			out.println(in.readLine());  // field data
		
		
		for (int j = 0; j < 3; j++)
		{
			line = in.readLine(); out.println(line); // Kunoichis, Dogs, NinjaSouls
	
			count = Integer.parseInt(line);
			for (int i = 0; i < count; i++)
				out.println(in.readLine());
		}
		
		out.println(in.readLine()); // 
	}
	
	public String getTurnState() throws Exception
	{
		baos.reset();
		
		for (int i = 0; i < 3; i++)  // time, ninjutsu kinds, ninjutsu costs
		{
			String line = in.readLine();
			if (line == null) return null;
			out.println(line);
		}
		
		// my
		scanFieldState();
		
		// rival
		scanFieldState();
		
		out.flush();
		return baos.toString();
	}
	
}
