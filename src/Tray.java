import java.util.*;

/**
 * <p>
 * A Tray represents a container of Blocks with a fixed rectangular dimension.
 * Blocks are added to a Tray <i>only</i> when the Tray is being initialized.
 * Once a Tray is created, no Blocks can be added to or removed from the Tray.
 * However, a Block already in a Tray may be moved around. A single move only
 * allows <i>at most</i> one displacement in a specific direction. This
 * restriction on moves was enforced because we wanted the search process to
 * attempt every possible Tray configuration until it reaches a solution. In the
 * sense that Blocks can be moved around, a Tray instance is not immutable,
 * unlike Point and Block.
 * </p>
 * 
 * <p>
 * Since a Tray instance is not immutable, we made Tray a <i>cloneable</i>
 * class. A Tray can be cloned using the public method <b>clone()</b>. A special
 * private constructor has been provided to ensure that this cloning process
 * takes place quickly. The usual public constructor of Tray is very expensive
 * in terms of runtime efficiency, because of having to set up a set of blank
 * Points (which is discussed below).
 * </p>
 * 
 * <p>
 * A Tray implicitly makes use of two distinct data structures.
 * <ul>
 * <li>The first data structure is an <i>ArrayList</i> of Blocks. We chose to
 * use ArrayList because our puzzle solving process "looks up" Blocks a lot,
 * when it moves them around. So we needed an O(1) process for the following
 * task: given a Block index, find the Block in a Tray as quickly as possible.
 * ArrayList was the way to go.</li>
 * <li>The second data structure is a <i>HashSet</i> of the blank Points within
 * a Tray. Such a structure is necessary when our search process performs
 * <i>blank-wise</i> search (refer to Solver.java for information on blank-wise
 * search). An important aspect about this collection of blank Points is that
 * there will be a lot of searches, additions and deletions performed on this
 * collection. When a Block gets moved, some of the old blank spots will now be
 * occupied, and there will be as many new blank spots. Hence, we needed a
 * structure that can search, add and delete in O(1) time. HashSet was the way
 * to go. ArrayList does not work in this case; although search can be performed
 * in O(1), additions and deletions are O(N).</li>
 * </ul>
 * </p>
 * <p>
 * If you want Tray to perform an invariants check after each call to the
 * constructor or the <b>moveBlock</b> method (which are the only "setter"
 * methods in Tray), simply set the static variable <b>Tray.checkInvariants</b>
 * to true. It is false by default. The checked invariants are listed in the
 * documentation of the private method <b>isOK()</b>. If any of these invariants
 * are violated after a call to either method, an <b>IllegalStateException</b>
 * will be thrown right away.
 * </p>
 * 
 */
public class Tray implements Cloneable {

	// ///////////////////// static members start //////////////////////

	/**
	 * If you want Tray to perform an invariants check after each call to the
	 * constructor or the <b>moveBlock</b> method (which are the only "setter"
	 * methods in Tray), set this variable to true. It is false by default.
	 */
	public static boolean checkInvariants = false;

	// ///////////////////// static members end //////////////////////

	// ///////////////////// instance members start //////////////////////

	/**
	 * Represents the number of rows in this tray. This instance variable is
	 * immutable. The data type 'short' was chosen because the maximum number of
	 * rows is being assumed to be 256. Since the dimension of a Tray never
	 * changes, it makes sense to store this value in a <b>final</b> instance
	 * variable.
	 */
	public final short rowSize;

	/**
	 * Represents the number of columns in this tray. This instance variable is
	 * immutable. The data type 'short' was chosen because the maximum number of
	 * columns is being assumed to be 256. Since the dimension of a Tray never
	 * changes, it makes sense to store this value in a <b>final</b> instance
	 * variable.
	 */
	public final short colSize;

	/**
	 * Represents the list of Blocks that this Tray configuration currently
	 * contains. This list is initialized by <i>deep-copying</i> a user-supplied
	 * collection of Blocks (one of the arguments in the constructor). The
	 * reference to the list is final, but the contents within the list may be
	 * changed.
	 * 
	 */
	private final ArrayList<Block> blocksList;

