import java.util.Stack;

/**
 * <p>
 * A SolveFringeElement instance serves as an element in a SolveFringe. Its main
 * purpose is to keep track of the moves that led the search process to the Tray
 * specified by this class's d Tray instance variable.
 * </p>
 * 
 * <p>
 * The references of a SolveFringeElement instance, once created, are immutable.
 * </p>
 * 
 */
public class SolveFringeElement {

	// ///////////////////// instance members start ///////////////////////

	/**
	 * Represents the current Tray configuration that the search process is at.
	 */
	public final Tray tray;

	/**
	 * Represents the other element that the search process was at <i>right
	 * before</i> this configuration.
	 */
	public final SolveFringeElement parent;

	/**
	 * Represents the position of the Block that has just been moved <i>in the
	 * <b>parent element</b>'s Tray configuration<i>.
	 */
	public final Point oldBlockPosition;

	/**
	 * Reperesents the position of the Block that has just been moved <i>in
	 * <b>this element</b>'s Tray configuration</i>.
	 */
	public final Point newBlockPosition;

	/**
	 * Creates a new SolveFringeElement with the given arguments. All the
	 * references set by the arguments are thereby immutable. Set parent,
	 * oldBlockPosition and newBlockPosition to <b>null</b> if this element is
	 * the root of a fringe. However, the <b>tray</b> argument should never be
	 * null.
	 * 
	 * @param tray
	 *            - the current Tray configuration that the search process is at
	 * @param parent
	 *            - the other element that the search process was at <i>right
	 *            before</i> this configuration
	 * @param oldBlockPosition
	 *            - the position of the Block that has just been moved <i>in the
	 *            <b>parent element</b>'s Tray configuration</i>
	 * @param newBlockPosition
	 *            - the position of the Block that has just been moved <i>in
	 *            <b>this element</b>'s Tray configuration</i>
	 * @throws NullPointerException
	 *             when tray is null
	 */
	public SolveFringeElement(Tray tray, SolveFringeElement parent,
			Point oldBlockPosition, Point newBlockPosition) {
		if (tray == null) {
			throw new NullPointerException();
		}
		this.tray = tray;
		this.parent = parent;
		this.oldBlockPosition = oldBlockPosition;
		this.newBlockPosition = newBlockPosition;
	}

	/**
	 * <p>
	 * Prints the series of moves that led the search process from its initial
	 * Tray to this configuration, in the order starting from the initial Tray
	 * and ending at this Tray.
	 * </p>
	 * 
	 * <p>
	 * Each line contains four digits and corresponds to a single move. The
	 * first two digits represent the row/column indices of the moving Block
	 * <i>before</i> the move. The last two digits represent the row/column
	 * indices of the moving Block <i>after</i> the move.
	 * </p>
	 * 
	 * <p>
	 * This method also returns an integer value that counts all the parents of
	 * this element and the element itself (this element, direct parent,
	 * grandparent, grand-grandparent, and so on).
	 * </p>
	 * 
	 * @return the number of parents of this element
	 */
	public int printMoveTrace() {
		outputStack = new Stack<String>();
		printMoveTraceHelper(this);
		int rtn = outputStack.size();
		while (!outputStack.isEmpty()) {
			System.out.println(outputStack.pop());
		}
		return rtn;
	}

	// ///////////////////// instance members end ///////////////////////

	// ///////////////////// static members start ///////////////////////

	/**
	 * This stack is used to reverse the order of moves that
	 * <b>printMoveTraceHelper</b> has searched through.
	 */
	private static Stack<String> outputStack = null;

	/**
	 * Adds the given argument's move string (as specified in
	 * <b>printMoveTrace()</b>), to the <b>outputStack</b>, and then iteratively
	 * does the same thing on the root's parent if any, then on the root's
	 * grandparent if any, and so on. If root==null, nothing happens.
	 * 
	 * @param root
	 *            - the element to start the addition process at
	 */
	private static void printMoveTraceHelper(SolveFringeElement root) {
		while (root != null) {
			if (root.oldBlockPosition != null && root.newBlockPosition != null) {
				outputStack.push(root.oldBlockPosition.toString() + " "
						+ root.newBlockPosition.toString());
			}
			root = root.parent;
		}
	}

	// ///////////////////// static members end ///////////////////////
}