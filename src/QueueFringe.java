import java.util.*;

/**
 * This class implements a SolveFringe in the form of a FIFO queue. It is
 * primarily used to perform breadth-first solution search on a given puzzle.
 */
public class QueueFringe extends LinkedList<SolveFringeElement> implements
		SolveFringe {

	// ///////////////////// instance members start ///////////////////////

	// This class uses the default constructor

	/**
	 * Adds the given element to the fringe.
	 * 
	 * @param e
	 *            - the element to add
	 * @throws NullPointerException
	 *             when the argument is null
	 */
	@Override
	public void put(SolveFringeElement e) {
		if (e == null) {
			throw new NullPointerException();
		}
		add(e);
	}

	/**
	 * Returns and removes the peeked element.
	 * 
	 * @throws NoSuchElementException
	 *             when this fringe is empty
	 */
	@Override
	public SolveFringeElement take() {
		return remove();
	}

	// isEmpty() is inherited from super

	// ///////////////////// instance members end ///////////////////////

	// compiler-generated UID
	private static final long serialVersionUID = -3876199994577866019L;
}