	/**
	 * Represents the number of Blocks in this Tray. Since the number of Blocks
	 * within a Tray never changes, it makes sense to store this value in a
	 * <b>final</b> instance variable, for the sake of fast and efficient access
	 * (especially compared to calling <b>blocksList.size()</b> every time).
	 */
	public final int numBlocks;

	/**
	 * Represents the set of blank Points that this Tray configuration currently
	 * contains. This set is automatically updated every time a Block gets
	 * moved. The reference to the set is final, but the contents within the set
	 * may be changed.
	 */
	private final HashSet<Point> blanksSet;

	/**
	 * Represents the number of blank spots in this Tray. Since the number of
	 * blank spots within a Tray never changes, it makes sense to store this
	 * value in a <b>final</b> instance variable, for the sake of fast and
	 * efficient access (especially compared to calling <b>blanksSet.size()</b>
	 * every time).
	 */
	public final int numBlanks;

	/**
	 * <p>
	 * Initializes an empty tray with the given dimensions and blocks,
	 * <i>without checking whether there are any invalid or conflicting
	 * blocks</i>. As it is being assumed that only valid Blocks are being
	 * given, there is no need for such a check. <b>rowSize</b> and
	 * <b>colSize</b> are immutable. The Blocks collection will be deep-copied,
	 * so that external references to the given argument cannot corrupt the data
	 * structure of this Tray.
	 * </p>
	 * 
	 * <p>
	 * Note that the argument types are <b>int</b>s, whereas the inner data
	 * types of the <b>rowSize</b> and <b>colSize</b> instance variables are
	 * <b>short</b>s. In fact, this method takes in <b>int</b> arguments just to
	 * ease the process of obtaining a Point instance with integer arguments.
	 * Any integer argument larger than SHORT.MAX_VALUE will be truncated and
	 * will result in unexpected behavior.
	 * </p>
	 * 
	 * @param rowSize
	 *            - the number of rows in this tray
	 * @param colSize
	 *            - the number of columns in this tray
	 * @param blocks
	 *            - the collection of Blocks to add to this tray
	 * @throws IllegalArgumentException
	 *             when either dimension is not positive
	 * @throws IllegalStateException
	 *             when an invariant of this Tray has been violated after a call
	 *             to this constructor (only when Tray.checkInvariants==true)
	 * @throws NullPointerException
	 *             when blocks is null
	 */
	public Tray(int rowSize, int colSize, Collection<Block> blocks) {
		if (rowSize <= 0 || colSize <= 0) {
			throw new IllegalArgumentException(
					"row size or col size is not positive");
		}
		this.rowSize = (short) rowSize;
		this.colSize = (short) colSize;

		int numOccupied = 0;

		this.blanksSet = new HashSet<Point>();
		// initializing blanksSet to contain all Points,
		// because no blocks have been added yet
		for (int i = 0; i < this.rowSize; i++) {
			Point leftEnd = Point.getInstance(i, 0);
			Point rightEnd = Point.getInstance(i, this.colSize - 1);
			markRegion(false, leftEnd, rightEnd);
		}

		// the blocks list will be a DEEP COPY of the given collection
		this.blocksList = new ArrayList<Block>(blocks);

		// updating blanksSet to not include the occupied Points
		for (Block b : this.blocksList) {
			numOccupied += b.width * b.height;
			markRegion(true, b.getUpperLeft(), b.getLowerRight());
		}
		this.numBlanks = this.rowSize * this.colSize - numOccupied;
		this.numBlocks = this.blocksList.size();

		if (checkInvariants) {
			isOK();
		}
	}

	/**
	 * <p>
	 * <i>This method is to be used in <b>clone()</b> only</i>. This method
	 * simply deep-copies the given Tray into this Tray, without manually
	 * setting up blanksSet. By doing so, this constructor saves a lot of time;
	 * on a big Tray, setting up blanksSet from scratch is a very expensive
	 * process.
	 * </p>
	 * <p>
	 * This operation runs in O(A+B) time, where A is the number of Blocks and B
	 * is the number of blanks in this Tray.
	 * </p>
	 * 
	 * @param copy
	 *            - the Tray to clone
	 */
	private Tray(Tray copy) {
		this.rowSize = copy.rowSize;
		this.colSize = copy.colSize;
		this.numBlocks = copy.numBlocks;
		this.numBlanks = copy.numBlanks;
		this.blocksList = new ArrayList<Block>(copy.blocksList);
		this.blanksSet = new HashSet<Point>(copy.blanksSet);
	}

