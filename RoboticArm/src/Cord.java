public class Cord {
    public double x, y;

    public  Cord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distanceFrom(Cord other) {
        return Math.sqrt(Math.pow(this.x - other.x,2) + Math.pow(this.y - other.y,2));
    }

    public double getDistance() {
        return Math.sqrt(x*x + y*y);
    }
}
