import java.util.ArrayDeque;
import java.util.Queue;

public class CannyTracer {
    private int[][] cannyMap;
    private Queue<Instruction> instructions = new ArrayDeque<>();

    /** Constructor, takes an int array as @param image */
    public CannyTracer() {}

    public Queue<Instruction> getInstructions(int[][] image) {
        if (image == null) return null;
        this.cannyMap = image;
        cannyToInstructions(3, 100);
        return instructions;
    }

    /** Iterates over whole image,
     * converts detected lines into instructions containing start and end point.
     * @param precision How long each instruction is.
     * @param thr How sensitive our line algorythm is.
     */
    public void cannyToInstructions(int precision, int thr) {
        for (int row = 0; row < cannyMap.length; row++)
            for (int col = 0; col < cannyMap[row].length; col++)
                if (cannyMap[row][col] > thr)
                    instructions.add(cannyRecursiveTrace(row, col, row, col, precision, thr));
    }

    /** Slave method for cannyToInstructions */
    private Instruction cannyRecursiveTrace(int startRow, int startCol, int row, int col, int precision, int thr) {
        String dir = findGreatestNeighbour(row, col);
        int r = 0; int c = 0;
        if (dir.startsWith("Top")) r--;
        else if (dir.startsWith("Bot")) r++;
        if (dir.startsWith("Lef")) c--;
        else if (dir.startsWith("Rig")) c++;
        cannyMap[row][col] = -1;
        if (Math.sqrt(Math.pow(startCol-col,2) + Math.pow(startRow-row,2)) < precision) {
            if(cannyMap[row+r][col+c] > thr) {
                cannyEraseGrid(row, col); // Been here now.
                Instruction instruction = cannyRecursiveTrace(startRow, startCol, row+r, col+c, precision, thr);
                if (instruction != null) return instruction;
            }
        }
        return new Instruction(new Cord(startCol, startRow), new Cord(startCol, startRow));
    }

    /** Returns a string identifying the greatest value neighbour.
     * Thought this was a rather creative solution.
     * @param row row
     * @param col col
     * **/
    private String findGreatestNeighbour(int row, int col) {
        String max = ""; int maxVal = 0;
        if (row > 0 && col > 0) if (cannyMap[row - 1][col-1] > maxVal) max = "TopLef";
        if (row > 0) if (cannyMap[row - 1][col] > maxVal) max = "TopMid";
        if (row > 0 && col < cannyMap[row].length) if (cannyMap[row - 1][col+1] > maxVal) max = "TopRig";
        if (col > 0) if (cannyMap[row][col - 1] > maxVal) max = "MidLef";
//        if (cannyMap[row][col] > maxVal) max = "MidMid"; // Uneeded :P
        if (col < cannyMap[row].length) if (cannyMap[row][col + 1] > maxVal) max = "MidRig";
        if (row < cannyMap.length - 1 && col > 0) if (cannyMap[row + 1][col - 1] > maxVal) max = "BotLef";
        if (row < cannyMap.length - 1) if (cannyMap[row + 1][col] > maxVal) max = "BotMid";
        if (row < cannyMap.length - 1 && col < cannyMap[row].length) if (cannyMap[row + 1][col + 1] > maxVal) max = "BotRig";
        return max;
    }

    /** Erases all touching pixels */
    private void cannyEraseGrid(int row, int col) {
        for (int r=-1; r<2; r++)
            for (int c=-1; c<2; c++)
                cannyMap[row+r][col+c] = -1;
    }
}