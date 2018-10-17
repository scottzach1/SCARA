public class Instruction {
    public Cord start, end;

    public Instruction(Cord start, Cord end) {
        this.start = start;
        this.end = end;
    }

    public String toString() {
        return start + " -> " + end;
    }
}
