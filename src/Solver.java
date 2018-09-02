import java.io.*;
import java.util.*;

/**
 * This class is the main driver of this application. It contains the main
 * method and various other private helper methods that help solve a given
 * puzzle.
 * 
 * Refer to the Javadoc on the <b>solve</b> method for more information on the
 * search process, and to the Javadoc on the <b>main</b> method for more
 * information on the debugging flags.
 */
public class Solver {

	// ///////////////////// static members start ///////////////////////

	private static boolean willPrintTime = false;
	private static boolean willPrintNumStates = false;
	private static boolean willPrintNumMoves = false;
	private static int numMoves = 0;
	private static boolean blankwiseOnly = false;
	private static boolean blockwiseOnly = false;

	/**
	 * The main method of this application. It is assumed that the initial and
	 * goal configuration files are free of errors. It is also assumed that the
	 * length and the width of a tray are no larger than 256.
	 * 
	 * @param args
	 *            - the command-line arguments:
	 *            <p>
	 *            <ul>
	 *            <li>First argument (optional, for debugging purposes): string
	 *            whose first two characters are "-o" and whose remaining
	 *            characters are one or more among A, C, M, O, S, T (no other
	 *            characters may appear) in any order. For more information on
	 *            what each flag means, see below.</li>
	 *            <li>Second argument: the name of the file that specifies an
	 *            initial tray configuration</li>
	 *            <li>Third argument: the name of the file that specifies a
	 *            desired goal configuration</li>
	 *            </ul>
	 *            </p>
	 * 
	 *            <p>
	 *            Debugging flags
	 *            <ul>
	 *            <li>T: The output of this program will inlcude the time taken
	 *            to execute this program.</li>
	 *            <li>C: Throughout the search process, a Tray will enforce
	 *            invariants on itself (more time-consuming)</li>
	 *            <li>A: This Solver will perform blank-wise search.</li>
	 *            <li>O: This Solver will perform block-wise search.</li>
	 *            <br/>
	 *            <br/>
	 *            <i>Note: If both A and O are set, the Solver will refuse to
	 *            start solution process. If neither is set, the Solver will
	 *            perform the most optimal search, based on the guidelines
	 *            demonstrated in the Javadoc on <b>Solver.solve</b>.</i> <br/>
	 *            <br/>
	 *            <li>S: The output of this program will include the number of
	 *            Tray configurations that the search process has come across.</li>
	 *            <li>M: The output of this program will include the number of
	 *            moves that the solution, if found, to this puzzle will have.</li>
	 *            </ul>
	 *            </p>
	 *            
	 * 		      <p>
	 *            Example of using the flags: -oTCS, -oOM, etc.
	 *            </p>
	 */
	public static void main(String[] args) {
		long start = System.nanoTime();

		if (args.length < 2 || args.length > 3) {
			System.out
					.println("There must be either 2 or 3 arguments. Refer to the Javadoc on Solver.main for details.");
			System.exit(1);
		}

		if (args.length == 3) {
			String oarg = args[0];
			if (!oarg.matches("-o[TCAOSM]{1,}")) {
				System.out
						.println("The debugging option must be in the format: -o[flags]. Refer to the Javadoc on Solver.main for details.");
				System.exit(1);
			}
			if (oarg.contains("T"))
				willPrintTime = true;
			if (oarg.contains("C"))
				Tray.checkInvariants = true;
			if (oarg.contains("A"))
				blankwiseOnly = true;
			if (oarg.contains("O"))
				blockwiseOnly = true;
			if (oarg.contains("S"))
				willPrintNumStates = true;
			if (oarg.contains("M"))
				willPrintNumMoves = true;

			if (blankwiseOnly && blockwiseOnly) {
				System.out
						.println("You cannot want both blank-wise search and block-wise search.");
				System.exit(1);
			}
		}

		BufferedReader inputReader = null;
		BufferedReader outputReader = null;
		Tray initialTray = null;
		List<Block> desiredBlocks = null;

		try {
			if (args.length == 2) {
				inputReader = new BufferedReader(new FileReader(args[0]));
				outputReader = new BufferedReader(new FileReader(args[1]));
			} else {
				inputReader = new BufferedReader(new FileReader(args[1]));
				outputReader = new BufferedReader(new FileReader(args[2]));
			}

			// first line of the input file must specify the tray dimensions
			Scanner firstLineScan = new Scanner(inputReader.readLine());
			short rowSize = firstLineScan.nextShort();
			short colSize = firstLineScan.nextShort();
			firstLineScan.close();

			Point.initPool(rowSize + 1, colSize + 1);

			// set up the initial tray
			List<Block> inputBlocks = new ArrayList<Block>();
			String curLine;
			while ((curLine = inputReader.readLine()) != null) {
				inputBlocks.add(createBlock(curLine));
			}
			initialTray = new Tray(rowSize, colSize, inputBlocks);
			inputReader.close();

			// set up the final desired blocks
			desiredBlocks = new ArrayList<Block>();
			while ((curLine = outputReader.readLine()) != null) {
				desiredBlocks.add(createBlock(curLine));
			}
			outputReader.close();
		} catch (IOException ioe) {
			System.out.println("Could not open file!");
		}

		try { // solve the puzzle
			if (!solve(initialTray, desiredBlocks)) {
				if (willPrintTime) {
					System.out.println("Finished in: "
							+ (System.nanoTime() - start) / 1000000000.0
							+ " seconds");
				}
				System.exit(1);
			} else {
				if (willPrintTime) {
					System.out.println("Finished in: "
							+ (System.nanoTime() - start) / 1000000000.0
							+ " seconds");
				}
				if (willPrintNumStates) {
					System.out.println("Total configurations visited: "
							+ configurationsSeen.size());
				}
				if (willPrintNumMoves) {
					System.out
							.println("Total number of moves in this solution: "
									+ numMoves);
				}
				return;
			}
		} catch (IllegalStateException ise) {
			System.out
					.println("The following invariant on a Tray has been violated: "
							+ ise.getMessage());
		}

	}

