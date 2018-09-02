/**
 * <p>
 * A Point represents the intersection of a certain row and a certain column
 * within a Tray. However, a Point instance is completely independent from any
 * specific Tray instance. A Point simply refers to an abstract location that
 * does not belong to any specific Tray.
 * </p>
 * 
 * <p>
 * A Point instance, once created, is completely <i>immutable</i>. The main
 * purpose of this immutability is to ensure that the structure of other
 * instances that take advantage of this class does not get corrupted by
 * external references to the internal Point instance.
 * </p>
 * 
 * <p>
 * This complete immutability lets this application take advantage of the
 * "pooling" technique. Analogous to Java's String-pooling, Point-pooling keeps
 * a <i>static</i> "pool" of Point instances, each of which can be referred to
 * by <b>getInstance</b>. On this account, the constructor of this class is set
 * to private, so that Point instances could only exist on this <i>static
 * pool</i>. This pooling technique saves memory and reduces the number of calls
 * to the expensive "new" operations. Without pooling, the puzzle solver would
 * create millions of Point instances (verified on the VisualVM Memory
 * Analyzer).
 * </p>
 * 
 * <p>
 * Do not forget to call <b>initPool</b> at least once, the method that
 * initializes the static pool, before calling <b>getInstance</b>. Proper
 * initialization would save a lot of memory; for instance, you do not need a
 * 200X200 pool while working with a 5X5 Tray, because such Points as (100, 100)
 * would never be reached. In this Solver application, the optimal initial size
 * for the pool is (R+1)X(C+1), where R is the maximum number of rows in a Tray
 * and C is the maximum number of columns in the Tray. This is because our
 * solution search process on a Tray takes advantage of the Points right outside
 * the Tray.
 * </p>
 */
public class Point {

	// ///////////////////// static members start ///////////////////////

	/**
	 * <p>
	 * Represents the <i>static pool</i> of the Point instances in this
	 * application. An array structure was used to enable fast access to the
	 * pool. There will be countless accesses to the pool during the solution
	 * process and therefore we need the pool to return a value as quickly as
	 * possible.
	 * </p>
	 * 
	 * <p>
	 * We did consider using a HashMap structure as in the Block pool, because
	 * the Point class, just like the Block class, has a <i>perfect hash
	 * function</i>. However, analysis has shown that the map-pooling
	 * implementation would perform slower than the array structure.
	 * </p>
	 * 
	 * <p>
	 * The first index represents the row index of the Point reference, and the
	 * second represents the column index of the reference.
	 * </p>
	 */
	private static Point[][] pool = null;

	/**
	 * Initializes the <i>static pool</i> of Point instances of this
	 * application, with the given row/column index bounds. If a pool has
	 * already been initialized before, this method will create a new pool and
	 * copy the old pool's Point references into the new pool; in this case, it
	 * is important to ensure that the new pool size is at least as big as the
	 * previous pool size.
	 * 
	 * @param maxRow
	 *            - the maximum possible row index within the new pool
	 * @param maxCol
	 *            - the maximum possible column index within the new pool
	 * @throws IndexOutOfBoundsException
	 *             when either argument is negative
	 * @throws IllegalArgumentException
	 *             when either argument is smaller than the previous
	 *             maxRow/maxCol
	 */
	public static void initPool(int maxRow, int maxCol) {
		if (pool != null && (maxRow < pool.length || maxCol < pool[0].length)) {
			throw new IllegalArgumentException();
		}

		Point[][] tmp = new Point[maxRow][maxCol];

		if (pool == null) { // if this is the first pool
			pool = tmp;
		} else { // if not, need to resize and copy into a new pool
			for (int i = 0; i < pool.length; i++) {
				for (int j = 0; j < pool[0].length; j++) {
					tmp[i][j] = pool[i][j];
				}
			}
			pool = tmp;
		}
	}

