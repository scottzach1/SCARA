public class ArmController {

    private double thetaLeft, thetaRight;

    // Calibration

    private double radius = 517 - 273;

    private Cord motorPoint1 = new Cord(1300, 1400);
    private Cord thetaPoint1 = new Cord(130, 70);

    private Cord motorPoint2 = new Cord(1500, 1600);
    private Cord thetaPoint2 = new Cord(128, 54);

    private Cord leftMotor = new Cord(312.0, 472.0);
    private Cord rightMotor = new Cord(439.0, 476.0);

    // PWM Fields:

    public ArmController() {

//        drawCircle(320, 240, 20, 150);
//        drawHorizontalLine(240, 240, 320);

        calculateMotorSignal(400, 240);
//        printData();

    }

    public void calculateTheta(double toolX, double toolY) {
        Cord tool = new Cord(toolX, toolY);

        double angle;

        // Motor 1:

        Cord distance1 = new Cord(tool.x - leftMotor.x, tool.y - leftMotor.y);
//        double distanceX1 = tool.x - leftMotor.x;
//        double distanceY1 = tool.y - leftMotor.y;

        Cord a = new Cord((tool.x + leftMotor.x)/2, (tool.y + leftMotor.y)/2);
//        double xA, yA;
//        xA = (tool.x + leftMotor.x)/2;
//        yA = (tool.y + leftMotor.y)/2;

//        double distance1 = Math.sqrt(Math.pow(distanceX1,2) + Math.pow(distanceY1,2)); // Can be achieved with distance1.getDistance()
        double h1 = Math.sqrt(Math.pow(radius,2) - Math.pow(distance1.getDistance()/2, 2));

        angle = Math.atan2(distance1.y, distance1.x);

        Cord joint1 = new Cord(a.x + h1*Math.sin(angle), a.y - h1*Math.cos(angle));
//        double xJoint1 = a.x + h1*Math.sin(angle);
//        double yJoint1 = a.y - h1*Math.cos(angle);

        thetaLeft = Math.PI/2 - Math.atan2(joint1.x - leftMotor.x, leftMotor.y - joint1.y);

        // Motor 2:

        Cord distance2 = new Cord(tool.x - rightMotor.x, tool.y - rightMotor.y);
//        double distanceX2 = tool.x - rightMotor.x;
//        double distanceY2 = tool.y - rightMotor.y;

        a.x = (tool.x + rightMotor.x)/2;
        a.y = (tool.y + rightMotor.y)/2;

//        double distance2 = Math.sqrt(Math.pow(distanceX2,2) + Math.pow(distanceY2,2)); // Can be achieved with distance2.getDistance()
        double h2 = Math.sqrt(Math.pow(radius,2) - Math.pow(distance2.getDistance()/2, 2));

        angle = Math.atan2(distance2.y, distance2.x);

        Cord joint2 = new Cord(a.x - h2*Math.sin(angle), a.y + h2*Math.cos(angle));
//        double xJoint2 = a.x - h2*Math.sin(angle);
//        double yJoint2 = a.y + h2*Math.cos(angle);

        thetaRight = Math.PI/2 - Math.atan2(joint2.x - rightMotor.x, rightMotor.y - joint2.y);

        // Output:

        System.out.println("xJoint1: " + joint1.x);
        System.out.println("yJoint1: " + joint1.y);

        System.out.println("xJoint2: " + joint2.x);
        System.out.println("yJoint2: " + joint2.y);

    }

    /** Use theta calculated in calculateTheta and convert this into PWM */
    public void calculateMotorSignal(double x, double y) {

        double gradient;

        calculateTheta(x, y);

        thetaLeft = Math.toDegrees(thetaLeft);
        thetaRight = Math.toDegrees(thetaRight);

        System.out.println("thetaLeft: " + thetaLeft);
        System.out.println("thetaRight: " + thetaRight);

        gradient = (motorPoint2.x - motorPoint1.x)/(thetaPoint2.x - thetaPoint1.x);
        System.out.println("g: "  + gradient);
        double pwmL = motorPoint1.x + (thetaLeft - thetaPoint1.x) * gradient;

        gradient = (motorPoint2.y - motorPoint1.y)/(thetaPoint2.y - thetaPoint1.y);
        System.out.println("g: "  + gradient);
        double pwmR = motorPoint1.y + (thetaRight - thetaPoint1.y) * gradient;

        System.out.println();
        System.out.println("PWM Left: " + pwmL);
        System.out.println("PWM Right: " + pwmR);

    }

    public static void main(String[] args) { new ArmController(); }
}
