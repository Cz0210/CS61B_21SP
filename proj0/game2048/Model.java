package game2048;

import com.sun.jdi.Value;

import java.util.Formatter;
import java.util.Iterator;
import java.util.Observable;


/**
 * The state of a game of 2048.
 *
 * @author Cz0210
 */
public class Model extends Observable {
    /**
     * Current contents of the board.
     */
    private Board board;
    /**
     * Current score.
     */
    private int score;
    /**
     * Maximum score so far.  Updated when game ends.
     */
    private int maxScore;
    /**
     * True iff game is ended.
     */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /**
     * Largest piece value.
     */
    public static final int MAX_PIECE = 2048;

    /**
     * A new 2048 game on a board of size SIZE with no pieces
     * and score 0.
     */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /**
     * A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes.
     */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /**
     * Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     * 0 <= COL < size(). Returns null if there is no tile there.
     * Used for testing. Should be deprecated and removed.
     */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /**
     * Return the number of squares on one side of the board.
     * Used for testing. Should be deprecated and removed.
     */
    public int size() {
        return board.size();
    }

    /**
     * Return true iff the game is over (there are no moves, or
     * there is a tile with value 2048 on the board).
     */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /**
     * Return the current score.
     */
    public int score() {
        return score;
    }

    /**
     * Return the current maximum game score (updated at end of game).
     */
    public int maxScore() {
        return maxScore;
    }

    /**
     * Clear the board to empty and reset the score.
     */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /**
     * Add TILE to the board. There must be no Tile currently at the
     * same position.
     */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /**
     * Tilt the board toward SIDE. Return true iff this changes the board.
     * <p>
     * 1. If two Tile objects are adjacent in the direction of motion and have
     * the same value, they are merged into one Tile of twice the original
     * value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     * tilt. So each move, every tile will only ever be part of at most one
     * merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     * value, then the leading two tiles in the direction of motion merge,
     * and the trailing tile does not.
     */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.

        board.setViewingPerspective(side);

        int[][] ifMerge = new int[board.size()][board.size()];//0代表未merge，1代表已经merge，无法再次merge
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                ifMerge[i][j] = 0;
            }
        }

        for (int c = 0; c < board.size(); c++) {
            for (int r = board.size() - 1; r >= 0; r--) {
                Tile t = board.tile(c, r);
                if (t != null) {
                    int dis = checkUpTile(t, side);
                    if (dis != 0) {
                        //dis != 0代表可以移动，ifMerge[c][r + dis] != 0代表可以merge
                        if (board.tile(c, r + dis) == null) {//空的，移动，不进行merge
                            board.move(c, r + dis, t);
                            changed = true;
                        } else if (ifMerge[c][r + dis] == 0) {//不是空的，merge
                            score += 2 * t.value();
                            board.move(c, r + dis, t);
                            ifMerge[c][r + dis] = 1;
                            changed = true;
                        } else {//不是空的，不能merge，仅移动
                            board.move(c, r + dis - 1, t);
                            changed = true;
                        }
                    }
                }
            }
        }
        board.setViewingPerspective(Side.NORTH);
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    public static void rotateMatrix1(int[][] matrix) {//顺时针
        int n = matrix.length;

        // 先沿对角线翻转矩阵
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // 再翻转每一行
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n / 2; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[i][n - j - 1];
                matrix[i][n - j - 1] = temp;
            }
        }
    }

    public static void rotateMatrix2(int[][] matrix) {
        int n = matrix.length;

        // 先沿对角线翻转矩阵
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // 再翻转每一列
        for (int i = 0; i < n / 2; i++) {
            for (int j = 0; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[n - i - 1][j];
                matrix[n - i - 1][j] = temp;
            }
        }
    }


    public static int[][] rotate180(int[][] matrix) {
        int n = matrix.length;
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = matrix[n - 1 - i][n - 1 - j];
            }
        }
        return result;
    }

    public int checkUpTile(Tile t, Side side) {
        int row = t.row();
        int col = t.col();
        int[][] metrix = new int[board.size()][board.size()];
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                if (board.tile(j, i) != null) {
                    metrix[i][j] = board.tile(j, i).value();
                } else {
                    metrix[i][j] = 0;
                }
            }
        }
        if (side == Side.SOUTH) {
            rotate180(metrix);
            row = board.size() - row - 1;
            col = board.size() - col - 1;
        } else if (side == Side.EAST) {
//            rotateMatrix2(metrix);
            int r = row, c = col;
            col = board.size() - r - 1;
            row = c;
        } else if (side == Side.WEST) {
//            rotateMatrix1(metrix);
            int r = row, c = col;
            col = r;
            row = board.size() - c - 1;
        }
        //t在最顶上一行
        if (row == board.size() - 1) {
            return 0;
        }
        //上面的tile与t相邻且value相等，可以进行merge
        if (metrix[row + 1][col] != 0 && metrix[row + 1][col] == t.value()) {
            return 1;
        }
        //不相邻
        int i = 1;
        for (i = 1; i + row < board.size(); i++) {
            if (metrix[i + row][col] != 0) {
                break;
            }
        }
        i -= 1;
        //不相邻，value相同，进行merge
        if (i + row + 1 < board.size() && metrix[i + row + 1][col] != 0) {
            if (t.value() == metrix[i + row + 1][col]) {
                i++;
            }
        }
        return i;//i==0说明，顶上就有tile，无法移动
        //i!=0时，i为需要移动的距离
    }

    /**
     * Checks if the game is over and sets the gameOver variable
     * appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /**
     * Determine whether game is over.
     */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /**
     * Returns true if at least one space on the Board is empty.
     * Empty spaces are stored as null.
     */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        for (Tile t : b) {
            if (t == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        for (Tile t : b) {
            if (t != null && t.value() == MAX_PIECE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        if (emptySpaceExists(b)) {
            return true;
        }
        for (Tile t : b) {
            if (t.col() > 0) {
                if (b.tile(t.col() - 1, t.row()).value() == t.value()) {
                    return true;
                }
            }
            if (t.col() < b.size() - 1) {
                if (b.tile(t.col() + 1, t.row()).value() == t.value()) {
                    return true;
                }
            }
            if (t.row() > 0) {
                if (b.tile(t.col(), t.row() - 1).value() == t.value()) {
                    return true;
                }
            }
            if (t.row() < b.size() - 1) {
                if (b.tile(t.col(), t.row() + 1).value() == t.value()) {
                    return true;
                }
            }
        }
//        for (int i = 0; i < b.size(); i++) {
//            for (int j = 0; j < b.size() - 1; j++) {
//                Tile up = b.tile(i, j);
//                Tile down = b.tile(i, j + 1);
//                if (up.value() == down.value()) {
//                    return true;
//                }
//                Tile left = b.tile(j, i);
//                Tile right = b.tile(j + 1, i);
//                if (left.value() == right.value()) {
//                    return true;
//                }
//            }
//        }
        return false;
    }


    @Override
    /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
