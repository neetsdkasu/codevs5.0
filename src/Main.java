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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		System.out.println(AI.NAME);

		StateScanner scanner = new StateScanner(System.in);
		AI ai = new AI();
		
		for (int loop = 0; loop < 300; loop++)
		{
			TurnState ts = scanner.scanTurnState();
			
			if (ts == null)
			{
				break;
			}
			
			// check game over
			if (ts.isGameOver())
			{
				break;
			}
			
			try
			{
				ai.compute(ts);
			}
			catch (Throwable ex)
			{
				ex.printStackTrace();
			}
			
			// output
			if (ai.existsNinjutsu())
			{
				System.out.println(3);
				System.out.println(ai.getNinjutsuCommand());
			}
			else
			{
				System.out.println(2);
			}
			System.out.println(ai.getKunoichiCommand(0));
			System.out.println(ai.getKunoichiCommand(1));
			System.out.flush();

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
	
	List<RowCol> scanRowCols() throws Exception
	{
		int count = gI();
		List<RowCol> rowcols = new ArrayList<>();
		for (int i = 0; i < count; i++)
		{
			rowcols.add(scanRowCol());
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
		
		String line = gS();
		
		if (line == null) return null;
		
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
	WALL, FLOOR, ROCK, DOG, DANGEROUS_ZONE;
	
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
	public List<RowCol>    souls;
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

class AI
{
	public static final String NAME = "DefeatRandomAI";

	private TurnState old_state = null;
	private final Ninjutsu  ninjutsu_command = new Ninjutsu(), old_ninjutsu_command = new Ninjutsu();
	private String[]  kunoichi_commands, old_kunoitchi_commands;
	
	private void initCompute(TurnState ts)
	{
		ninjutsu_command.clear();
		if (kunoichi_commands == null)
		{
			kunoichi_commands = new String[ts.my_state.kunoichis.length];
			old_kunoitchi_commands = new String[kunoichi_commands.length];
		}
		for (int i = 0; i < kunoichi_commands.length; i++)
		{
			kunoichi_commands[i] = "";
		}
	}
	
	public boolean existsNinjutsu()
	{
		return ninjutsu_command.exists();
	}
	
	public String getNinjutsuCommand()
	{
		return ninjutsu_command.toString();
	}
	
	public String getKunoichiCommand(int id)
	{
		return kunoichi_commands[id];
	}

	public void compute(TurnState ts)
	{
		initCompute(ts);
		
		computeInner(ts);
		old_state = ts;
		for (int i = 0; i < kunoichi_commands.length; i++)
		{
			old_kunoitchi_commands[i] = kunoichi_commands[i];
		}
		old_ninjutsu_command.copyFrom(ninjutsu_command);
	}
	
	private int[][] findSoulDistanceTable(FieldState fs)
	{
		RowCol size = fs.field_size;
		FieldObject[][] field = fs.field;
		int[][] table = new int[size.row][size.col];
		Deque<RowCol> cur = new ArrayDeque<>(), next = new ArrayDeque<>(), temp;
		for (RowCol rc : fs.souls)
		{
			if (fs.field[rc.row][rc.col] != FieldObject.FLOOR) continue;
			table[rc.row][rc.col] = 3;
			cur.addFirst(rc);
		}
		int[] add_rows = {1, 0, -1,  0};
		int[] add_cols = {0, 1,  0, -1};
		int distance = 3;
		while (cur.isEmpty() == false)
		{
			distance++;
			next.clear();
			for (RowCol rc : cur)
			{
				for (int i = 0; i < 4; i++)
				{
					RowCol addrc = rc.move(add_rows[i], add_cols[i]);
					if (table[addrc.row][addrc.col] != 0) continue;
					if (field[addrc.row][addrc.col] != FieldObject.FLOOR) continue;
					table[addrc.row][addrc.col] = distance;
					next.addFirst(addrc);
				}
			}
			// swap cur next
			temp = cur; cur = next; next = temp;
		}
		for (int i = 0; i < fs.field_size.row; i++)
		{
			for (int j = 0; j < fs.field_size.col; j++)
			{
				if (fs.field[i][j] == FieldObject.DOG || fs.field[i][j] == FieldObject.DANGEROUS_ZONE)
				{
					table[i][j] = 1;
					continue;
				}
				if (fs.field[i][j] == FieldObject.ROCK)
				{
					if (i > 1 && i < fs.field_size.row - 2)
					{
						if (fs.field[i - 1][j] == FieldObject.FLOOR
							&& fs.field[i - 2][j] == FieldObject.FLOOR
							&& fs.field[i + 1][j] == FieldObject.FLOOR
							&& fs.field[i + 2][j] == FieldObject.FLOOR)
						{
							table[i][j] = 2;
							continue;
						}
					}
					else if (j > 1 && j < fs.field_size.col - 2)
					{
						if (fs.field[i][j - 1] == FieldObject.FLOOR
							&& fs.field[i][j - 2] == FieldObject.FLOOR
							&& fs.field[i][j + 1] == FieldObject.FLOOR
							&& fs.field[i][j + 2] == FieldObject.FLOOR)
						{
							table[i][j] = 2;
							continue;
						}
					}
				}
				if (table[i][j] != 0 || fs.field[i][j] != FieldObject.FLOOR) continue;
				int max = 0;
				RowCol rc = new RowCol(i, j);
				for (Unit kunoichi : fs.kunoichis)
				{
					max = Math.max(max, rc.distanceTo(kunoichi.pos));
				}
				table[i][j] = 1000 - max;
			}
		}
		return table;
	}
	
	private void mappingDogs(FieldState fs)
	{
		outerloop:
		for (Unit dog : fs.dogs)
		{
			if (fs.field[dog.pos.row][dog.pos.col] != FieldObject.FLOOR) continue;
			for (Unit kunoichi : fs.kunoichis)
			{
				if (kunoichi.pos.distanceTo(dog.pos) < 8)
				{
					fs.field[dog.pos.row][dog.pos.col] = FieldObject.DOG;
					continue outerloop;
				}
			}
		}
		for (int i = 1; i < fs.field_size.row - 1; i++)
		{
			for (int j = 1; j < fs.field_size.col - 1; j++)
			{
				if (fs.field[i][j] != FieldObject.FLOOR) continue;
				if (fs.field[i + 1][j] == FieldObject.DOG
					|| fs.field[i - 1][j] == FieldObject.DOG
					|| fs.field[i][j + 1] == FieldObject.DOG
					|| fs.field[i][j - 1] == FieldObject.DOG)
				{
					fs.field[i][j] = FieldObject.DANGEROUS_ZONE;
				}
			}
		}
	}
	
	private void searchAllKunoichiRoot(int[][] souls_table, int s, int n, RowCol pos, String root, Map<Integer, String> roots)
	{
		if (s != n && souls_table[pos.row][pos.col] == 0)
		{
			boolean flag = false;
			int[] add_col = { 0, 1, 0, -1}, add_row = { -1, 0, 1, 0};
			int p = 0;
			switch (root.charAt(root.length() - 1))
			{
				case 'U': p = 0; break;
				case 'R': p = 1; break;
				case 'D': p = 2; break;
				case 'L': p = 3; break;
			}
			RowCol xx, yy;
			xx = pos.move(add_row[(p + 1) & 3], add_col[(p + 1) & 3]);
			yy =  xx.move(add_row[(p + 2) & 3], add_col[(p + 2) & 3]);
			flag |= souls_table[xx.row][xx.col] > 0 && souls_table[yy.row][yy.col] == 0;
			xx = pos.move(add_row[(p + 3) & 3], add_col[(p + 3) & 3]);
			yy =  xx.move(add_row[(p + 2) & 3], add_col[(p + 2) & 3]);
			flag |= souls_table[xx.row][xx.col] > 0 && souls_table[yy.row][yy.col] == 0;
			xx = pos.move(add_row[p], add_col[p]);
			yy =  xx.move(add_row[p], add_col[p]);
			flag |= yy.row >= 0 && yy.row < souls_table.length
				 && yy.col >= 0 && xx.col < souls_table[0].length
				 && souls_table[xx.row][xx.col] > 0 && souls_table[yy.row][yy.col] > 0;
			if (flag == false) return;
		}
		roots.put(souls_table[pos.row][pos.col], root);
		if (n == 0) return;
		searchAllKunoichiRoot(souls_table, s, n - 1, pos.move(1, 0),  root + "D", roots);
		searchAllKunoichiRoot(souls_table, s, n - 1, pos.move(-1, 0), root + "U", roots);
		searchAllKunoichiRoot(souls_table, s, n - 1, pos.move(0, 1),  root + "R", roots);
		searchAllKunoichiRoot(souls_table, s, n - 1, pos.move(0, -1), root + "L", roots);
	}
	
	private List<RowCol> parseRoot(RowCol from, String root)
	{
		List<RowCol> list = new ArrayList<>();
		looplabel:
		for (int i = 0; i < root.length(); i++)
		{
			switch (root.charAt(i))
			{
				case 'D': from = from.move(1, 0); break;
				case 'U': from = from.move(-1, 0); break;
				case 'L': from = from.move(0, -1); break;
				case 'R': from = from.move(0, 1); break;
				default: continue looplabel;
			}
			list.add(from);
		}
		return list;
	}
	
	private void computeKunoichiRoot(int[][] souls_table, Unit kunoichi, FieldState fs, int s)
	{
		Map<Integer, String> roots = new HashMap<>();
		searchAllKunoichiRoot(souls_table, s, s, kunoichi.pos, "", roots);
		int min = Integer.MAX_VALUE;
		String root = "";
		for (Integer key : roots.keySet())
		{
			if (key.intValue() >= 3 && key.intValue() < min)
			{
				root = roots.get(key);
				min = key.intValue();
			}
		}
		for (int i = root.length(); i < s; i++)
		{
			root += "N";
		}
		List<RowCol> path = parseRoot(kunoichi.pos, root);
		RowCol from = kunoichi.pos;
		for (RowCol rc : path)
		{
			fs.souls.remove(rc);
			if (fs.field[rc.row][rc.col] == FieldObject.ROCK)
			{
				RowCol df = from.subtractFrom(rc);
				fs.field[rc.row][rc.col] = FieldObject.FLOOR;
				from = rc;
				df = rc.move(df.row, df.col);
				fs.field[rc.row][rc.col] = FieldObject.ROCK;
			}
		}
		kunoichi_commands[kunoichi.id] = root;
	}
	
	private void computeInner(TurnState ts)
	{
		mappingDogs(ts.my_state);
		
		for (Unit kunoichi : ts.my_state.kunoichis)
		{
			int[][] souls_table = findSoulDistanceTable(ts.my_state);
			computeKunoichiRoot(souls_table, kunoichi, ts.my_state, 2);
		}
	}
}

