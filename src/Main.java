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
import java.util.Arrays;
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
	
	private static final int
		KUNOICHI_MOVABLE_NO = 0,
		KUNOICHI_MOVABLE_YES = 1,
		KUNOICHI_MOVABLE_GET_SOUL = 4 | KUNOICHI_MOVABLE_YES,
		KUNOICHI_MOVABLE_PUSH_ROCK = 16 | KUNOICHI_MOVABLE_YES;
	
	private int canKunoichiMove(RowCol from, int add_row, int add_col,  FieldState fs)
	{
		RowCol to = from.move(add_row, add_col);
		switch (fs.field[to.row][to.col])
		{
			case FLOOR:
				for (RowCol soul : fs.souls)
				{
					if (soul.equals(to))
					{
						return KUNOICHI_MOVABLE_GET_SOUL;
					}
				}
				return KUNOICHI_MOVABLE_YES;
			case ROCK: 
				if (fs.field[to.row + add_row][to.col + add_col] == FieldObject.FLOOR)
				{
					for (RowCol soul : fs.souls)
					{
						if (soul.equals(to))
						{
							return KUNOICHI_MOVABLE_GET_SOUL | KUNOICHI_MOVABLE_PUSH_ROCK;
						}
					}
					return KUNOICHI_MOVABLE_PUSH_ROCK;
				}
				break;
		}
		return KUNOICHI_MOVABLE_NO;
	}
	
	private void searchRootKunoichi(int n, RowCol from, RowCol to, FieldState fs, String root, int state, List<String> roots)
	{
		if (from.equals(to))
		{
			String x = root;
			for (int i = 0; i < n; i++)
			{
				x += "N";
			}
			if ((state & KUNOICHI_MOVABLE_GET_SOUL) > 0)
			{
				for (int i = (state >> 2) & 3; i > 0; i--)
				{
					x += "  S";
				}
			}
			if ((state & KUNOICHI_MOVABLE_PUSH_ROCK) > 0)
			{
				for (int i = (state >> 4) & 3; i > 0; i--)
				{
					x += " P";
				}
			}
			roots.add(x);
		}
		if (n == 0) return;
		int movable;
		if ((movable = canKunoichiMove(from, 1, 0, fs)) != KUNOICHI_MOVABLE_NO)
		{
			searchRootKunoichi(n - 1, from.move(1, 0), to, fs, root + "D", state + movable, roots);
		}
		if ((movable = canKunoichiMove(from, -1, 0, fs)) != KUNOICHI_MOVABLE_NO)
		{
			searchRootKunoichi(n - 1, from.move(-1, 0), to, fs, root + "U", state + movable, roots);
		}
		if ((movable = canKunoichiMove(from, 0, 1, fs)) != KUNOICHI_MOVABLE_NO)
		{
			searchRootKunoichi(n - 1, from.move(0, 1), to, fs, root + "R", state + movable, roots);
		}
		if ((movable = canKunoichiMove(from, 0, -1, fs)) != KUNOICHI_MOVABLE_NO)
		{
			searchRootKunoichi(n - 1, from.move(0, -1), to, fs, root + "L", state + movable, roots);
		}
	}
	
	private String makeRootKunoichi(RowCol from, RowCol to, FieldState fs, boolean rock_moved)
	{
		StringBuilder root = new StringBuilder();
		RowCol df = from.subtractFrom(to);
		if (df.col == 0 && Math.abs(df.row) > 1)
		{
			char ch = df.row < 0 ? 'U' : 'D';
			for (int i = Math.abs(df.row); i > 0; i--)
			{
				root.append(ch);
			}
		}
		else if (df.row == 0 && Math.abs(df.col) > 1)
		{
			char ch =df.col < 0 ? 'L' : 'R';
			for (int i = Math.abs(df.col); i > 0; i--)
			{
				root.append(ch);
			}
		}
		else
		{
			List<String> roots = new ArrayList<>();
			searchRootKunoichi(ninjutsu_command.type == NinjutsuType.SPEED_UP ? 3 : 2, from, to, fs, "", 0, roots);
			if (roots.isEmpty() == false)
			{
				String x = "";
				for (String y : roots)
				{
					if (y.length() > x.length())
					{
						if (y.charAt(y.length() - 1) == 'P' && rock_moved == false)
						{
							continue;
						}
						x = y;
					}
				}
				if (x.length() > 1)
				{
					String[] xs = x.split(" ");
					return xs[0];
				}
			}
			char ch = df.row < 0 ? 'U' : 'D';
			for (int i = Math.abs(df.row); i > 0; i--)
			{
				root.append(ch);
			}
			ch = df.col < 0 ? 'L' : 'R';
			for (int i = Math.abs(df.col); i > 0; i--)
			{
				root.append(ch);
			}
		}
		return root.toString();
	}
	
	private void computeKunoichRoots(TurnState ts)
	{
		int rocks = new_rival_rocks.size() - old_rival_rocks.size();
		if (ninjutsu_command.type == NinjutsuType.DROP_ROCK_MY_FIELD)
		{
			rocks -= 1;
		}
		if (old_ninjutsu_command.type == NinjutsuType.DROP_ROCK_RIVAL_FIELD)
		{
			rocks -= 1;
		}
		if (ninjutsu_command.type == NinjutsuType.THUNDERSTROKE_MY_FIELD)
		{
			rocks += 1;
		}
		if (old_ninjutsu_command.type == NinjutsuType.THUNDERSTROKE_RIVAL_FIELD)
		{
			rocks += 1;
		}
		for (int i = 0; i < kunoichi_commands.length; i++)
		{
			kunoichi_commands[i] =
				makeRootKunoichi(
					old_state.rival_state.kunoichis[i].pos,
					ts.rival_state.kunoichis[i].pos,
					old_state.rival_state,
					rocks > 0
				);
		}
	}
	
	private final List<RowCol>
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
	
	private RowCol findDummyAppearance(FieldState old_fs, FieldState new_fs, RowCol ninjutsu_dummy)
	{
		RowCol lt1 = new RowCol(0, 0), rb1 = old_fs.field_size.move(-1, -1),
			lt2 = lt1.move(0, 0), rb2 = rb1.move(0, 0), df;
		int i = 0, j = 0;
		while (i < old_fs.dogs.length && j < new_fs.dogs.length)
		{
			Unit x = old_fs.dogs[i], y = new_fs.dogs[j];
			if (x.id < y.id)
			{
				i++;
			}
			else if (x.id > y.id)
			{
				j++;
			}
			else
			{
				i++; j++;
				df = x.pos.subtractFrom(y.pos);
				if (df.row < 0)
				{
					if (rb1.row > y.pos.row)
					{
						rb1 = new RowCol(y.pos.row, rb1.col);
					}
					else if (y.pos.row < lt1.row && rb2.row > y.pos.row)
					{
						rb2 = new RowCol(y.pos.row, rb2.col);
					}
				}
				else if (df.row > 0)
				{
					if (lt1.row < y.pos.row)
					{
						lt1 = new RowCol(y.pos.row, lt1.col);
					}
					else if (y.pos.row > rb1.row && lt2.row < y.pos.row)
					{
						lt2 = new RowCol(y.pos.row, lt2.col);
					}
				}
				else if (df.col < 0)
				{
					if (rb1.col > y.pos.col)
					{
						rb1 = new RowCol(rb1.row, y.pos.col);
					}
					else if (y.pos.col < lt1.col && rb2.col > y.pos.col)
					{
						rb2 = new RowCol(rb2.row, y.pos.col);
					}
				}
				else if (df.col > 0)
				{
					if (lt1.col < y.pos.col)
					{
						lt1 = new RowCol(lt1.row, y.pos.col);
					}
					else if (y.pos.col > rb1.col && lt2.col < y.pos.col)
					{
						lt2 = new RowCol(lt2.row, y.pos.col);
					}
				}
			}
		}
		df = lt1.subtractFrom(rb1);
		if (df.row < 0 || df.col < 0)
		{
			df = new RowCol(0, 0);
			j = old_fs.kunoichis.length;
			for (i = 0; i < j; i++)
			{
				df = old_fs.kunoichis[i].pos.move(df.row, df.col);
			}
			return new RowCol(df.row / j, df.col / j); 
		}
		if (ninjutsu_dummy == null) return new RowCol((lt1.row+rb1.row)/2,(lt1.col+rb1.col)/2);
		df = lt1.subtractFrom(ninjutsu_dummy);
		if (df.row < 0 || df.col < 0) return new RowCol((lt1.row+rb1.row)/2,(lt1.col+rb1.col)/2);
		df = ninjutsu_dummy.subtractFrom(rb1);
		if (df.row < 0 || df.col < 0) return new RowCol((lt1.row+rb1.row)/2,(lt1.col+rb1.col)/2);
		df = lt2.subtractFrom(rb2);
		if (df.row < 0 || df.col < 0)
		{
			df = new RowCol(0, 0);
			j = old_fs.kunoichis.length;
			for (i = 0; i < j; i++)
			{
				df = old_fs.kunoichis[i].pos.move(df.row, df.col);
			}
			return new RowCol(df.row / j, df.col / j); 
		}
		return new RowCol((lt2.row+rb2.row)/2,(lt2.col+rb2.col)/2);
	}
	
	private int findTurningCuttingKunoichi(FieldState old_fs, FieldState new_fs)
	{
		Set<Unit> dogs = new HashSet<>(Arrays.asList(old_fs.dogs));
		for (Unit dog : new_fs.dogs)
		{
			dogs.remove(dog);
		}
		int[] values = new int[old_fs.kunoichis.length];
		for (int i = 0; i < values.length; i++)
		{
			Unit kunoichi = old_fs.kunoichis[i];
			for (Unit dog : dogs)
			{
				values[i] += dog.pos.distanceTo(kunoichi.pos);
			}
		}
		int id = 0;
		for (int i = 1; i < values.length; i++)
		{
			if (values[i] < values[id])
			{
				id = i;
			}
		}
		return id;
	}
	
	private void useNinjutsu(TurnState ts, int ninjutsu_id)
	{
		ninjutsu_command.type = NinjutsuType.valueOf(ninjutsu_id);
		
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
				ninjutsu_command.pos = findDummyAppearance(old_state.rival_state, ts.rival_state,
					old_ninjutsu_command.type == NinjutsuType.MAKE_RIVAL_DUMMY ? old_ninjutsu_command.pos : null);
				break;
			case MAKE_RIVAL_DUMMY:
				ninjutsu_command.pos = findDummyAppearance(old_state.my_state, ts.my_state,
					old_ninjutsu_command.type == NinjutsuType.MAKE_MY_DUMMY ? old_ninjutsu_command.pos : null);
				break;
			case TURN_CUTTING:
				ninjutsu_command.kunoichi_id = findTurningCuttingKunoichi(old_state.rival_state, ts.rival_state);
				break;
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
		
		checkNinjutsu(ts);
		computeKunoichRoots(ts);
	}
}

