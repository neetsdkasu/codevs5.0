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
	
	private final Deque<FieldObject[][]> field_backup_stack = new ArrayDeque<>();
	private final Deque<Unit[]> dogs_backup_stack = new ArrayDeque<>();
	private final Deque<List<RowCol>> souls_backup_stack = new ArrayDeque<>();
	
	private NinjutsuType[] emergencies = {
		NinjutsuType.SPEED_UP,
		NinjutsuType.DROP_ROCK_MY_FIELD,
		NinjutsuType.THUNDERSTROKE_MY_FIELD,
		NinjutsuType.MAKE_MY_DUMMY,
		NinjutsuType.TURN_CUTTING
	};
	
	
	private void initCompute(TurnState ts)
	{
		ninjutsu_command.clear();
		if (kunoichi_commands == null)
		{
			kunoichi_commands = new String[ts.my_state.kunoichis.length];
			old_kunoitchi_commands = new String[kunoichi_commands.length];
			
			Arrays.sort(emergencies, (a, b) -> ts.ninjutsu_costs[a.ordinal()] - ts.ninjutsu_costs[b.ordinal()] );
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
	
	private int[][] makeFieldSizeIntTable(FieldState fs)
	{
		return new int[fs.field_size.row][fs.field_size.col];
	}
	
	private int[][] findSoulDistanceTable(FieldState fs)
	{
		FieldObject[][] field = fs.field;
		int[][] table = makeFieldSizeIntTable(fs);
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
				case 'U': p = 0; if (pos.row == 0) return; break;
				case 'R': p = 1; if (pos.col == souls_table[0].length - 1) return; break;
				case 'D': p = 2; if (pos.row == souls_table.length - 1) return; break;
				case 'L': p = 3; if (pos.col == 0) return; break;
			}
			RowCol xx, yy;
			xx = pos.move(add_row[p], add_col[p]);
			if (souls_table[xx.row][xx.col] < 3) return;
			xx = pos.move(add_row[(p + 1) & 3], add_col[(p + 1) & 3]);
			yy =  xx.move(add_row[(p + 2) & 3], add_col[(p + 2) & 3]);
			flag |= souls_table[xx.row][xx.col] > 0 && souls_table[yy.row][yy.col] == 0;
			xx = pos.move(add_row[(p + 3) & 3], add_col[(p + 3) & 3]);
			yy =  xx.move(add_row[(p + 2) & 3], add_col[(p + 2) & 3]);
			flag |= souls_table[xx.row][xx.col] > 0 && souls_table[yy.row][yy.col] == 0;
			xx = pos.move(add_row[p], add_col[p]);
			yy =  xx.move(add_row[p], add_col[p]);
			flag |= yy.row >= 0 && yy.row < souls_table.length
				 && yy.col >= 0 && yy.col < souls_table[0].length
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
	
	private <T> void dropBackupedStack(Deque<T> stack, int n)
	{
		if (stack.size() <= n || n < 1)
		{
			stack.clear();
		}
		else
		{
			for (int i = 0; i < n; i++) stack.removeFirst();
		}
	}
	
	private void backupDogs(FieldState fs)
	{
		Unit[] dogs = Arrays.copyOf(fs.dogs, fs.dogs.length);
		dogs_backup_stack.addFirst(fs.dogs);
		fs.dogs = dogs;
	}
	
	private void restoreDogs(FieldState fs)
	{
		if (dogs_backup_stack.isEmpty()) return;
		fs.dogs = dogs_backup_stack.removeFirst();
	}
	
	private void dropBackupedDogs(int n)
	{
		dropBackupedStack(dogs_backup_stack, n);
	}
	
	private List<RowCol> backupSouls(FieldState fs)
	{
		List<RowCol> souls = new ArrayList<>(fs.souls);
		souls_backup_stack.addFirst(fs.souls);
		fs.souls = souls;
		return souls_backup_stack.peekFirst();
	}
	
	private void restoreSouls(FieldState fs)
	{
		if (souls_backup_stack.isEmpty()) return;
		fs.souls = souls_backup_stack.removeFirst();
	}
	
	private void dropBackupedSouls(int n)
	{
		dropBackupedStack(souls_backup_stack, n);
	}
	
	private void copyField(FieldObject[][] src, FieldObject[][] dest)
	{
		for (int i = 0; i < src.length; i++)
		{
			System.arraycopy(src[i], 0, dest[i], 0, dest[i].length);
		}
	}
	
	private FieldObject[][] copyFieldOf(FieldObject[][] src)
	{
		FieldObject[][] field = new FieldObject[src.length][];
		for (int i = 0; i < field.length; i++)
		{
			field[i] = Arrays.copyOf(src[i], src[i].length);
		}
		return field;
	}
	
	private FieldObject[][] backupField(FieldState fs)
	{
		FieldObject[][] field = copyFieldOf(fs.field);
		field_backup_stack.addFirst(fs.field);
		fs.field = field;
		return field_backup_stack.peekFirst();
	}
	
	private void restoreField(FieldState fs)
	{
		if (field_backup_stack.isEmpty()) return;
		fs.field = field_backup_stack.removeFirst();
	}
	
	private void dropBackupedField(int n)
	{
		dropBackupedStack(field_backup_stack, n);
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
	
	private void labelingFloor(FieldState fs, int[][] labelTable, int label, int row, int col)
	{
		if (fs.field[row][col] != FieldObject.FLOOR) return;
		if (labelTable[row][col] != 0) return;
		labelTable[row][col] = label;
		labelingFloor(fs, labelTable, label, row + 1, col);
		labelingFloor(fs, labelTable, label, row - 1, col);
		labelingFloor(fs, labelTable, label, row, col + 1);
		labelingFloor(fs, labelTable, label, row, col - 1);
	}
	
	private int getLabelingFloor(FieldState fs, int[][] labelTable)
	{
		int label = 0;
		for (int i = 0; i < fs.field_size.row; i++)
		{
			for (int j = 0; j < fs.field_size.col; j++)
			{
				if (labelTable[i][j] == 0)
				{
					label++;
					labelingFloor(fs, labelTable, label, i, j);
				}
			}
		}
		return label;
	}
	
	private int getNinjutsuCost(TurnState ts, NinjutsuType type)
	{
		return ts.ninjutsu_costs[type.ordinal()];
	}
	
	private List<RowCol> getJointPos(FieldState fs)
	{
		List<RowCol> list = new ArrayList<>();
		int[][] labelingFloor = makeFieldSizeIntTable(fs);
		int labels = getLabelingFloor(fs, labelingFloor);
		if (labels < 2) return list;
		for (int i = 1; i < fs.field_size.row - 1; i++)
		{
			for (int j = 1; j < fs.field_size.col - 1; j++)
			{
				if (labelingFloor[i][j] == 0
					&& (
						(labelingFloor[i + 1][j] != 0
							&& labelingFloor[i - 1][j] != 0
							&& labelingFloor[i + 1][j] != labelingFloor[i - 1][j]
						)
						||
						(labelingFloor[i][j + 1] != 0
							&& labelingFloor[i][j - 1] != 0
							&& labelingFloor[i][j + 1] != labelingFloor[i][j - 1])
						))
				{
					list.add(new RowCol(i, j));
				}
			}
		}
		return list;
	}
	
	private List<RowCol> getPartitioningPos(FieldState fs)
	{
		List<RowCol> list = new ArrayList<>();
		int[][] labelingFloor = makeFieldSizeIntTable(fs);
		int labels = getLabelingFloor(fs, labelingFloor);
		
		boolean[] kunoichi_f = new boolean[labels];
		for (Unit kunoichi : fs.kunoichis)
		{
			kunoichi_f[labelingFloor[kunoichi.pos.row][kunoichi.pos.col]] = true;
		}
		
		for (int i = 1; i < fs.field_size.row - 1; i++)
		{
			for (int j = 1; j < fs.field_size.col - 1; j++)
			{
				int f = labelingFloor[i][j];
				if (kunoichi_f[f] == false) continue;
				
				if ((labelingFloor[i + 1][j] == f 
						&& labelingFloor[i - 1][j] == f
						&& (labelingFloor[i][j - 1] == 0 || labelingFloor[i + 1][j - 1] == 0)
						&& (labelingFloor[i][j + 1] == 0 || labelingFloor[i + 1][j + 1] == 0))
					||
					(labelingFloor[i][j + 1] == f 
						&& labelingFloor[i][j - 1] == f
						&& (labelingFloor[i - 1][j] == 0 || labelingFloor[i - 1][j + 1] == 0)
						&& (labelingFloor[i + 1][j] == 0 || labelingFloor[i + 1][j + 1] == 0))
					)
				{
					list.add(new RowCol(i, j));
				}
			}
		}
		return list;
	}
	
	private void computeJoinMyField(TurnState ts)
	{
		if (ninjutsu_command.type != null) return;
		if (ts.my_state.ninja_enegy < getNinjutsuCost(ts, NinjutsuType.THUNDERSTROKE_MY_FIELD)) return;
		
		List<RowCol> list = getJointPos(ts.my_state);
		if (list.isEmpty()) return;
		
		RowCol pos = null;
		int d = -1;
		for (RowCol rc : list)
		{
			int td = 0;
			for (Unit kunoichi : ts.my_state.kunoichis)
			{
				td += rc.distanceTo(kunoichi.pos);
			}
			if (td > d)
			{
				d = td;
				pos = rc;
			}
		}
		if (pos == null) return;
		
		ninjutsu_command.type = NinjutsuType.THUNDERSTROKE_MY_FIELD;
		ninjutsu_command.pos = pos;
		ts.my_state.field[pos.row][pos.col] = FieldObject.FLOOR;
	}
	
	private int getKunoichiDistanceTable(FieldState fs, int[][] table)
	{
		Deque<RowCol> cur = new ArrayDeque<>(), next = new ArrayDeque<>(), temp;
		for (Unit kunoichi : fs.kunoichis)
		{
			cur.addFirst(kunoichi.pos);
			table[kunoichi.pos.row][kunoichi.pos.col] = 1;
		}
		int d = 1;
		int[] add_row = { 1, 0, -1, 0};
		int[] add_col = { 0, 1, 0, -1};
		while (cur.isEmpty() == false)
		{
			d++;
			next.clear();
			for (RowCol pos : cur)
			{
				for (int i = 0; i < 4; i++)
				{
					RowCol rc = pos.move(add_row[i], add_col[i]);
					if (table[rc.row][rc.col] != 0) continue;
					if (fs.field[rc.row][rc.col] != FieldObject.FLOOR) continue;
					table[rc.row][rc.col] = d;
					next.addFirst(rc);
				}
			}
			temp = next; next = cur; cur = temp;
		}
		return d;
	}
	
	private void computePartitioning(TurnState ts, int[][] kunoichiDistanceTable)
	{
		if (ninjutsu_command.type != null) return;
		if (ts.my_state.ninja_enegy <= getNinjutsuCost(ts, NinjutsuType.DROP_ROCK_RIVAL_FIELD)) return;
		
		List<RowCol> list= getPartitioningPos(ts.rival_state);
		if (list.isEmpty()) return;
		
		int[] ds = new int[list.size()];
		Integer[] idx = new Integer[ds.length];
		for (int i = 0; i < list.size(); i++)
		{
			RowCol rc = list.get(i);
			ds[i] = kunoichiDistanceTable[rc.row][rc.col];
			idx[i] = Integer.valueOf(i);
		}
		Arrays.sort(idx, (a, b) -> ds[a.intValue()] - ds[b.intValue()] );
		
		RowCol pos = list.get(idx[0].intValue());
		
		ninjutsu_command.type = NinjutsuType.DROP_ROCK_RIVAL_FIELD;
		ninjutsu_command.pos = pos;
	}
	
	private void computeTurnCut(TurnState ts, FieldObject[][] clean_field, Unit dangerKunoichi, List<RowCol> clean_souls)
	{
		ninjutsu_command.clear();
		ninjutsu_command.type = NinjutsuType.TURN_CUTTING;
		ninjutsu_command.kunoichi_id = dangerKunoichi.id;
		List<Unit> dogs = new ArrayList<>(Arrays.asList(ts.my_state.dogs));
		for (Unit dog : ts.my_state.dogs)
		{
			RowCol df = dangerKunoichi.pos.subtractFrom(dog.pos);
			if ((Math.abs(df.row) | Math.abs(df.col)) == 1)
			{
				dogs.remove(dog);
			}
		}
		copyField(clean_field, ts.my_state.field);
		ts.my_state.souls = new ArrayList<>(clean_souls);
		ts.my_state.dogs = dogs.toArray(new Unit[0]);
		for (int i = 0; i < kunoichi_commands.length; i++)
		{
			kunoichi_commands[i] = "";
		}
		computeInner(ts);
	}
	
	private Unit checkDanger(FieldState fs)
	{
		for (Unit kunoichi : fs.kunoichis)
		{
			List<RowCol> list = parseRoot(kunoichi.pos, kunoichi_commands[kunoichi.id]);
			RowCol pos = list.isEmpty() ? kunoichi.pos : list.get(list.size() - 1);
			if (fs.field[pos.row + 1][pos.col] == FieldObject.DOG
				|| fs.field[pos.row - 1][pos.col] == FieldObject.DOG
				|| fs.field[pos.row][pos.col + 1] == FieldObject.DOG
				|| fs.field[pos.row][pos.col - 1] == FieldObject.DOG)
			{
				return kunoichi;
			}
		}
		return null;
	}
	
	private boolean computeSppedUp(TurnState ts, FieldObject[][] clean_field, List<RowCol> clean_souls)
	{
		String[] temp_cmds = Arrays.copyOf(kunoichi_commands, kunoichi_commands.length);
		for (int i = 0; i < kunoichi_commands.length; i++) kunoichi_commands[i] = "";
		copyField(clean_field, ts.my_state.field);
		ts.my_state.souls = new ArrayList<>(clean_souls);
		
		mappingDogs(ts.my_state);
		
		for (Unit kunoichi : ts.my_state.kunoichis)
		{
			int[][] souls_table = findSoulDistanceTable(ts.my_state);
			computeKunoichiRoot(souls_table, kunoichi, ts.my_state, 3);
		}
		
		Unit dangerKunoichi = checkDanger(ts.my_state);
		
		if (dangerKunoichi == null)
		{
			ninjutsu_command.clear();
			ninjutsu_command.type = NinjutsuType.SPEED_UP;
			return true;
		}
		
		kunoichi_commands = temp_cmds;
		
		return false;
	}
	
	private boolean computeEmergencies(TurnState ts, FieldObject[][] clean_field, List<RowCol> clean_souls)
	{
		Unit dangerKunoichi = checkDanger(ts.my_state);
		if (dangerKunoichi == null) return false;
		
		loop_label:
		for (int i = 0; i < emergencies.length; i++)
		{
			if (ts.my_state.ninja_enegy < getNinjutsuCost(ts, emergencies[i])) break;
			
			switch (emergencies[i])
			{
			case SPEED_UP:
				if (computeSppedUp(ts, clean_field, clean_souls)) break loop_label;
				break;
			case DROP_ROCK_MY_FIELD:
			case THUNDERSTROKE_MY_FIELD:
			case MAKE_MY_DUMMY:
				break;
			case TURN_CUTTING:
				computeTurnCut(ts, clean_field, dangerKunoichi, clean_souls);
				break loop_label;
			}
		}
		
		return ninjutsu_command.exists();
	}
	
	private boolean computeNinjutsu(TurnState ts, FieldObject[][] clean_field, List<RowCol> clean_souls)
	{
		if (ninjutsu_command.type != null) return false;
		
		// backupField (before move Ninja1) (before move Ninja0) (before Mapping dogs)
		
		if (computeEmergencies(ts, clean_field, clean_souls)) return true;
		
		// attack ninjutsu
		return false;
	}

	
	private void computeInner(TurnState ts)
	{
		/*
		int[][] rival_kunoichiDistanceTable = makeFieldSizeIntTable(ts.rival_state);
		getKunoichiDistanceTable(ts.rival_state, rival_kunoichiDistanceTable);
		
		computeJoinMyField(ts);
		
		computePartitioning(ts, rival_kunoichiDistanceTable);
		*/
		
		FieldObject[][] clean_field = backupField(ts.my_state); // save before mapping dogs
		List<RowCol> clean_souls = backupSouls(ts.my_state);
		
		mappingDogs(ts.my_state);
		
		for (Unit kunoichi : ts.my_state.kunoichis)
		{
			backupField(ts.my_state);
			backupSouls(ts.my_state);
			int[][] souls_table = findSoulDistanceTable(ts.my_state);
			computeKunoichiRoot(souls_table, kunoichi, ts.my_state, 2);
		}
		
		backupField(ts.my_state); // save moved Ninjas 
		backupSouls(ts.my_state);
		
		computeNinjutsu(ts, clean_field, clean_souls);
		
		dropBackupedField(3);
		dropBackupedSouls(3);
		
		restoreField(ts.my_state);
		restoreSouls(ts.my_state);
	}
}

