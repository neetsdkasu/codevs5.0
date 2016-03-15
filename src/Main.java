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
import java.util.Collections;
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
		fs.dogs        = new ArrayList<>(Arrays.asList(scanUnits()));
		fs.souls       = new ArrayList<>(Arrays.asList(scanRowCols()));
		
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
	public RowCol move(RowCol addrc)
	{
		return move(addrc.row, addrc.col);
	}
	public int distanceTo(RowCol rc)
	{
		return Math.abs(row - rc.row) + Math.abs(col - rc.col);
	}
	public RowCol subtractFrom(RowCol rc)
	{
		return rc.move(-row, -col);
	}
	
	public static final RowCol DOWN  = new RowCol( 1,  0);
	public static final RowCol UP    = new RowCol(-1,  0);
	public static final RowCol LEFT  = new RowCol( 0, -1);
	public static final RowCol RIGHT = new RowCol( 0,  1);
	public static final RowCol STAY  = new RowCol( 0,  0);
}

class Unit
{
	public final int id;
	public final RowCol pos;
	
	public Unit(int id, RowCol pos) { this.id = id; this.pos = pos; }
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
	public List<Unit>      dogs;
	public List<RowCol>    souls;
	public int[]           ninjutsu_used_counts;
	public List<RowCol>    dummies = new ArrayList<>();
	public int             new_dogs = 0;
	public int             dog_last_id = 0;
	
	public int getKunoichiCount() { return kunoichis.length; }
	
	public boolean isAlive()
	{
		for (Unit kunoichi : kunoichis)
		{
			for (Unit dog : dogs)
			{
				if (dog.pos.equals(kunoichi.pos)) return false;
			}
		}
		return true;
	}
	
	public FieldState copy()
	{
		FieldState fs = new FieldState();
		fs.ninja_enegy = ninja_enegy;
		fs.field_size = field_size;
		
		fs.field = Arrays.copyOf(field, field.length);
		for (int i = 0; i < fs.field.length; i++) fs.field[i] = Arrays.copyOf(field[i], field[i].length);
		fs.kunoichis = Arrays.copyOf(kunoichis, kunoichis.length);
		fs.dogs = new ArrayList<>(dogs);
		fs.souls = new ArrayList<>(souls);
		fs.ninjutsu_used_counts = Arrays.copyOf(ninjutsu_used_counts, ninjutsu_used_counts.length);
		fs.dog_last_id = dog_last_id;
		return fs;
	}
	
	private int move(int id, List<RowCol> moving)
	{
		int dog_cnt = 0;
		loop_label:
		for (RowCol addrc : moving)
		{
			RowCol pos = kunoichis[id].pos.move(addrc);
			switch (field[pos.row][pos.col])
			{
			case FLOOR:
				break;
			case ROCK:
				RowCol away = pos.move(addrc);
				if (field[away.row][away.col] != FieldObject.FLOOR) continue loop_label;
				if (kunoichis[1 - id].pos.equals(away)) continue loop_label;
				for (Unit dog : dogs) if (dog.pos.equals(pos)) continue loop_label;
				field[away.row][away.col] = FieldObject.ROCK;
				field[pos.row][pos.col] = FieldObject.FLOOR;
				break;
			default:
				continue loop_label;
			}
			kunoichis[id] = new Unit(id, pos);
			if (souls.remove(pos))
			{
				ninja_enegy++;
				dog_cnt++;
			}
		}
		return dog_cnt;
	}
	
	private void checkDummies()
	{
		for (RowCol dummy : dummies.toArray(new RowCol[0]))
		{
			if (field[dummy.row][dummy.col] == FieldObject.ROCK)
			{
				dummies.remove(dummy);
			}
		}
	}
	