	/**
	 * Checks whether the given Point will be valid within this tray. A Point is
	 * considered invalid when it is out of bounds.
	 * 
	 * @param p
	 *            - the Point to be checked
	 * @return whether the given Point will be valid within this tray
	 * @throws NullPointerException
	 *             when the given argument is null
	 */
	private boolean isValidPoint(Point p) {
		return p.rowIdx < rowSize && p.colIdx < colSize;
	}

	/**
	 * Returns a String representation of this Tray in the format of: <br/>
	 * <br/>
	 * rowsize colsize <br/>
	 * Block1's String rep <br/>
	 * Block2's String rep <br/>
	 * Block3's String rep... <br/>
	 * 
	 * @return a String representation of this Tray
	 */
	@Override
	public String toString() {
		String rtn = rowSize + " " + colSize + "\n";
		for (Block b : blocksList) {
			rtn += b.toString() + "\n";
		}
		return rtn;
	}

	/**
	 * Returns a hash code for this Tray instance. This hash is <i>not</i>
	 * perfect.
	 * 
	 * @return a hash code for this Tray instance
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + colSize;
		result = prime * result + rowSize;
		Set<Block> blockSet = new HashSet<Block>(blocksList);
		result = prime * result + blockSet.hashCode();
		return result;
	}

	/**
	 * Two Trays are considered equal when the Trays have the same dimensions,
	 * contain the same number of blocks, and contains the same size blocks at
	 * the same positions.
	 * 
	 * @return whether the other Object is equal to this Tray
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tray other = (Tray) obj;
		if (rowSize != other.rowSize)
			return false;
		if (colSize != other.colSize)
			return false;

		// convert the lists of blocks into sets and compare the sets
		Set<Block> thisBlockSet = new HashSet<Block>(blocksList);
		Set<Block> otherBlockSet = new HashSet<Block>(other.blocksList);
		if (!thisBlockSet.equals(otherBlockSet))
			return false;

		return true;
	}

	/**
	 * This method checks the following invariants of this Tray instance:
	 * <ul>
	 * <li>The dimension of every Block must fit in the dimension of this Tray.</li>
	 * <li>The sum of the areas of every Block must not be bigger than this
	 * Tray's area.</li>
	 * <li>This Tray must have a dimension bigger than or equal to 1X1.</li>
	 * <li>The number of blocks must not be bigger than this Tray's area.</li>
	 * <li>The number of blanks must not be bigger than this Tray's area.</li>
	 * <li>All four endpoints of every Block must be inside this Tray.</li>
	 * <li>No Block must collide with the other Blocks in this Tray.</li>
	 * <li><b>numBlocks</b> must equal <b>blocksList.size()</b>.</li>
	 * <li><b>numBlanks</b> must equal <b>blanksSet.size()</b>.</li>
	 * <li>The sum of the areas of every Block, combined with numBlanks, must
	 * equal this Tray's area.</li>
	 * </ul>
	 * 
	 * @throws IllegalStateException
	 *             when any of the invariants has been violated
	 */
	private void isOK() throws IllegalStateException {
		// The dimension of every Block must fit in the dimension of this Tray.
		for (int i = 0; i < numBlocks; i++) {
			Block b = getBlock(i);
			if (b.height > this.rowSize || b.width > this.colSize) {
				throw new IllegalStateException(
						"The dimension of every Block must fit in the dimension of this Tray.");
			}
		}

		// The sum of the areas of every Block must not be bigger than this
		// Tray's area.
		int sumAreas = 0;
		for (int i = 0; i < numBlocks; i++) {
			Block b = getBlock(i);
			sumAreas += b.height * b.width;
		}
		if (sumAreas > this.colSize * this.rowSize) {
			throw new IllegalStateException(
					"The sum of the areas of every Block must be smaller than this Tray's area.");
		}

		// This Tray must have a dimension bigger than and or equal to 1X1.
		if (this.rowSize < 1 || this.colSize < 1) {
			throw new IllegalStateException(
					"This Tray must have a dimension bigger than and or equal to 1X1.");
		}

		// The number of blocks must not be bigger than this Tray's area.
		if (this.numBlocks > this.colSize * this.rowSize) {
			throw new IllegalStateException(
					"The number of blocks must not be bigger than this Tray's area.");
		}

		// The number of blanks must not be bigger than this Tray's area.
		if (this.numBlanks > this.colSize * this.rowSize) {
			throw new IllegalStateException(
					"The number of blanks must not be bigger than this Tray's area.");
		}

		// All four endpoints of every Block must be inside this Tray.
		for (int i = 0; i < numBlocks; i++) {
			Block b = getBlock(i);
			if (!isValidPoint(b.getUpperLeft())
					|| !isValidPoint(b.getLowerLeft())
					|| !isValidPoint(b.getLowerRight())
					|| !isValidPoint(b.getUpperRight())) {
				throw new IllegalStateException(
						"All four endpoints of every Block must be inside this Tray.");
			}
		}

		// No Block must collide with the other Blocks in this Tray.
		for (int i = 0; i < numBlocks; i++) {
			Block b = getBlock(i);
			for (Block o : blocksList) {
				if (b != o && b.collidesWith(o)) {
					throw new IllegalStateException(
							"No Block must collide with the other Blocks in this Tray.");
				}
			}
		}

		// numBlocks must equal blocksList.size()
		if (numBlocks != blocksList.size()) {
			throw new IllegalStateException(
					"numBlocks must equal blocksList.size()");
		}

		// numBlanks must equal blanksSet.size()
		if (numBlanks != blanksSet.size()) {
			throw new IllegalStateException(
					"numBlanks must equal blanksSet.size()");
		}

		// The sum of the areas of every Block, combined with numBlanks, must
		// equal this Tray's area.
		if (sumAreas + numBlanks != this.colSize * this.rowSize) {
			throw new IllegalStateException(
					"The sum of the areas of every Block, combined with numBlanks, must equal this Tray's area.");
		}
	}