	/**
	 * <p>
	 * Returns a Point instance with the given indices from the <i>static Point
	 * pool</i>. This method is the only public interface by which a Point
	 * instance can be obtained. Make sure that <b>initPool</b> has been called
	 * with an appropriate pool size before this method is called. Otherwise
	 * UnsupportedOperationException will be thrown.
	 * </p>
	 * 
	 * <p>
	 * Note that the argument types are <b>int</b>s, whereas the inner data
	 * types of the <b>rowIdx</b> and <b>colIdx</b> instance variables are
	 * <b>short</b>s. In fact, this method takes in <b>int</b> arguments just to
	 * ease the process of obtaining a Point instance with integer arguments.
	 * Any integer argument larger than SHORT.MAX_VALUE will be truncated and
	 * will result in unexpected behavior.
	 * </p>
	 * 
	 * @param rowIdx
	 *            - row index of this Point
	 * @param colIdx
	 *            - column index of this Point
	 * @return a Point instance with the given indices from the <i>Point
	 *         pool</i>
	 * @throws IndexOutOfBoundsException
	 *             when either argument is negative or is bigger than the value
	 *             set by <b>initPool</b>
	 * @throws UnsupportedOperationException
	 *             when the pool has not been initialized
	 */
	public static Point getInstance(int rowIdx, int colIdx) {
		try {
			if (pool[rowIdx][colIdx] == null) {
				pool[rowIdx][colIdx] = new Point((short) rowIdx, (short) colIdx);
			}
			return pool[rowIdx][colIdx];
		} catch (NullPointerException npe) { // caused because pool==null
			throw new UnsupportedOperationException();
		}
	}

	// ///////////////////// static members end ///////////////////////

	// ///////////////////// instance members start ///////////////////////

	/**
	 * Represents the row index of this Point within a Tray. This instance
	 * variable is immutable. The data type 'short' was chosen because the
	 * maximum number of rows in a Tray is being assumed to be 256. Since the
	 * row index of a Point should never change, it makes sense to store this
	 * value in a <b>final</b> instance variable.
	 */
	public final short rowIdx;

	/**
	 * Represents the column index of this Point within a Tray. This instance
	 * variable is immutable. The data type 'short' was chosen because the
	 * maximum number of columns in a Tray is being assumed to be 256. Since the
	 * column index of a Point should never change, it makes sense to store this
	 * value in a <b>final</b> instance variable.
	 */
	public final short colIdx;

	/**
	 * Initializes a Point with the given indices. The given indices are
	 * immutable.
	 * 
	 * @param rowIdx
	 *            - row index of this Point
	 * @param colIdx
	 *            - column index of this Point
	 * @throws IndexOutOfBoundsException
	 *             when either argument is negative
	 */
	private Point(short rowIdx, short colIdx) {
		if (rowIdx < 0 || colIdx < 0) {
			throw new IndexOutOfBoundsException();
		}
		this.rowIdx = rowIdx;
		this.colIdx = colIdx;
	}

	/**
	 * Returns a String representation in the format of "rowIdx colIdx".
	 * 
	 * @return a String representation in the format of "rowIdx colIdx"
	 */
	@Override
	public String toString() {
		return rowIdx + " " + colIdx;
	}

	/**
	 * Returns a hash code for this Point instance. This is a <i>perfect
	 * hash</i>, meaning that any two <i>unequal</i> legal Points within a
	 * 257X257 dimension will give out different hash values.
	 * 
	 * @return a perfect hash code for this Point instance
	 */
	@Override
	public int hashCode() {
		return 257 * rowIdx + colIdx;
	}

	/**
	 * Two Points are considered <i>equal</i> if they have the same exact
	 * coordinates.
	 * 
	 * @param obj
	 *            - the Object to compare to
	 * @return whether the other Object is equal to this Point
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (colIdx != other.colIdx)
			return false;
		if (rowIdx != other.rowIdx)
			return false;
		return true;
	}

	/**
	 * Returns the Point that is directly adjacent to this Point in the
	 * specified direction.
	 * 
	 * @param d
	 *            - the Direction to move
	 * @return the Point directly adjacent to this Point in the specified
	 *         direction
	 * @throws IndexOutOfBoundsException
	 *             when, if d is LEFT, this Point is at the leftmost column, or
	 *             when, if d is UP, this Point is at the top row
	 * @throws NullPointerException
	 *             when the argument is null
	 */
	public Point go(Direction d) {
		if (d == null) {
			throw new NullPointerException();
		}
		switch (d) {
		case UP:
			return Point.getInstance(rowIdx - 1, colIdx);
		case RIGHT:
			return Point.getInstance(rowIdx, colIdx + 1);
		case DOWN:
			return Point.getInstance(rowIdx + 1, colIdx);
		default:
			return Point.getInstance(rowIdx, colIdx - 1);
		}
	}

	// ///////////////////// instance members end ///////////////////////
}