	private int getDistanceTable(int[][] table)
	{
		checkDummies();
		Set<RowCol> cur = new HashSet<>(dummies), nex = new HashSet<>(), temp;
		if (cur.isEmpty()) for (Unit kunoichi : kunoichis) cur.add(kunoichi.pos);
		int d = 1;
		while (cur.isEmpty() == false)
		{
			nex.clear();
			for (RowCol pos : cur)
			{
				RowCol df = pos.subtractFrom(field_size);
				if (df.row <= 0 || df.col <= 0) continue;
				if (pos.row <= 0 || pos.col <= 0) continue;
				if (table[pos.row][pos.col] != 0) continue;
				if (field[pos.row][pos.col] != FieldObject.FLOOR) continue;
				table[pos.row][pos.col] = d;
				nex.add(pos.move(RowCol.LEFT));
				nex.add(pos.move(RowCol.RIGHT));
				nex.add(pos.move(RowCol.UP));
				nex.add(pos.move(RowCol.DOWN));
			}
			temp = cur; cur = nex; nex = temp;
			d++;
		}
		return d;
	}
	
	public void moveAndRaiseDogs()
	{
		int[][] table = new int[field_size.row][field_size.col];
		int d = getDistanceTable(table);
		List<List<Unit>> list = new ArrayList<>();
		List<Unit> new_dog_list = new ArrayList<>();
		for (int i = 0; i < d; i++) list.add(new ArrayList<>());
		for (Unit dog : dogs)
		{
			list.get(table[dog.pos.row][dog.pos.col]).add(dog);
		}
		for (int i = 0; i < d; i++)
		{
			List<Unit> temp = list.get(i);
			if (i < 2)
			{
				new_dog_list.addAll(temp);
				continue;
			}
			temp.sort( (a, b) -> a.id - b.id );
			for (Unit dog : temp)
			{
				RowCol to_pos = dog.pos;
				for (RowCol addrc : AI.elems)
				{
					RowCol pos = dog.pos.move(addrc);
					if (table[pos.row][pos.col] != d - 1) continue;
					table[pos.row][pos.col] *= -1;
					to_pos = pos;
					break;
				}
				new_dog_list.add(new Unit(dog.id, to_pos));
			}
		}
		new_dog_list.sort( (a, b) -> a.id - b.id );
		loop_label:
		while (new_dogs > 0)
		{
			d--;
			if (d < 1)
			{
				new_dogs = 0;
				break;
			}
			for (int i = 0; i < field_size.row; i++)
			{
				for (int j = 0; j < field_size.col; j++)
				{
					if (table[i][j] == d)
					{
						new_dog_list.add(new Unit(dog_last_id, new RowCol(i, j)));
						dog_last_id++;
						new_dogs--;
						if (new_dogs == 0) break loop_label;
					}
				}
			}
		}
	}
	
	public int move(List<List<RowCol>> moving)
	{
		int dog_cnt = 0;
		for (int i = 0; i < 2; i++) dog_cnt += move(i, moving.get(i));
		return dog_cnt;
	}
	
	public int doAttackNinjutsu(TurnState ts, FieldState fs, Ninjutsu ninjutsu)
	{
		if (ninjutsu.exists() == false) return 0;
		if (fs.ninja_enegy < ts.ninjutsu_costs[ninjutsu.type.ordinal()]) return -1;
		int res = 0;
		switch (ninjutsu.type)
		{
		case DROP_ROCK_RIVAL_FIELD:
			if (field[ninjutsu.pos.row][ninjutsu.pos.col] != FieldObject.FLOOR)  { res = -1; break; }
			for (Unit kunoichi : kunoichis) if (kunoichi.pos.equals(ninjutsu.pos)) { res = -1; break; }
			for (Unit dog : dogs) if (dog.pos.equals(ninjutsu.pos)) { res = -1; break; }
			for (RowCol soul : souls) if (soul.equals(ninjutsu.pos)) { res = -1; break; }
			field[ninjutsu.pos.row][ninjutsu.pos.col] = FieldObject.ROCK;
			break;
		case THUNDERSTROKE_RIVAL_FIELD:
			if (field[ninjutsu.pos.row][ninjutsu.pos.col] != FieldObject.ROCK) { res = -1; break; }
			field[ninjutsu.pos.row][ninjutsu.pos.col] = FieldObject.FLOOR;
			break;
		case MAKE_RIVAL_DUMMY:
			if (field[ninjutsu.pos.row][ninjutsu.pos.col] == FieldObject.ROCK) { res = -1; break; }
			if (field[ninjutsu.pos.row][ninjutsu.pos.col] == FieldObject.WALL)  { res = -1; break; }
			dummies.add(ninjutsu.pos);
			break;
		default:
			return 0;
		}
		fs.ninja_enegy -= ts.ninjutsu_costs[ninjutsu.type.ordinal()];
		return res;
	}
	
