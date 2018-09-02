import java.util.*;

/**
 * <p>
 * A Block represents a rectangle of a fixed width and height, at a certain
 * fixed position within a Tray. This upper left position is completely
 * independent from any specific Tray instance.
 * </p>
 * 
 * <p>
 * A Block instance, once created, is completely <i>immutable</i>. No property
 * of a Block instance will ever differ from what it was at the time of the
 * instance's creation. A Block will always represent the same width and height
 * at the same position. The main purpose of this immutability is to ensure that
 * the structure of other instances that take advantage of this class does not
 * get corrupted by external references to the internal Block instance.
 * Furthermore, fixing the width and the height makes intuitive sense, because a
 * Block never changes its shape throughout a puzzle.
 * </p>
 * 
 * <p>
 * This complete immutability lets this application take advantage of the
 * "pooling" technique. Analogous to Java's String-pooling, Block-pooling keeps
 * a <i>static</i> "pool" of Block instances, each of which can be referred to
 * by <b>getInstance</b>. On this account, the constructor of this class is set
 * to private, so that Block instances could only exist on this <i>static
 * pool</i>. This pooling technique saves memory and reduces the number of calls
 * to the expensive "new" operations. Without pooling, the puzzle solver would
 * create millions of Block instances (verified on the VisualVM Memory
 * Analyzer).
 * </p>
 * 
 * <p>
 * Unlike the Point class, no pool-initializing is required for Block.
 * </p>
 */
public class Block {

	// ///////////////////// static members start ///////////////////////

	/**
	 * <p>
	 * Represents the <i>static pool</i> of the Block instances in this
	 * application. A HashMap structure was used to enable fast access to the
	 * pool. There will be countless accesses to the pool during the solution
	 * process and therefore we need the pool to return a value as quickly as
	 * possible.
	 * </p>
	 * 
	 * <p>
	 * A key in the map represents the <i>perfect hash code</i> of the
	 * corresponding Block value. When asked to return a reference to a specific
	 * Block, the map will compute the Block's hash value, and then find and
	 * return the corresponding reference if one exists. Note that this map
	 * representation only works because the hash function is perfect; no
	 * duplicate hash values could ever occur for distinct Blocks.
	 * </p>
	 * 
	 * <p>
	 * We could not use an array structure as we did for the Point pool, because
	 * the range of the hash function is too large (there are almost 2^31
	 * distinct blocks fitting in a 256X256 Tray) and thus too much memory would
	 * have to be initialized.
	 * </p>
	 */
	private static Map<Integer, Block> pool = new HashMap<Integer, Block>();

	/**
	 * <p>
	 * Returns a Block instance with the given endpoints from the <i>static
	 * Block pool</i>. This method and its overloads are the only public
	 * interfaces by which a Block instance can be obtained.
	 * </p>
	 * 
	 * <p>
	 * Note that the argument types are <b>int</b>s, whereas the inner data
	 * types of Block's instance variables are <b>short</b>s. In fact, this
	 * method takes in <b>int</b> arguments just to ease the process of
	 * obtaining a Point instance with integer arguments. Any integer argument
	 * larger than SHORT.MAX_VALUE-1 will be truncated and will result in
	 * unexpected behavior.
	 * </p>
	 * 
	 * @param row1
	 *            - the row index of the upper left corner of this Block
	 * @param col1
	 *            - the column index of the upper left corner of this Block
	 * @param row2
	 *            - the row index of the lower right corner of this Block
	 * @param col2
	 *            - the column index of the lower right corner of this Block
	 * @return a Block instance with the given endpoints from the <i>Block
	 *         pool</i>
	 * @throws IndexOutOfBoundsException
	 *             when any argument is negative
	 * @throws IllegalArgumentException
	 *             when row1/col1 is bigger than row2/col2 (respectively)
	 */
	public static Block getInstance(int row1, int col1, int row2, int col2) {
		Point upperLeft = Point.getInstance(row1, col1);
		return getInstance(upperLeft, col2 - col1 + 1, row2 - row1 + 1);
	}

