import java.util.Random;
/**
 * A concrete Tic-tac-toe player that picks moves uniformly at random from the 
 * still unoccupied positions on the game board.
 */
public class CpuPlayer extends APlayer {
    static boolean stillHaveChance = true;

    // constructor 
    /** Constructor for objects of class CpuPlayer
     *  @param game - the tic-tac-toe game that is to be played
     *  @param symbol - the character symbol representing this player's moves
     */
    public CpuPlayer(Game game, char symbol) {
        super(game, 'O');
        stillHaveChance = this.stillHaveChance;
    }

    // method
    /** Picks a move uniformly at random. It does this by generating random moves 
     * within the game board boundaries until it finds an unnocupied position. Assumes
     * the game isn't over yet, otherwise it'd go into an infinite loop.
     * @override pickMove in class APlayer
     * @return the chosen move. Because the CPU never quits, this implementation 
     * method never returns null.
     */
    public Move pickMove() {

        Move move = new Move(0,0);
        Game currGame = this.game;
        int size = currGame.boardSize;
        char[][] board = currGame.board;        
        Move moveMiddle = new Move(size / 2, size / 2);

        // advanced feature 1
        // logic: pick the center spot for odd-sized game 
        if ((size % 2 != 0) && (game.isValidMove(moveMiddle) == 'V')) {
            move = moveMiddle;
        } else {

            // advanced feature 2
            // logic: if there is one move away from completing a row, a column or a 
            //        diagonal, the program would favors that move instead of the ran-
            //        domized move 

            // set up 
            // number of rows + columns + 2 diagonals 
            int totalLines = size + size + 2;
            // personal note: 
            // slots from 0 to (size - 1) contain the scanned points for rows 1 to (size)
            // slots from size to (2 * size - 1) contain the scanned points for columns 1 to (size)
            // the penultimate slot contains the scanned point for the (\) diagonal line 
            // the last slot contains the scanned point for the (\) diagonal line 
            int[] scanPoint = new int[totalLines];
            int[] rowPoint = new int[size];
            int[] colPoint = new int[size];
            int firstDiagPoint = 0;
            int secDiagPoint = 0;

            // scan all the rows for O - TESTED :)
            for (int i=0; i < size; i++) {
                for (int j=0; j < size; j++) {
                    char symbol = board[i][j]; 
                    if (symbol == 'O') {
                        // increment respective rowPoint element 
                        rowPoint[i]++;
                    } else if (symbol == 'X') {
                        rowPoint[i] = 0;
                        break;
                    }
                }
            }
            
            // scan all the columns for O - TESTED :) 
            // create a transposed version of the board 
            char[][] transposed = new char[size][size];

            for(int i = 0; i < size; i++) 
                for(int j = 0; j < size; j++) 
                    transposed[i][j] = board[j][i];

            for (int i=0; i < size; i++) {
                for (int j=0; j < size; j++) {
                    char symbol = transposed[i][j]; 
                    if (symbol == 'O') {
                        // increment respective rowCol element 
                        colPoint[i]++;
                    } else if (symbol == 'X') {
                        colPoint[i] = 0;
                        break;
                    }
                }
            }

            // scan the \ diagonal for O - TESTED :)
            for (int i=0; i < size; i++) {
                char symbol = board[i][i];
                if (symbol == 'X') {
                    firstDiagPoint = 0;
                    break;
                } else if (symbol == 'O') {
                    firstDiagPoint++;
                } 
            }

            // scan the / diagonal for O - TESTED :)
            for (int j=0; j < size; j++) {
                char symbol = board[j][size - 1 - j];
                if (symbol == 'X') {
                    secDiagPoint = 0;
                    break;
                }
                else if (symbol == 'O') {
                    secDiagPoint++;
                }
            }

            // put points into the scanPoint array 
            // transfer from rowPoint
            for (int i=0; i < size; i++) {
                scanPoint[i] = rowPoint[i];
            }
            // transfer from colPoint
            for (int i=size, j=0; i < size * 2 && j < size; i++, j++) {
                scanPoint[i] = colPoint[j];
            }
            // transfer from the 2 diagonals
            scanPoint[totalLines - 2] = firstDiagPoint;
            scanPoint[totalLines - 1] = secDiagPoint;

            int first = scanPoint[0];
            
            // if all points are zero but there are still blank places 
            boolean switchToRandom = false;
            if (first == 0) {
            for (int i=0; i < totalLines; i++) {
                    if (scanPoint[i] != first) {
                        switchToRandom = false;
                        break;
                    }
                    switchToRandom = true; 
                }
            }

            // identify a maximum slot of scanPoint array
            int maxSlot = 0; 

            for (int i=0; i < totalLines; i++) {
                if(scanPoint[i] > first) {
                    maxSlot = i;
                    first = scanPoint[i];
                }
            }

            // translate maxSlot into a Move object
            Random rand = new Random();

            while (switchToRandom == true && game.isValidMove(move) != 'V') {
                move.row = rand.nextInt(size);
                move.col = rand.nextInt(size);
            }
            
            while (switchToRandom == false && game.isValidMove(move) != 'V') {
                // if maxSlot indicates a move in the rows 
                if (maxSlot < size) {
                    move.row = maxSlot;
                    move.col = rand.nextInt(size);
                }
                // if maxSlot indicates a move in the columns
                else if (maxSlot >= size && maxSlot < size * 2) {
                    move.col = maxSlot - size;
                    move.row = rand.nextInt(size);
                }
                // if maxSlot indicates a move in the first diagonal 
                else if (maxSlot == totalLines - 2) {
                    move.row = move.col = rand.nextInt(size);
                }
                // if maxSlot indicates a move in the second diagonal 
                else if (maxSlot == totalLines - 1) {
                    move.row = rand.nextInt(size);
                    move.col = size - 1 - move.row;
                }
                else {
                    move.row = rand.nextInt(size);
                    move.col = rand.nextInt(size);
                }
            }
        }

        // print out move 
        int charRowNum = move.row + 65;
        char charRow = (char)charRowNum;
        int charCol = move.col + 1;
        System.out.println("CPU's move: " + charRow + charCol);

        return move; 
    }

    /** Scan all possible lines and return with the number of slots already filled 
     *  counting towards a full line. If there is any human-occupied slot, the point 
     *  is 0. If there isn't, the points are based on the number of CPU-occupied slots.
     *  @param game - the current game 
     *  @return an array of integers indicating the the number of slots already 
     *          filled 
     */
    public int[] scanBoard(Game game) {

        return null;
    }
}