	public int doGuardNinjutsu(TurnState ts, FieldState fs, Ninjutsu ninjutsu)
	{
		if (ninjutsu.exists() == false) return 0;
		if (ninja_enegy < ts.ninjutsu_costs[ninjutsu.type.ordinal()]) return -1;
		int res = 0;
		switch (ninjutsu.type)
		{
		case SPEED_UP:
			break;
		case DROP_ROCK_MY_FIELD:
			if (field[ninjutsu.pos.row][ninjutsu.pos.col] != FieldObject.FLOOR) { res = -1; break; }
			for (Unit kunoichi : kunoichis) if (kunoichi.pos.equals(ninjutsu.pos)) { res = -1; break; }
			for (Unit dog : dogs) if (dog.pos.equals(ninjutsu.pos)) { res = -1; break; }
			for (RowCol soul : souls) if (soul.equals(ninjutsu.pos)) { res = -1; break; }
			field[ninjutsu.pos.row][ninjutsu.pos.col] = FieldObject.ROCK;
			break;
		case THUNDERSTROKE_MY_FIELD:
			if (field[ninjutsu.pos.row][ninjutsu.pos.col] != FieldObject.ROCK) { res = -1; break; }
			field[ninjutsu.pos.row][ninjutsu.pos.col] = FieldObject.FLOOR;
			break;
		case MAKE_MY_DUMMY:
			if (field[ninjutsu.pos.row][ninjutsu.pos.col] == FieldObject.ROCK) { res = -1; break; }
			if (field[ninjutsu.pos.row][ninjutsu.pos.col] == FieldObject.WALL) { res = -1; break; }
			dummies.add(ninjutsu.pos);
			break;
		case TURN_CUTTING:
			for (Unit dog : dogs.toArray(new Unit[0]))
			{
				RowCol df = dog.pos.subtractFrom(kunoichis[ninjutsu.kunoichi_id].pos);
				if (Math.max(Math.abs(df.row), Math.abs(df.col)) > 1) continue;
				dogs.remove(dog);
				fs.new_dogs++;
			}	
			break;
		default:
			return 0;
		}
		ninja_enegy -= ts.ninjutsu_costs[ninjutsu.type.ordinal()];
		return res;
	}
	
	public int sumDistKunoichi(FieldState fs)
	{
		int sum = 0;
		for (int i = 0; i < 2; i++) sum += kunoichis[i].pos.distanceTo(fs.kunoichis[i].pos);
		return sum;
	}
	
	public int sumDistSouls()
	{
		int sum = 0;
		for (Unit kunoichi: kunoichis)
		{
			for (RowCol soul : souls)
			{
				sum += soul.distanceTo(kunoichi.pos);
			}
		}
		return sum;
	}
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
	
	private static final List<List<RowCol>> failure = Collections.nCopies(2, Collections.emptyList());
	
	public TurnState next(List<List<RowCol>> my_next, Ninjutsu my_ninjutsu, List<List<RowCol>> rival_next, Ninjutsu rival_ninjutsu)
	{
		TurnState ts = new TurnState();
		ts.ninjutsu_kinds_count = ninjutsu_kinds_count;
		ts.ninjutsu_costs = ninjutsu_costs;
		ts.my_state = my_state.copy();
		ts.rival_state = rival_state.copy();
		
		if (ts.my_state.doAttackNinjutsu(this, ts.rival_state, rival_ninjutsu) < 0)
		{
			rival_next = failure;
		}
		if (ts.rival_state.doAttackNinjutsu(this, ts.my_state, my_ninjutsu) < 0)
		{
			my_next = failure;
		}
		if (ts.my_state.doGuardNinjutsu(this, ts.rival_state, my_ninjutsu) < 0)
		{
			my_next = failure;
		}
		if (ts.rival_state.doGuardNinjutsu(this, ts.my_state, rival_ninjutsu) < 0)
		{
			rival_next = failure;
		}
		
		ts.rival_state.new_dogs += ts.my_state.move(my_next);
		ts.my_state.new_dogs += ts.rival_state.move(rival_next);
		
		ts.my_state.moveAndRaiseDogs();
		ts.rival_state.moveAndRaiseDogs();
		
		return ts;
	}
}