	/**
	 * <p>
	 * Returns a Block instance with the given upper left position, width and
	 * height from the <i>static Block pool</i>. This method and its overloads
	 * are the only public interfaces by which a Block instance can be obtained.
	 * </p>
	 * 
	 * <p>
	 * Note that the argument types are <b>int</b>s, whereas the inner data
	 * types of Block's instance variables are <b>short</b>s. In fact, this
	 * method takes in <b>int</b> arguments just to ease the process of
	 * obtaining a Point instance with integer arguments. Any integer argument
	 * larger than SHORT.MAX_VALUE will be truncated and will result in
	 * unexpected behavior.
	 * </p>
	 * 
	 * @param upperLeft
	 *            - the upper left position of this Block
	 * @param width
	 *            - the width of this Block
	 * @param height
	 *            - the height of this Block
	 * @return a Block instance with the given upper left position, width and
	 *         height, from the <i>Block pool</i>
	 * @throws IllegalArgumentException
	 *             when either width or height is not positive
	 * @throws NullPointerException
	 *             when upperLeft is null
	 */
	public static Block getInstance(Point upperLeft, int width, int height) {
		int hash = computePerfectHash(upperLeft.rowIdx, upperLeft.colIdx,
				(short) width, (short) height);

		if (!pool.containsKey(hash)) {
			Block b = new Block(upperLeft, (short) width, (short) height);
			pool.put(hash, b);
		}

		return pool.get(hash);
	}

	/**
	 * <p>
	 * Returns a <i>perfect hash value</i> for the hypothetical Block
	 * corresponding to the given arguments. A <i>perfect</i> hash here means
	 * that any two <i>unequal</i> legal Blocks that fit in a 256X256 Tray will
	 * give out different hash values.
	 * </p>
	 * <p>
	 * It has been mathematically proven that the number of distinct Block
	 * instances fitting in a 256X256 Tray is less than 2^31. Therefore, it is
	 * theoretically possible to map each distinct Block instance to the 2^31
	 * non-zero integers that the data type <b>int</b> offers. This method is a
	 * practical implementation of such a perfect hash function.
	 * </p>
	 * <p>
	 * This private helper method assumes that the given arguments are all in
	 * their appropriate ranges, and thus does not perform any argument
	 * checking. In fact, any conditional operators added to this function will
	 * slow down the performance.
	 * </p>
	 * 
	 * @param r
	 *            - the row index of the upper left position of the hypothetical
	 *            Block
	 * @param c
	 *            - the column index of the upper left position of the
	 *            hypothetical Block
	 * @param w
	 *            - the width of the hypothetical Block
	 * @param h
	 *            - the height of the hypothetical Block
	 * @return a perfect hash representing the hypothetical Block
	 */
	private static int computePerfectHash(short r, short c, short w, short h) {
		int startR = (256 * r - r * (r - 1) / 2) * 32896; // 32896=256*257/2 is
															// sum of (256-j)
															// from j=0 to j=255
		int startRC = startR + (256 - r) * (256 * c - c * (c - 1) / 2);
		int startRCW = startRC + (w - 1) * (256 - r);
		return startRCW + (h - 1);
	}

	// ///////////////////// static members end ///////////////////////

	// ///////////////////// instance members start ///////////////////////

	/**
	 * Represents the upper left position of this Block. This field should be
	 * accessed through <b>getUpperLeft()</b>; this is for stylistic coherence
	 * among getUpperLeft(), getUpperRight(), getLowerLeft() and
	 * getLowerRight(). Since the location of a Block should never change, it
	 * makes sense to store this reference in a <b>final</b> instance variable.
	 */
	private final Point upperLeft;

	/**
	 * Represents the horizontal width of this Block. This instance variable is
	 * immutable. The data type 'short' was chosen because the maximum number of
	 * columns in a Tray is being assumed to be 256. Since the dimension of a
	 * Block never changes, it makes sense to store this value in a <b>final</b>
	 * instance variable.
	 */
	public final short width;

	/**
	 * Represents the vertical height of this Block. This instance variable is
	 * immutable. The data type 'short' was chosen because the maximum number of
	 * rows in a Tray is being assumed to be 256. Since the dimension of a Block
	 * never changes, it makes sense to store this value in a <b>final</b>
	 * instance variable.
	 */
	public final short height;

	/**
	 * Initializes a Block with the given width and height at the given
	 * position. The given position, width and height are immutable.
	 * 
	 * @param upperLeft
	 *            - the upper left position of this Block
	 * @param width
	 *            - the width of this Block
	 * @param height
	 *            - the height of this Block
	 * @throws IllegalArgumentException
	 *             when either width or height is not positive
	 * @throws NullPointerException
	 *             when upperLeft is null
	 */
	private Block(Point upperLeft, short width, short height) {
		if (upperLeft == null) {
			throw new NullPointerException();
		}
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("non-positive width or height");
		}

