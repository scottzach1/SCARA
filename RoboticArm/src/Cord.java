public class Cord {
    public double x, y;

    public  Cord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** Returns distance from another point using distance formula. Could be useful. */
    public double distanceFrom(Cord other) {
        return Math.sqrt(Math.pow(this.x - other.x,2) + Math.pow(this.y - other.y,2));
    }

    /** Returns Pythagoras Distance, c = root (a^2 + b^2) */
    public double getDistance() {
        return Math.sqrt(x*x + y*y);
    }
}