	/**
	 * This set keeps track of all the configurations that the solver has
	 * encountered, so that memoization prevents infinite loops from occurring
	 * during solution search.
	 */
	private static final Set<Tray> configurationsSeen = new HashSet<Tray>();

	/**
	 * <p>
	 * This Solver will make two choices before it performs the search:
	 * 
	 * <ul>
	 * <li>Blank-wise search or block-wise search: if neither
	 * Solver.blankwiseOnly or Solver.blockwiseOnly is true, the Solver will
	 * choose block-wise search if the number of Blocks in initialTray is larger
	 * than the number of blank spots. For explanation of what blank-wise or
	 * block-wise search is, see below.</li>
	 * <li>Depth-first search or breadth-first search: if initialTray's
	 * dimension is bigger than 50X50, the Solver will think that the tray is
	 * too big for DFS and use BFS. Otherwise, the Solver will use DFS</li>
	 * </ul>
	 * </p>
	 * <p>
	 * <i>Blank-wise search</i> is a search process in which the Solver spots a
	 * blank within a Tray and tries moving the Blocks around the blank. This
	 * approach is more suitable when there are fewer blanks than the number of
	 * Blocks in initialTray.
	 * </p>
	 * <p>
	 * <i>Block-wise search</i> is a search process in which the Solver goes
	 * through every single Block within a Tray and tries moving it in all
	 * directions. This approach is more suitable when there are fewer Blocks
	 * than the number of blanks in initialTray.
	 * </p>
	 * 
	 * @param initialTray
	 *            - the initial Tray configuration to start the search process
	 *            at
	 * @param desiredBlocks
	 *            - the collection of blocks in the desired final Tray
	 *            configuration
	 * @throws IllegalStateException
	 *             when any of the Trays in the search process fails to keep its
	 *             invariants (only when Tray.checkInvariants is set to true)
	 * @throws NullPointerException
	 *             when any argument is null
	 */
	private static boolean solve(Tray initialTray,
			Collection<Block> desiredBlocks) {

		SolveFringe fringe = null;
		// BFS if Tray is too big for DFS
		if (initialTray.rowSize > 50 && initialTray.colSize > 50) {
			fringe = new QueueFringe();
		} else {
			fringe = new StackFringe();
		}

		fringe.put(new SolveFringeElement(initialTray, null, null, null));

		while (!fringe.isEmpty()) {
			SolveFringeElement curElem = fringe.take();
			Tray currentConfig = curElem.tray;

			if (isDesiredConfiguration(currentConfig, desiredBlocks)) {
				numMoves = curElem.printMoveTrace();
				return true;
			}

			if (configurationsSeen.contains(currentConfig)) {
				continue;
			}
			configurationsSeen.add(currentConfig);

			if (blockwiseOnly
					|| (!blankwiseOnly && currentConfig.numBlocks < currentConfig.numBlanks)) {
				// block-wise search
				for (int i = 0; i < currentConfig.numBlocks; i++) {
					for (Direction d : Direction.all) {
						Point oldBlockPosition = currentConfig.getBlock(i)
								.getUpperLeft();
						Tray nextConfig = currentConfig.clone();
						try {
							nextConfig.moveBlock(i, d);
						} catch (IllegalArgumentException iae) {
							// the block cannot be moved in this direction
							// so do not add the new configuration to the fringe
							continue;
						}
						Point newBlockPosition = nextConfig.getBlock(i)
								.getUpperLeft();
						fringe.put(new SolveFringeElement(nextConfig, curElem,
								oldBlockPosition, newBlockPosition));
					}
				}
			} else { // blank-wise search
				Iterator<Point> blanksItr = currentConfig.blanksIterator();
				while (blanksItr.hasNext()) {
					Point curBlank = blanksItr.next();
					for (Direction d : Direction.all) {
						int i = -1;
						try {
							i = currentConfig.findBlockContaining(curBlank.go(d
									.reverse()));
						} catch (IndexOutOfBoundsException ioobe) {
							continue;
						}
						if (i != -1) {
							Point oldBlockPosition = currentConfig.getBlock(i)
									.getUpperLeft();
							Tray nextConfig = currentConfig.clone();
							try {
								nextConfig.moveBlock(i, d);
							} catch (IllegalArgumentException iae) {
								// the block cannot be moved in this direction
								// so do not add the new configuration to the
								// fringe
								continue;
							}
							Point newBlockPosition = nextConfig.getBlock(i)
									.getUpperLeft();
							fringe.put(new SolveFringeElement(nextConfig,
									curElem, oldBlockPosition, newBlockPosition));
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether config contains all the Blocks defined in desiredBlocks.
	 * 
	 * @param config
	 *            - the Tray configuration to compare
	 * @param desiredBlocks
	 *            - the collection of blocks in the desired final Tray
	 *            configuration
	 * @return whether config contains all the Blocks defined in desiredBlocks
	 * @throws NullPointerException
	 *             when any argument is null
	 */
	private static boolean isDesiredConfiguration(Tray config,
			Collection<Block> desiredBlocks) {
		for (Block b : desiredBlocks) {
			if (!config.containsBlock(b)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Scans through the given String, which must be in the format
	 * "ULrow ULcol LRrow LRcol", and returns the corresponding Block.
	 * 
	 * @param line
	 *            - the String input to parse
	 * @return the Block whose toString() would return the exact same format as
	 *         the given String (not counting whitespace)
	 * @throws IllegalArgumentException
	 *             when there are more or less than four arguments in the input,
	 *             or when the input contains a non-integer value, or when the
	 *             input contains an integer value that is outside the range
	 *             defined by the type <b>short</b>, or when the Point defined
	 *             by LRrow and LRcol is not on the lower right side of the
	 *             Point defined by ULrow and ULcol ("lower right side" includes
	 *             same row and same column)
	 * @throws IndexOutOfBoundsException
	 *             when any of the argument's integers is negative
	 * @throws NullPointerException
	 *             when the argument is null
	 */
	private static Block createBlock(String line) {
		Scanner scan = new Scanner(line);
		try {
			short row1 = scan.nextShort();
			short col1 = scan.nextShort();
			short row2 = scan.nextShort();
			short col2 = scan.nextShort();
			if (scan.hasNext()) {
				throw new IllegalArgumentException(
						"too many arguments in the input");
			}
			return Block.getInstance(row1, col1, row2, col2);
		} catch (InputMismatchException ime) {
			throw new IllegalArgumentException(
					"non-integer argument or value too big");
		} catch (NoSuchElementException nsee) {
			throw new IllegalArgumentException("too few arguments in the input");
		} finally {
			scan.close();
		}
	}
	// ///////////////////// static members end ///////////////////////
}
