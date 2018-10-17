public class Instruction {
    public Cord start, end;

    public Instruction(Cord start, Cord end) {
        this.start = start;
        this.end = end;
    }

    public String toString() {
        return start + " -> " + end;
    }

    public void switchEnds() {
        Cord holder = this.start;
        this.start = this.end;
        this.end = holder;
    }

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