	/**
	 * Returns a deep copy of this Tray. This operation runs in O(A+B) time,
	 * where A is the number of Blocks and B is the number of blanks in this
	 * Tray.
	 */
	@Override
	public Tray clone() {
		return new Tray(this);
	}

	/**
	 * Checks whether the given Block is in this Tray. This operation runs in at
	 * most O(N) time, where N is the number of Blocks in this Tray.
	 * 
	 * @param b
	 *            - the Block to check
	 * @return whether the given Block is in this Tray
	 * @throws NullPointerException
	 *             when the given argument is null
	 */
	public boolean containsBlock(Block b) {
		if (b == null) {
			throw new NullPointerException();
		}
		return blocksList.contains(b);
	}

	/**
	 * Returns the Block with the given index from the list of Blocks that this
	 * Tray currently contains. This operation runs in O(1) time.
	 * 
	 * @param blockIdx
	 *            - the unique index of a Block within this Tray's list of
	 *            Blocks
	 * @return the Block with the given index from this Tray's list of Blocks
	 * @throws IndexOutOfBoundsException
	 *             when the given index is smaller than zero and larger than or
	 *             equal to the number of Blocks in this Tray
	 */
	public Block getBlock(int blockIdx) {
		return blocksList.get(blockIdx);
	}

	/**
	 * Moves the Block specified by blockIdx in the given direction <b>by
	 * one</b>. This method is the <i>only</i> public setter method of this
	 * class.
	 * 
	 * @param blockIdx
	 *            - the unique index of a Block within this Tray's list of
	 *            Blocks
	 * @param d
	 *            - the Direction in which the Block will be moved
	 * @throws IndexOutOfBoundsException
	 *             when the given index is smaller than zero and larger than or
	 *             equal to the number of Blocks in this Tray
	 * @throws IllegalArgumentException
	 *             when the specified block cannot be moved to the desired
	 *             location
	 * @throws IllegalStateException
	 *             when an invariant of this Tray has been violated after a call
	 *             to this method (only when Tray.checkInvariants==true)
	 * @throws NullPointerException
	 *             when d is null
	 */
	public void moveBlock(int blockIdx, Direction d) {
		Block oldBlock = getBlock(blockIdx);
		Point oldUL = oldBlock.getUpperLeft();
		Point oldUR = oldBlock.getUpperRight();
		Point oldLL = oldBlock.getLowerLeft();
		Point oldLR = oldBlock.getLowerRight();

		switch (d) { // mark old row/col as blank
		case UP:
			markRegion(false, oldLL, oldLR);
			break;
		case RIGHT:
			markRegion(false, oldUL, oldLL);
			break;
		case DOWN:
			markRegion(false, oldUL, oldUR);
			break;
		default:
			markRegion(false, oldUR, oldLR);
		}

		Block newBlock = null;
		try {
			newBlock = oldBlock.relocate(oldUL.go(d));
		} catch (IndexOutOfBoundsException ioobe) { // caused by go()
			throw new IllegalArgumentException(
					"block cannot be moved to the new location");
		}

		// checking whether the Block will go out of this Tray's bounds
		if (!isValidPoint(newBlock.getUpperLeft())
				|| !isValidPoint(newBlock.getLowerRight())) {
			throw new IllegalArgumentException(
					"block cannot be moved to the new location");
		}

		// checking whether the Block will collide with other Blocks
		for (Block o : blocksList) {
			if (o != oldBlock && newBlock.collidesWith(o)) {
				throw new IllegalArgumentException(
						"block cannot be moved to the new location");
			}
		}

		blocksList.set(blockIdx, newBlock); // moving the block

		Point newUL = newBlock.getUpperLeft();
		Point newUR = newBlock.getUpperRight();
		Point newLL = newBlock.getLowerLeft();
		Point newLR = newBlock.getLowerRight();

		switch (d) { // mark new row/col as occupied
		case UP:
			markRegion(true, newUL, newUR);
			break;
		case RIGHT:
			markRegion(true, newUR, newLR);
			break;
		case DOWN:
			markRegion(true, newLL, newLR);
			break;
		default:
			markRegion(true, newUL, newLL);
		}

		if (checkInvariants) {
			isOK();
		}
	}