		this.upperLeft = upperLeft;
		this.width = width;
		this.height = height;
	}

	/**
	 * Returns the upper left endpoint.
	 * 
	 * @return the upper left endpoint
	 */
	public Point getUpperLeft() {
		return upperLeft;
	}

	/**
	 * Returns the lower right endpoint.
	 * 
	 * @return the lower right endpoint
	 */
	public Point getLowerRight() {
		return Point.getInstance(upperLeft.rowIdx + height - 1,
				upperLeft.colIdx + width - 1);
	}

	/**
	 * Returns the lower left endpoint.
	 * 
	 * @return the lower left endpoint
	 */
	public Point getLowerLeft() {
		return Point.getInstance(upperLeft.rowIdx + height - 1,
				upperLeft.colIdx);
	}

	/**
	 * Returns the upper right endpoint.
	 * 
	 * @return the upper right endpoint
	 */
	public Point getUpperRight() {
		return Point
				.getInstance(upperLeft.rowIdx, upperLeft.colIdx + width - 1);
	}

	/**
	 * Returns a String representation in the format of
	 * "ULrow ULcol LRrow LRcol".
	 * 
	 * @return String representation in the format of "ULrow ULcol LRrow LRcol"
	 */
	@Override
	public String toString() {
		return getUpperLeft().toString() + " " + getLowerRight().toString();
	}

	/**
	 * Returns a hash code for this Block instance. This is a <i>perfect
	 * hash</i>, meaning that any two <i>unequal</i> legal Blocks that fit in a
	 * 256X256 Tray will give out different hash values.
	 * 
	 * @return a perfect hash value for this Block instance
	 */
	@Override
	public int hashCode() {
		return computePerfectHash(upperLeft.rowIdx, upperLeft.colIdx, width,
				height);
	}

	/**
	 * Two Blocks are considered equal if they have the same exact endpoints.
	 * 
	 * @param obj
	 *            - the Object to compare to
	 * @return whether the other Object has the same exact endpoints as this
	 *         Block
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Block other = (Block) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		if (!upperLeft.equals(other.upperLeft))
			return false;
		return true;
	}

	/**
	 * Checks whether there are any common Points shared by this Block and the
	 * given Block.
	 * 
	 * @param o
	 *            - the other Block
	 * @return whether there are any common Points between this Block and the
	 *         given Block
	 * @throws NullPointerException
	 *             when the argument is null
	 */
	public boolean collidesWith(Block o) {
		int thisR1 = this.getUpperLeft().rowIdx;
		int thisR2 = this.getLowerRight().rowIdx;
		int thisC1 = this.getUpperLeft().colIdx;
		int thisC2 = this.getLowerRight().colIdx;

		int oR1 = o.getUpperLeft().rowIdx;
		int oR2 = o.getLowerRight().rowIdx;
		int oC1 = o.getUpperLeft().colIdx;
		int oC2 = o.getLowerRight().colIdx;

		int maxR1 = Math.max(thisR1, oR1);
		int minR2 = Math.min(thisR2, oR2);
		int maxC1 = Math.max(thisC1, oC1);
		int minC2 = Math.min(thisC2, oC2);

		// this is the logical condition for collision
		return minR2 >= maxR1 && minC2 >= maxC1;
	}

	/**
	 * Returns the Block instance which would result from moving this Block to
	 * the given position.
	 * 
	 * @param newUpperLeft
	 *            - the new upper left position of the Block
	 * @return the the Block instance that would result from moving this Block
	 *         to the given position
	 * @throws NullPointerException
	 *             when the argument is null
	 */
	public Block relocate(Point newUpperLeft) {
		return Block.getInstance(newUpperLeft, width, height);
	}

	/**
	 * Checks whether the given Point is contained within the range occupied by
	 * this Block.
	 * 
	 * @param p
	 *            - the Point to check
	 * @return whether the given Point is contained within this Block
	 * @throws NullPointerException
	 *             when the argument is null
	 */
	public boolean containsPoint(Point p) {
		int r = p.rowIdx;
		int c = p.colIdx;

		// checks how far away p is from this.upperLeft
		int offR = r - upperLeft.rowIdx;
		int offC = c - upperLeft.colIdx;

		// this is the logical condition for containing
		return 0 <= offR && offR < height && 0 <= offC && offC < width;
	}

	// ///////////////////// instance members end ///////////////////////
}
