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
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.atomic.AtomicBoolean;

import java.net.Socket;
import java.net.ServerSocket;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Player implements Runnable
{
	public void run()
	{
		try
		{
			Server server = new Server();
			PlayerUI ui = PlayerUI.getInstance(server);
			ui.setVisible(true);
			(new Thread(server)).start();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		SwingUtilities.invokeLater(new Player());
	}
}

class Server implements Runnable, Closeable, AutoCloseable
{
	public static final int PORT   = 45459;

	private ServerSocket    sevsoc = null;
	
	public Server() throws Exception
	{
		sevsoc = new ServerSocket(PORT);
	}
	
	public void close() throws IOException
	{
		try
		{
			ClientConnector.getInstance().close();
		}
		catch (NullPointerException ex)
		{
		}
		sevsoc.close();
	}
	
	public void run()
	{
		try
		{
			for(;;)
			{
				Socket soc = sevsoc.accept();
				
				ClientConnector.makeNewInstance(soc);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}

class ClientConnector implements Runnable, Closeable, AutoCloseable
{
	private static volatile ClientConnector instance = null;
	private static final Object lock = new Object();
	
	public static ClientConnector getInstance()
	{
		synchronized (lock)
		{
			if (instance == null) throw new NullPointerException("not exist instance");
			return instance;
		}
	}
	public static void makeNewInstance(Socket soc) throws Exception
	{
		synchronized (lock)
		{
			if (instance != null)
			{
				instance.close();
			}
			instance = new ClientConnector(soc);
			(new Thread(instance)).start();
		}
	}
	
	private volatile Socket soc;
	private volatile boolean running = true;
	private final AtomicBoolean recvOutput = new AtomicBoolean(false);
	private volatile String output = "";
	
	private ClientConnector(Socket soc)
	{
		this.soc = soc;
	}
	
	public void close() throws IOException
	{
		running = false;
		soc.close();
	}
	
	public void setOutput(String output)
	{
		this.output = output;
		recvOutput.set(true);
	}
	
	public void run()
	{
		try
		{
			StateScanner scanner = new StateScanner(soc.getInputStream());
			PrintWriter  writer  = new PrintWriter(soc.getOutputStream());
			
			int count = 0;
			
			while (running)
			{
				count++;
				System.err.printf("%3d: --------------------- %s"
					, count, System.lineSeparator());
				
				System.err.println("recieving input...");
				
				TurnState ts = scanner.scanTurnState();
				
				System.err.println("recieved input.");
				
				if (ts == null)
				{
					System.err.println("null poge.");
					break;
				}
				
				PlayerUI.getInstance(null).setInput(this, ts);
				
				System.err.println("waiting decide output...");
				
				while (recvOutput.get() == false) if (running == false) return;
				
				System.err.println("sending output...");
				
				writer.print(output);
				writer.flush();
				
				System.err.println("sent output.");
				
				recvOutput.set(false);
				
				System.err.println("end of turn");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}

class StateScanner
{
	public static long  toL(String s) { return Long.parseLong(s); }
	public static int   toI(String s) { return Integer.parseInt(s); }
	public static int[] toIs(String[] ss)
	{
		int[] rs = new int[ss.length];
		for (int i = 0; i < ss.length; i++)
		{
			rs[i] = toI(ss[i]);
		}
		return rs;
	}
	String gS() throws Exception { return in.readLine(); }
	long   gL() throws Exception { return toL(gS()); }
	int    gI() throws Exception { return toI(gS()); }
	int[]  gIs() throws Exception { return toIs(gS().split(" ")); }
	
	BufferedReader in;
	
	public StateScanner(InputStream is)
	{
		in = new BufferedReader(new InputStreamReader(is));
	}
	
	FieldObject[][] scanField(RowCol size) throws Exception
	{
		FieldObject[][] field = new FieldObject[size.row][size.col];
		for (int i = 0; i < size.row; i++)
		{
			String cols = gS();
			for (int j = 0; j < size.col; j++)
			{
				field[i][j] = FieldObject.valueOf(cols.charAt(j));
			}
		}
		return field;
	}
	
	Unit scanUnit() throws Exception
	{
		return new Unit(gIs());
	}
	
	Unit[] scanUnits() throws Exception
	{
		int count = gI();
		Unit[] units = new Unit[count];
		for (int i = 0; i < count; i++)
		{
			units[i] = scanUnit();
		}
		return units;
	}
	
	RowCol scanRowCol() throws Exception
	{
		return new RowCol(gIs());
	}
	
	RowCol[] scanRowCols() throws Exception
	{
		int count = gI();
		RowCol[] rowcols = new RowCol[count];
		for (int i = 0; i < count; i++)
		{
			rowcols[i] = scanRowCol();
		}
		return rowcols;
	}
	
	FieldState scanFieldState() throws Exception
	{
		FieldState fs = new FieldState();
		
		fs.ninja_enegy = gI();
		fs.field_size  = scanRowCol();
		fs.field       = scanField(fs.field_size);
		
		fs.kunoichis   = scanUnits();
		fs.dogs        = scanUnits();
		fs.souls       = scanRowCols();
		
		fs.ninjutsu_used_counts = gIs();
		
		return fs;
	}
	
	public TurnState scanTurnState() throws Exception
	{
		TurnState ts = new TurnState();
		
		String line = gS(); if (line == null) return null;
		
		ts.remain_time          = toL(line);
		ts.ninjutsu_kinds_count = gI();
		ts.ninjutsu_costs       = gIs();
		
		ts.my_state             = scanFieldState();
		ts.rival_state          = scanFieldState();
		
		return ts;
	}
}

enum NinjutsuType
{
	SPEED_UP,
	DROP_ROCK_MY_FIELD,
	DROP_ROCK_RIVAL_FIELD,
	THUNDERSTROKE_MY_FIELD,
	THUNDERSTROKE_RIVAL_FIELD,
	MAKE_MY_DUMMY,
	MAKE_RIVAL_DUMMY,
	TURN_CUTTING;
	
	public static NinjutsuType valueOf(int ordinal)
	{
		switch (ordinal)
		{
			case 0: return SPEED_UP;
			case 1: return DROP_ROCK_MY_FIELD;
			case 2: return DROP_ROCK_RIVAL_FIELD;
			case 3: return THUNDERSTROKE_MY_FIELD;
			case 4: return THUNDERSTROKE_RIVAL_FIELD;
			case 5: return MAKE_MY_DUMMY;
			case 6: return MAKE_RIVAL_DUMMY;
			default: return TURN_CUTTING;
		}
	}
}

class Ninjutsu
{
	public NinjutsuType type = null;
	public RowCol pos = null;
	public int kunoichi_id = -1;
	public String toString()
	{
		if (type == null) return "";
		StringBuilder sb = new StringBuilder();
		sb.append(type.ordinal());
		if (pos != null)
		{
			sb.append(' ');
			sb.append(pos.toString());
		}
		if (kunoichi_id >= 0)
		{
			sb.append(' ');
			sb.append(kunoichi_id);
		}
		return sb.toString();
	}
	public void clear()
	{
		type = null;
		pos = null;
		kunoichi_id = -1;
	}
	public boolean exists()
	{
		return type != null;
	}
	public void copyFrom(Ninjutsu src)
	{
		type = src.type;
		pos = src.pos;
		kunoichi_id = src.kunoichi_id;
	}
}

enum FieldObject
{
	WALL, FLOOR, ROCK;
	
	public static FieldObject valueOf(char ch)
	{
		switch (ch)
		{
			case 'W': return WALL;
			case 'O': return ROCK;
			default:  return FLOOR;
		}
	}
}

class RowCol
{
	public final int row;
	public final int col;
	
	public RowCol(int row, int col) { this.row = row; this.col = col; }
	public RowCol(int[] row_col) { this(row_col, 0); }
	public RowCol(int[] row_col, int offset)
	{
		row = row_col[0 + offset];
		col = row_col[1 + offset];
	}
	public int hashCode() { return (row << 8) | col; }
	public boolean equals(Object o)
	{
		if (o == this) return true;
		if (o == null) return false;
		if (!o.getClass().equals(getClass())) return false;
		return hashCode() == o.hashCode();
	}
	public String toString() { return row + " " + col; }
	
	public RowCol move(int add_row, int add_col)
	{
		return new RowCol(row + add_row, col + add_col);
	}
	public int distanceTo(RowCol rc)
	{
		return Math.abs(row - rc.row) + Math.abs(col - rc.col);
	}
	public RowCol subtractFrom(RowCol rc)
	{
		return rc.move(-row, -col);
	}
}

class Unit
{
	public final int id;
	public final RowCol pos;
	
	public Unit(int[] values)
	{
		id = values[0];
		pos = new RowCol(values, 1);
	}
	public int hashCode() { return id; }
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null) return false;
		if (!o.getClass().equals(getClass())) return false;
		return hashCode() == o.hashCode();
	}
}

class FieldState
{
	public int             ninja_enegy;
	public RowCol          field_size;
	public FieldObject[][] field;
	public Unit[]          kunoichis;
	public Unit[]          dogs;
	public RowCol[]        souls;
	public int[]           ninjutsu_used_counts;
	
	public int getKunoichiCount() { return kunoichis.length; }
}

class TurnState
{
	public long       remain_time;
	public int        ninjutsu_kinds_count;
	public int[]      ninjutsu_costs;
	public FieldState my_state;
	public FieldState rival_state;
	
	public boolean isGameOver()
	{
		return my_state.getKunoichiCount() + rival_state.getKunoichiCount() != 4;
	}
}

class GamePanel extends JPanel
{
	private BufferedImage backgroundImage;
	
	public GamePanel()
	{
		super();
		setSize(640,480);
		backgroundImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
	}
	
	public void paintComponent(Graphics g)
	{
		g.drawImage(backgroundImage, 0, 0, this);
	}
	
	private void drawField(Graphics2D g, FieldState fs, int offX, int offY)
	{
		
		g.setColor(Color.BLACK);
		
		g.drawString(Integer.toString(fs.ninja_enegy), offX, offY - 20);
		
		for (int i = 0; i < fs.field_size.row; i++)
		{
			for (int j = 0; j < fs.field_size.col; j++)
			{
				switch (fs.field[i][j])
				{
					case WALL:
						g.drawString("W", j * 20 + offX, i * 20 + offY);
						break;
					case ROCK:
						g.drawString("O", j * 20 + offX, i * 20 + offY);
						break;
				}
			}
		}
		g.setColor(Color.BLUE);
		for (RowCol pos : fs.souls)
		{
			g.drawString("S", pos.col * 20 + offX, pos.row * 20 + offY);
		}
		g.setColor(Color.RED);
		for (Unit dog : fs.dogs)
		{
			g.drawString("d", dog.pos.col * 20 + offX, dog.pos.row * 20 + offY);
		}
		g.setColor(Color.MAGENTA);
		for (Unit kunoichi : fs.kunoichis)
		{
			g.drawString("@", kunoichi.pos.col * 20 + offX, kunoichi.pos.row * 20 + offY);
		}
		
	}
	
	public void drawTurnState(TurnState ts)
	{
		SwingUtilities.invokeLater( () -> {
			Graphics2D g = backgroundImage.createGraphics();
			
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			
			g.setFont(g.getFont().deriveFont(Font.BOLD));
			drawField(g, ts.my_state, 20, 50);
			drawField(g, ts.rival_state, 335, 50);
			
			int p = getWidth() / 30;
			int h = 20 * ts.my_state.field_size.row + 55;
			
			g.setColor(Color.BLUE);
			g.drawString(Integer.toString(ts.ninjutsu_costs[0]), p * (2 +  0), h);
			g.drawString(Integer.toString(ts.ninjutsu_costs[1]), p * (1 +  5), h);
			g.drawString(Integer.toString(ts.ninjutsu_costs[3]), p * (1 + 10), h);
			g.drawString(Integer.toString(ts.ninjutsu_costs[5]), p * (1 + 15), h);
			g.drawString(Integer.toString(ts.ninjutsu_costs[7]), p * (2 + 20), h);
			g.setColor(Color.RED);
			g.drawString(Integer.toString(ts.ninjutsu_costs[2]), p * (3 +  5), h);
			g.drawString(Integer.toString(ts.ninjutsu_costs[4]), p * (3 + 10), h);
			g.drawString(Integer.toString(ts.ninjutsu_costs[6]), p * (3 + 15), h);
			
			repaint();
		});
	}
}

class PlayerUI extends JFrame
{
	private static volatile PlayerUI instance = null;
	public static PlayerUI getInstance(Server server)
	{
		if (instance == null)
		{
			instance = new PlayerUI(server);
		}
		return instance;
	}
	
	private final Server server;
	private GamePanel game_panel;
	private volatile ClientConnector conn = null;
	private volatile TurnState ts = null;
	
	private PlayerUI(Server server)
	{
		super("ManualPlay UI");
		this.server = server;
	}
	
	protected void frameInit()
	{
		super.frameInit();
		
		setSize(640, 460);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout());
		
		add(game_panel = new GamePanel(), BorderLayout.CENTER);
		
		JPanel jPanel = new JPanel(); 
		
		jPanel.setLayout(new GridLayout(1, 6));
		
		JButton btn;
		jPanel.add(new JButton("Speed Up"));
		jPanel.add(new JButton("Drop Rock"));
		jPanel.add(new JButton("Thunder"));
		jPanel.add(new JButton("Dummy"));
		jPanel.add(new JButton("Turn Cut"));
		jPanel.add(btn = new JButton("OK"));
		btn.addActionListener( e -> {
			conn.setOutput("2" + System.lineSeparator() + System.lineSeparator() + System.lineSeparator());
		});
		add(jPanel, BorderLayout.SOUTH);
		
		
	}
	
	protected void processWindowEvent(WindowEvent e)
	{
		try
		{
			switch (e.getID())
			{
			case WindowEvent.WINDOW_CLOSING:
				if (server != null) server.close();
				break;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			super.processWindowEvent(e);
		}
	}
	
	public void setInput(ClientConnector conn, TurnState ts)
	{
		this.conn = conn;
		this.ts = ts;
		game_panel.drawTurnState(ts);
		
	}
}