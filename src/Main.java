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
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main
{
	public static void main(String[] args) throws Exception
	{

		StateScanner scanner = new StateScanner(System.in);
		
		
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
			
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println("# " + loop);
			System.out.println("my_state -------------------");
			ts.my_state.mappingItems().printField();
			
			System.out.println("rival_state ----------------");
			ts.rival_state.mappingItems().printField();
			
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
	WALL, FLOOR, ROCK, DOG, KUNOICHI;
	
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
	
	public FieldState mappingItems()
	{
		for (Unit kunoichi : kunoichis)
		{
			field[kunoichi.pos.row][kunoichi.pos.col] = FieldObject.KUNOICHI;
		}
		for (Unit dog : dogs)
		{
			field[dog.pos.row][dog.pos.col] = FieldObject.DOG;
		}
		return this;
	}
	
	public void printField()
	{
		List<RowCol> list = Arrays.asList(souls);
		for (int i = 0; i < field_size.row; i++)
		{
			String ch = "";
			for (int j = 0; j < field_size.col; j++)
			{
				RowCol pos = new RowCol(i, j);
				switch (field[i][j])
				{
				case WALL: ch += "#"; break;
				case ROCK: ch += list.contains(pos) ? "8" : "o"; break;
				case FLOOR: ch += list.contains(pos) ? "S" : "_"; break;
				case KUNOICHI: ch += pos.equals(kunoichis[0].pos) ? "0" : "1"; break;
				case DOG: ch += list.contains(pos) ? "$" : "d"; break;
				}
			}
			System.out.println(ch);
		}
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
}