	/**
	 * Marks the given region as specified by toOccupied.
	 * 
	 * @param toOccupied
	 *            - <b>true</b> if this region is going to be marked
	 *            <b>occupied</b>; <b>false</b> if this region is going to be
	 *            marked <b>blank</b>
	 * @param upperLeft
	 *            - the upper left corner of the region
	 * @param lowerRight
	 *            - the lower right corner of the region
	 * @throws IllegalArgumentException
	 *             when lowerRight is not on the lower right side of upperLeft
	 *             ("lower right side" includes current column and row)
	 * @throws NullPointerException
	 *             when any argument is null
	 */
	private void markRegion(boolean toOccupied, Point upperLeft,
			Point lowerRight) {
		if (upperLeft.colIdx > lowerRight.colIdx
				|| upperLeft.rowIdx > lowerRight.rowIdx) {
			throw new IllegalArgumentException(
					"lowerRight is not on the lower right side of upperLeft");
		}
		for (int i = upperLeft.rowIdx; i <= lowerRight.rowIdx; i++) {
			for (int j = upperLeft.colIdx; j <= lowerRight.colIdx; j++) {
				Point curPoint = Point.getInstance(i, j);
				if (toOccupied) {
					blanksSet.remove(curPoint);
				} else {
					blanksSet.add(curPoint);
				}
			}
		}
	}

	/**
	 * Returns an iterator over all blank Points in this Tray.
	 * 
	 * @return an iterator over all blank Points in this Tray
	 */
	public Iterator<Point> blanksIterator() {
		return blanksSet.iterator();
	}

	/**
	 * Returns the unique index of the Block in this Tray that contains the
	 * given Point. If there is no such Block, -1 is returned. This operation
	 * runs in O(N) time, where N is the number of Blocks in this Tray.
	 * 
	 * @param p
	 *            - the Point to search for
	 * @return the unique index of the Block in this Tray that contains the
	 *         given Point; if there is no such Block, -1 is returned
	 * @throws NullPointerException
	 *             when the argument is null
	 */
	public int findBlockContaining(Point p) {
		for (int i = 0; i < blocksList.size(); i++) {
			Block b = getBlock(i);
			if (b.containsPoint(p)) {
				return i;
			}
		}
		return -1;
	}

	// ///////////////////// instance members end //////////////////////
}
