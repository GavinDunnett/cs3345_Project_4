import java.util.*;

/**
 * This program generates and displays a maze as described in the textbook. The program
 * is a command-line program that generates a character-based maze.
 * The user is able to specify the number of rows and columns in the maze, up to 20x20.
 * The DisjSet class from the textbook is used to implement the textbook's 
 * algorithm. The DisjSet class is used as given in the textbook without any
 * modifications to it.
 * 
 * 20FA CS3345 Data Structures and Introduction to Algorithmic Analysis
 * Project #4
 * @author Gavin John Dunnett
 */

public class Maze {
    private int numRows; // number of rows in the maze
    private int numCols; // number of columns in the maze
    private int size; // number of cells in the maze (m * n)
    private DisjSets sets;
    private int[][] maze;
    private Random rand = new Random();
    private java.util.Scanner kb = new Scanner(System.in); // reads keyboard input

    public Maze() {
        while (true) { // repeat until user decides to exit
            do {
                System.out.println("Enter a number of rows (1 to 20) or 0 to exit: ");
                numRows = kb.nextInt();
                if (numRows == 0)
                    System.exit(0);
            } while (numRows < 1 || numRows > 20);
            do {
                System.out.println("Enter a number of columns (1 to 20) or 0 to exit: ");
                numCols = kb.nextInt();
                if (numCols == 0)
                    System.exit(0);
            } while (numCols < 1 || numCols > 20);

            size = numRows * numCols;

            // Initialize the maze with its walls in place.
            // Walls are either removeable or permanent. Perimeter walls are permanent.
            // Instead of using numerious ifelse blocks to check for walls, I decided to use
            // prime numbers as these provide more concise expressions. This provides a quick way to
            // check for walls since primes are only divisable by themselves and one.
            // Removable walls
            //      left = 2
            //      bottom = 3
            // Permanent walls
            //      left = 5
            //      bottom = 7
            //
            // example...
            // a cell with a removable left & bottom
            //      2*3 = 6
            // now, check if this cell has a removable left
            //      6%2 == 0 Yes, it does
            // now, remove the left wall
            //      6/2 = 3 (the cell is left with its removable bottom)
            // now, check again is this cell has a removable left
            //      3%2 == 1 No! it does not
            // now, remove the bottom wall
            //      3/3 = 1 (the cell now has no walls)
            //
            // another example...
            // a cell with a removable left but permanent bottom
            //      2*7 = 14
            // now, check if this cell has a removeable bottom
            //      14%3=2 No! it does not
            // now, check if this cell has a removable left
            //      14%2=0 Yes, it does
            // now, remove the left wall
            //      14/2 = 7 (the cell is left with its permanent bottom)
            // now, check again if this cell has a removeable bottom
            //      7%3=1 No!, it does not
            maze = new int[numRows][numCols];
            for (int m = 0; m < numRows; m++) {
                maze[m][0] = 15; // left perimeter: 5*3 permanent left but removable bottom
                for (int n = 1; n < numCols; n++)
                    maze[m][n] = 6; // 2*3 removable bottom & left
                maze[numRows - 1][0] = 35; // bottom left cornet so permanent left and bottom
                for (int n = 1; n < numCols; n++) // bottom row except for left corner
                    maze[numRows - 1][n] = 14; // 2*7 removale left but permanent bottom
            }

            sets = new DisjSets(size); // initialize disjoint sets

            while (!isSingleSet()) { // repeat loop until maze's cells are a single set
                int neighbor = 0;
                int m = rand.nextInt(numRows); // choose a row
                int n = rand.nextInt(numCols); // choose a column
                int wall = 2 + rand.nextInt(2); // choose a left or bottom wall to remove
                // If a perimeter wall or any wall that has already been deleted is choosen then this if-statement detects it.
                if (maze[m][n] % wall != 0) //  test if cell has such a removable wall
                    continue; // jump to loop's next iteration
                int cell = (m * numCols) + n; // calculate cell number
                switch (wall) { // calculate cell's neighbor
                    case 2: // left wall so neighbor is one cell to the left
                        neighbor = cell - 1;
                        break;
                    case 3: // bottom wall so neighbor is one column below
                        neighbor = cell + (numCols);
                }
                // delete that wall only if the cell and its neighbor are not in the same set
                int root1 = sets.find(cell);
                int root2 = sets.find(neighbor);
                if (root1 == root2) // cell and neighbor in same set so...
                    continue; // jump to loop's next iteration
                maze[m][n] = maze[m][n] / wall; // remove the wall
                sets.union(root1, root2); // union the sets
            }
            drawMaze();
        }
    }

    /**
     * Tests if all the sets have the same root.
     * @return True if the sets are a single set.
     */
    private boolean isSingleSet() {
        int baseRoot = sets.find(0);
        for (int x = 1; x < size; x++) {
            if (baseRoot != sets.find(x))
                return false;
        }
        return true;
    }

    /**
     * Draw the maze.
     */
    private void drawMaze() {

        System.out.println();
        System.out.print(" ");
        for (int n = 0; n < numCols - 1; n++)
            System.out.print("\u2584"); // draw top perimeter
        System.out.println();
        for (int m = 0; m < numRows; m++) {
            for (int n = 0; n < numCols; n++) {
                switch (maze[m][n]) {
                    case 2:
                    case 5:
                        System.out.print("\u258C"); // draw left
                        break;
                    case 3:
                    case 7:
                        System.out.print("\u2584"); // draw bottom
                        break;
                    case 6:
                    case 14:
                    case 15:
                    case 35:
                        System.out.print("\u2599"); // draw left/bottom
                        break;
                    default:
                        System.out.print(" ");
                }
            }
            if (m < numRows - 1)
                System.out.print("\u258C"); // draw right perimeter
            System.out.println();
        }
    }

    public static void main(String[] args) {
        new Maze();
    }
}