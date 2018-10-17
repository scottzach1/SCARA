import java.util.ArrayDeque;
import java.util.Queue;

public class CannyTracer {
    private int[][] cannyMap;
    private int startCol, startRow, currentRow, currentCol, endRow, endCol;
    private int precision, thr;
    private Queue<Instruction> instructions = new ArrayDeque<>();

    /** Constructor, takes an int array as @param image */
    public CannyTracer() {}

    public Queue<Instruction> getInstructions(int[][] image) {
        if (image == null) return null;
        this.cannyMap = image;
        this.precision = 4;
        this. thr = 100;
        cannyToInstructions();
        return instructions;
    }

    /** Iterates over whole image,
     * converts detected lines into instructions containing start and end point.
     */
    public void cannyToInstructions() {
        for (currentCol = 0; currentCol < cannyMap[0].length; currentCol++) {
            for (currentRow = 0; currentRow < cannyMap.length; currentRow++) {

                if (cannyMap[currentRow][currentCol] > thr) {
                    startRow = currentRow;
                    startCol = currentCol;
                    endRow = currentRow;
                    endCol = currentCol;
                    instructions.add(cannyRecursiveTrace());
                    String dir = findGreatestNeighbour(endRow, endCol);
                    int r = 0;
                    int c = 0;
                    if (dir.startsWith("Top")) r--;
                    else if (dir.startsWith("Bot")) r++;
                    if (dir.startsWith("Lef")) c--;
                    else if (dir.startsWith("Rig")) c++;
                    cannyMap[endRow][endCol] = -1;
                    if (cannyMap[endRow + r][endCol + c] > thr) {
                        startRow = endRow + r;
                        startCol = endCol + c;
                        endRow = startRow;
                        endCol = startCol;
                        instructions.add(cannyRecursiveTrace());
                    }
                }
            }
        }
    }

    /** Slave method for cannyToInstructions */
    private Instruction cannyRecursiveTrace() {
        String dir = findGreatestNeighbour(endRow, endCol);
        int r = 0; int c = 0;
        if (dir.startsWith("Top")) r--;
        else if (dir.startsWith("Bot")) r++;
        if (dir.startsWith("Lef")) c--;
        else if (dir.startsWith("Rig")) c++;
        cannyMap[endRow][endCol] = -1;
        if (Math.sqrt(Math.pow(startCol-endCol,2) + Math.pow(startRow-endRow,2)) < precision) {
            if(cannyMap[endRow+r][endCol+c] > thr) {
                cannyEraseGrid(endRow, endCol); // Been here now.
                endRow += r; endCol += c;
                Instruction instruction = cannyRecursiveTrace();
                if (instruction != null) return instruction;
            }
        }
        return new Instruction(new Cord(startCol, startRow), new Cord(endCol, endRow));
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
            for (int c=-1; c<2; c++){}
//                cannyMap[row+r][col+c] = -1;
    }
}