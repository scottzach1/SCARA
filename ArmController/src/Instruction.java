public class Instruction {
    public Cord start, end;

    /** Constructor, create an object that stores 2 cords.
     * @param start
     * @param end
     */
    public Instruction(Cord start, Cord end) {
        this.start = start;
        this.end = end;
    }

    /** Returns a string that prints the start then end cord with a "->" as separation */
    public String toString() {
        return start + " -> " + end;
    }

    /** Switch the start and end cord location */
    public void switchEnds() {
        Cord holder = this.start;
        this.start = this.end;
        this.end = holder;
    }

    /** Calculate the distance from another instruction, checks both ends of both instructions. */
    public double distanceFrom(Instruction other) {
        double shortestDistance = this.start.distanceFrom(other.start);
        double distance = this.start.distanceFrom(other.end);
        if (distance < shortestDistance) shortestDistance = distance;
        distance = this.end.distanceFrom(other.start);
        if (distance < shortestDistance) shortestDistance = distance;
        distance = this.end.distanceFrom(other.end);
        if (distance < shortestDistance) shortestDistance = distance;
        return shortestDistance;
    }
}