class AI
{
	public static final String NAME = "ThinkAllThings";

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
	
	private final List<List<RowCol>> movements = new ArrayList<>();
	private final List<List<RowCol>> movements_3 = new ArrayList<>();
	public static final List<RowCol> elems = Arrays.asList( RowCol.UP, RowCol.LEFT, RowCol.RIGHT, RowCol.DOWN );
	
	{
		for (RowCol p1 : elems)
		{
			for (RowCol p2 : elems)
			{
				movements.add(new ArrayList<>(Arrays.asList(p1, p2)));
			}
		}
		for (RowCol p3 : elems)
		{
			for (List<RowCol> list : movements)
			{
				List<RowCol> temp = new ArrayList<>(list);
				temp.add(p3);
				movements_3.add(temp);
			}
		}
		for (RowCol e : elems)
		{
			movements.add(new ArrayList<>(Collections.singletonList(e)));
		}
		movements.add(new ArrayList<>(Collections.singletonList(RowCol.STAY)));
		movements_3.addAll(movements);
		
		movements.sort( (a, b) -> a.size() - b.size() );
		movements_3.sort( (a, b) -> a.size() - b.size() );
	}
	
	private void computeInner(TurnState ts)
	{
		Ninjutsu empty = new Ninjutsu();
		TurnState sel_ts = ts;
		List<List<RowCol>> sel_move = Collections.nCopies(2, Collections.singletonList(RowCol.STAY));
		int sel_sum_soul = ts.my_state.sumDistSouls();
		int sel_sum_kuno = 0;
		
		outer_loop_label:
		for (List<RowCol> my_move0 : movements) for (List<RowCol> my_move1 : movements)
		{
			//for (List<RowCol> rival_move0 : movements) for (List<RowCol> rival_move1 : movements)
			{
				List<List<RowCol>>
					my_move = Arrays.asList(my_move0, my_move1);
					//rival_move = Arrays.asList(rival_move0, rival_move1);
				TurnState next_ts = ts.next(my_move, empty, my_move, empty);
				
				if (next_ts.rival_state.isAlive() == false)
				{
					sel_ts = next_ts;
					sel_move = my_move;
					if (next_ts.my_state.isAlive())
					{
						break outer_loop_label;
					}
				}
				else if (next_ts.my_state.isAlive())
				{
					if (next_ts.my_state.ninja_enegy >= ts.my_state.ninja_enegy)
					{
						int sum_soul = next_ts.my_state.sumDistSouls();
						int sum_kuno = ts.my_state.sumDistKunoichi(next_ts.my_state);
						if (sum_kuno * 100 / sum_soul >= sel_sum_kuno)
						{
							sel_ts = next_ts;
							sel_move = my_move;
							//sel_sum_soul = sum_soul;
							sel_sum_kuno = sum_kuno * 100 / sum_soul;
						}
					}
				}
			}
		}
		
		ninjutsu_command.copyFrom(empty);
		for (int i = 0; i < 2; i++)
		{
			kunoichi_commands[i] = "";
			for (RowCol addrc : sel_move.get(i))
			{
				if (addrc.equals(RowCol.LEFT)) kunoichi_commands[i] += "L";
				if (addrc.equals(RowCol.RIGHT)) kunoichi_commands[i] += "R";
				if (addrc.equals(RowCol.UP)) kunoichi_commands[i] += "U";
				if (addrc.equals(RowCol.DOWN)) kunoichi_commands[i] += "D";
				if (addrc.equals(RowCol.STAY)) kunoichi_commands[i] += "N";
			}
		}
	}
}

