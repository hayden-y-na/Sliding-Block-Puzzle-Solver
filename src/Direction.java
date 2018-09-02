/**
 * <p>
 * This <b>enum</b> type represents an abstract direction of movement within a
 * Tray. There are four Directions: RIGHT, DOWN, LEFT, UP.
 * </p>
 * 
 * <p>
 * Java's enum types have a built-in public method named <b>values()</b> which
 * returns an array of all possible values in the enum type. In this
 * application, <b>Direction.values()</b> is in very high demand during the
 * puzzle-solving process. An issue with these calls to <b>values()</b>,
 * however, is that each time <b>values()</b> is called, it creates a <i>new</i>
 * array, instead of returning a <i>pooled</i> reference (this was verified on
 * the VisualVM Memory Analyzer). In order to save memory, this enum type thus
 * defines a public static field named <b>Direction.all</b>, which basically
 * serves the same purpose as <b>values()</b> except it returns a fixed, already
 * pooled reference.
 * </p>
 */
public enum Direction {

	/////////////////////// static members start ///////////////////////

	RIGHT, DOWN, LEFT, UP;

	/**
	 * A static, immutable array containing all four Directions in no specific
	 * order.
	 */
	public static final Direction[] all = { RIGHT, DOWN, LEFT, UP };

	/////////////////////// static members end ///////////////////////

	/////////////////////// instance members start ///////////////////////

	/**
	 * Returns the direction reverse to this Direction.
	 * 
	 * @return DOWN if this is UP; RIGHT if this is LEFT; UP if this is DOWN;
	 *         LEFT if this is RIGHT
	 */
	public Direction reverse() {
		switch (this) {
		case UP:
			return DOWN;
		case RIGHT:
			return LEFT;
		case DOWN:
			return UP;
		default:
			return RIGHT;
		}
	}

	/////////////////////// instance members end ///////////////////////
}