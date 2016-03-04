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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		
		ts.remain_time          = gL();
		ts.ninjutsu_kinds_count = gI();
		ts.ninjutsu_costs       = gIs();
		
		ts.my_state             = scanFieldState();
		ts.rival_state          = scanFieldState();
		
		return ts;
	}
}

class NinjutsuTypeUtil
{
	static Map<Integer, NinjutsuType> map = new HashMap<>();
	public static NinjutsuType valueOf(int ordinal)
	{
		return map.get(ordinal);
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
	
	
	private NinjutsuType()
	{
		NinjutsuTypeUtil.map.put(ordinal(), this);
	}
}

class Ninjutsu
{
	public NinjutsuType type = null;
	public RowCol pos = null;
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
		return sb.toString();
	}
	public void clear()
	{
		type = null;
		pos = null;
	}
	public boolean exists()
	{
		return type != null;
	}
	public void copyFrom(Ninjutsu src)
	{
		type = src.type;
		pos = src.pos;
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
	public int id;
	public RowCol pos;
	
	public Unit(int[] values)
	{
		id = values[0];
		pos = new RowCol(values, 1);
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

class AI
{
	public static final String NAME = "CopyNinja";
	
	private int[][] findSoulDistanceTable(FieldState fs)
	{
		RowCol size = fs.field_size;
		FieldObject[][] field = fs.field;
		int[][] table = new int[size.row][size.col];
		Deque<RowCol> cur = new ArrayDeque<>(), next = new ArrayDeque<>(), temp;
		for (RowCol rc : fs.souls)
		{
			table[rc.row][rc.col] = 1;
			cur.addFirst(rc);
		}
		int[] add_rows = {1, 0, -1,  0};
		int[] add_cols = {0, 1,  0, -1};
		int distance = 1;
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
		return table;
	}
	
	private TurnState old_state = null;
	private final Ninjutsu  ninjutsu_command = new Ninjutsu(), old_ninjutsu_command = new Ninjutsu();
	private String[]  kunoichi_commands = null, old_kunoichi_commands = null;;
	
	private void initCompute(TurnState ts)
	{
		ninjutsu_command.clear();
		if (kunoichi_commands == null)
		{
			kunoichi_commands = new String[ts.my_state.kunoichis.length];
			old_kunoichi_commands = new String[kunoichi_commands.length];
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
		old_ninjutsu_command.copyFrom(ninjutsu_command);
		for (int i = 0; i < kunoichi_commands.length; i++)
		{
			old_kunoichi_commands[i] = kunoichi_commands[i];
		}
	}
	
	private String makeRootKunoichi(RowCol from, RowCol to)
	{
		StringBuilder root = new StringBuilder();
		char ch = from.row > to.row ? 'U' : 'D';
		for (int i = Math.abs(from.row - to.row); i > 0; i--)
		{
			root.append(ch);
		}
		ch = from.col > to.col ? 'L' : 'R';
		for (int i = Math.abs(from.col - to.col); i > 0; i--)
		{
			root.append(ch);
		}
		return root.toString();
	}
	
	private void computeKunoichRoots(TurnState ts)
	{
		for (int i = 0; i < kunoichi_commands.length; i++)
		{
			kunoichi_commands[i] =
				makeRootKunoichi(
					old_state.rival_state.kunoichis[i].pos,
					ts.rival_state.kunoichis[i].pos
				);
		}
	}
	
	private List<RowCol>
		old_my_rocks = new ArrayList<>(),
		new_my_rocks = new ArrayList<>(),
		old_rival_rocks = new ArrayList<>(),
		new_rival_rocks = new ArrayList<>();
	
	private void findRockChanges(FieldState old_fs, FieldState new_fs, List<RowCol> old_rocks, List<RowCol> new_rocks)
	{
		old_rocks.clear();
		new_rocks.clear();
		Set<RowCol> old_all_rocks = new HashSet<>();
		for (int i = 0; i < old_fs.field_size.row; i++)
		{
			for (int j = 0; j < old_fs.field_size.col; j++)
			{
				if (old_fs.field[i][j] != FieldObject.ROCK) continue;
				old_all_rocks.add(new RowCol(i, j));
			}
		}
		for (int i = 0; i < new_fs.field_size.row; i++)
		{
			for (int j = 0; j < new_fs.field_size.col; j++)
			{
				if (new_fs.field[i][j] != FieldObject.ROCK) continue;
				RowCol rock = new RowCol(i, j);
				if (old_all_rocks.remove(rock) == false)
				{
					new_rocks.add(rock);
				}
			}
		}
		old_rocks.addAll(old_all_rocks);
	}
	
	private void checkRockChanges(TurnState ts)
	{
		findRockChanges(old_state.my_state, ts.my_state, old_my_rocks, new_my_rocks);
		findRockChanges(old_state.rival_state, ts.rival_state, old_rival_rocks, new_rival_rocks);
	}
	
	private RowCol findDropRock(List<RowCol> old_rocks, List<RowCol> new_rocks, RowCol ninjutsu_rock)
	{
		int new_count = new_rocks.size();
		boolean[] flag = new boolean[new_count];
		int idx = new_count * (new_count - 1) / 2;
		if (ninjutsu_rock != null)
		{
			int exidx = new_rocks.indexOf(ninjutsu_rock);
			if (exidx >= 0)
			{
				flag[exidx] = true;
				idx -= exidx;
			}
		}
		for (RowCol rock : old_rocks)
		{
			for (int i = 0; i < new_count; i++)
			{
				if (flag[i]) continue;
				if (rock.distanceTo(new_rocks.get(i)) != 1) continue;
				flag[i] = true;
				idx -= i;
				break;
			}
		}
		return new_rocks.get(idx);
	}
	
	private RowCol findDisappearRock(List<RowCol> old_rocks, List<RowCol> new_rocks, RowCol ninjutsu_rock)
	{
		return findDropRock(new_rocks, old_rocks, ninjutsu_rock);
	}
	
	private void useNinjutsu(TurnState ts, int ninjutsu_id)
	{
		ninjutsu_command.type = NinjutsuTypeUtil.valueOf(ninjutsu_id);
		
		switch (ninjutsu_command.type)
		{
			case SPEED_UP:
				break;
			case DROP_ROCK_MY_FIELD:
				ninjutsu_command.pos = findDropRock(old_rival_rocks, new_rival_rocks,
					old_ninjutsu_command.type == NinjutsuType.DROP_ROCK_RIVAL_FIELD ? old_ninjutsu_command.pos : null);
				break;
			case DROP_ROCK_RIVAL_FIELD:
				ninjutsu_command.pos = findDropRock(old_my_rocks, new_my_rocks,
					old_ninjutsu_command.type == NinjutsuType.DROP_ROCK_MY_FIELD ? old_ninjutsu_command.pos : null);
				break;
			case THUNDERSTROKE_MY_FIELD:
				ninjutsu_command.pos = findDisappearRock(old_rival_rocks, new_rival_rocks,
					old_ninjutsu_command.type == NinjutsuType.THUNDERSTROKE_RIVAL_FIELD ? old_ninjutsu_command.pos : null);
				break;
			case THUNDERSTROKE_RIVAL_FIELD:
				ninjutsu_command.pos = findDisappearRock(old_my_rocks, new_my_rocks,
					old_ninjutsu_command.type == NinjutsuType.THUNDERSTROKE_MY_FIELD ? old_ninjutsu_command.pos : null);
				break;
			case MAKE_MY_DUMMY:
			case MAKE_RIVAL_DUMMY:
			case TURN_CUTTING:
				return;
		}
	}
	
	private void checkNinjutsu(TurnState ts)
	{
		for (int i = 0; i < ts.ninjutsu_kinds_count; i++)
		{
			if (ts.rival_state.ninjutsu_used_counts[i]
					!= old_state.rival_state.ninjutsu_used_counts[i])
			{
				useNinjutsu(ts, i);
				return;
			}
		}
	}
	
	private void computeInner(TurnState ts)
	{
		if (old_state == null) return;
		
		checkRockChanges(ts);
		
		computeKunoichRoots(ts);
		checkNinjutsu(ts);
	}
}

