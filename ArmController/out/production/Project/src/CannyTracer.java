import ecs100.UI;

import java.util.*;

public class  CannyTracer {
    private int[][] cannyMap;
    private Cord start, current, end; // Cords use (x, y) or (col, row) ## Important to note!
    private int thr;

    private Queue<Instruction> instructions;
    private ArrayList<Cord> travel;

    public CannyTracer() {
        this.thr = 20;
        instructions = new ArrayDeque<>();
        travel = new ArrayList<>();
    }

    public Queue<Instruction> cannyTrace(int[][] imageData) {
        this.cannyMap = imageData;
            current = new Cord(0,0); start = new Cord(0, 0); end = new Cord(0, 0);
        for (current.x = 0; current.x < cannyMap[0].length; current.x++) {      // col
            for (current.y = 0; current.y < cannyMap.length; current.y++) {             // row
                    if (this.cannyMap[(int) current.y][(int) current.x] > thr) {
                        travel.clear();
                        start = current.copy();
                        end = current.copy();
                        travel.add(start);
                        int c = 0;
                        while (findGreatestNeighbour() && checkStraightDistanceFromLine()) { c++; }
//                        UI.println(findGreatestNeighbour() + " " + checkStraight() + " " + c + " " + start.distanceFrom(end));
                        instructions.add(new Instruction(start, end));
                    }
                }
            }
//            UI.println("Done");
        fixInstructions();
        return instructions;
    }

    private void fixInstructions() {
        Set<Instruction> set = new HashSet<>();
        Stack<Instruction> stack = new Stack<>();

        stack.push(instructions.poll());

        while (!instructions.isEmpty()) set.add(instructions.poll());

        while (!set.isEmpty()) {
            double closestDistance=1000; Instruction closestInst = null;
            Cord end = stack.peek().end;
            for (Instruction inst : set) {
                if (inst.start.distanceFrom(end) > inst.end.distanceFrom(end)) inst.switchEnds();
                double distance = inst.start.distanceFrom(end);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestInst = inst;
                }
            }
            set.remove(closestInst);
            if (closestInst.start.distanceFrom(stack.peek().end) < 3) closestInst.start = stack.peek().end;
            stack.push(closestInst);
        }
        Collections.reverse(stack);
        while(!stack.empty()) instructions.add(stack.pop());
    }

    private boolean findGreatestNeighbour() {
        int r = 0, c = 0, maxVal = 0;
        int row = (int) end.y, col = (int) end.x;
        if (row>0&&col>0)if(cannyMap[row-1][col-1]>maxVal){r=-1;c=-1;maxVal=cannyMap[row+r][col+c];}//TopLeft
        if (row>0)if(cannyMap[row-1][col]>maxVal){r=-1;c=0;maxVal=cannyMap[row+r][col+c];}//TopMid
        if (row>0&&col<cannyMap[row].length)if(cannyMap[row-1][col+1]>maxVal){r=-1;c=1;maxVal=cannyMap[row+r][col+c];}//TopRight
        if (col>0)if(cannyMap[row][col-1]>maxVal){r=0;c=-1;maxVal=cannyMap[row+r][col+c];}//MidLef
        if (col<cannyMap[row].length)if(cannyMap[row][col+1]>maxVal){r=0;c=1;maxVal=cannyMap[row+r][col+c];}//MidRig
        if (row<cannyMap.length-1&&col>0)if(cannyMap[row+1][col-1]>maxVal){r=1;c=-1;maxVal=cannyMap[row+r][col+c];}//BotLef
        if (row<cannyMap.length-1)if(cannyMap[row+1][col]>maxVal){r=1;c=0;maxVal=cannyMap[row+r][col+c];}//BotMid
        if (row<cannyMap.length-1&&col<cannyMap[row].length)if(cannyMap[row+1][col+1]>maxVal){r=1;c=1;maxVal=cannyMap[row+r][col+c];}//BotRig

        if (maxVal < thr) return false; // No white neighbours were found.

//        cannyMap[row + r][col + c] = -1; // Erase current location.
        cannyEraseGrid(row, col, 1);

        travel.add(end); // Add current location to travel list.
        end = end.add(new Cord(c, r)); // Move to next location.

        return true; // Assure safe to continue.
    }

    public boolean checkStraightTotalDistance() {
        double totalDistance = 0;

        for(int i=0; i<travel.size()-1; i++)
            totalDistance += travel.get(i).distanceFrom(travel.get(i+1)); // Add distance from all points.

        totalDistance += travel.get(travel.size()-1).distanceFrom(end); // Add distance from last point to end.

        return (totalDistance <= start.distanceFrom(end)*1.08);
    }

    public boolean checkStraightDistanceFromLine() {
        int misses = 0;

        double rise, run, m;
        // Deal with edge cases.
        if (start.y == end.y) return true;// horizontal line
        if (start.x == end.x) return true; // vertical line
        if (end.x < start.x) end.swapValues(start);
        rise = end.y - start.y;
        run = end.x - start.x;
        m = rise / run;
//        UI.println("m: " + m);

        double y = start.y;
        double maxDistance = 5;
        boolean checked;
        for (int i = 0; i < travel.size(); i++) {
            checked = false;
            for (double x = start.x; x <= end.x && !checked; x++, y += m) {
                double distance = travel.get(i).distanceFrom(new Cord(x, y));
                if (distance > maxDistance) {
                    misses++;
                    checked = true;
                }
            }
        }
        if (misses > 5) return false;
        else return true;
    }

    /** Erases all touching pixels */
    private void cannyEraseGrid(int row, int col, int radius) {
        for (int r=-radius; r<radius+1; r++)
            for (int c=-radius; c<radius+1; c++)
                if(row+r>=0 && row+r<cannyMap.length && col+c>=0 && col+c<cannyMap[0].length)
                    cannyMap[row+r][col+c] = -1;
    }
}