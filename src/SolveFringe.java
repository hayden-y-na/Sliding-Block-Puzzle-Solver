/**
 * <p>
 * This interface was created to let the puzzle-solving process easily switch
 * between depth-first search and breadth-first search. This interface allows
 * polymorphism between <b>StackFringe</b> and <b>QueueFringe</b>.
 * </p>
 * 
 * <p>
 * A SolveFringe will contain a series of <b>SolveFringeElement</b>s in a
 * specific order according to whether it is a StackFringe or a QueueFringe.
 * </p>
 */
public interface SolveFringe {

	// ///////////////////// instance members start ///////////////////////

	/**
	 * Adds the given element to the fringe.
	 * 
	 * @param e
	 *            - the element to add
	 * @throws NullPointerException
	 *             when the argument is null
	 */
	public void put(SolveFringeElement e);

	/**
	 * Returns and removes the peeked element.
	 * 
	 * @throws NoSuchElementException
	 *             when this fringe is empty
	 */
	public SolveFringeElement take();

	/**
	 * Checks whether this fringe is empty.
	 * 
	 * @return whether this fringe is empty
	 */
	public boolean isEmpty();

	// ///////////////////// instance members end ///////////////////////
}
