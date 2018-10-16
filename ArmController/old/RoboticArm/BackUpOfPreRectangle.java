import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ArmController {

    // Calibration

    private double radius = 261;

    double motor1Point1 = 1300, thetaMotor1Point1 = 135;
    double motor1Point2 = 1700, thetaMotor1Point2 = 98;

    double motor2Point1 = 1300, thetaMotor2Point1 = 87;
    double motor2Point2 = 1700, thetaMotor2Point2 = 50;

    private Cord leftMotor = new Cord(220, 500);
    private Cord rightMotor = new Cord(390, 500);

    public ArmController() {

//        double x = 295;
//        double y = 142;
//
//        double thetaLeft = calculateThetaLeft(x, y);
//        double thetaRight = calculateThetaRight(x, y);
//
//        thetaLeft = Math.toDegrees(thetaLeft);
//        thetaRight = Math.toDegrees(thetaRight);
//
//        System.out.println(thetaLeft);
//        System.out.println(thetaRight);
//
//        System.out.println(calculatePWMMotor1(thetaLeft));
//        System.out.println(calculatePWMMotor2(thetaRight));

        drawHorizontalLine(240, 340, 240);


    }

    public double calculateThetaLeft(double toolX, double toolY) {

        Cord tool, distance, midpoint, joint;
        double h, angle, theta;

        tool = new Cord(toolX, toolY);
        distance = new Cord(tool.x - leftMotor.x, tool.y - leftMotor.y);
        midpoint = new Cord((tool.x + leftMotor.x)/2, (tool.y + leftMotor.y)/2);

        h = Math.sqrt(Math.pow(radius,2) - Math.pow(distance.getDistance()/2, 2));
        angle = Math.atan2(distance.y, distance.x);

        joint = new Cord(midpoint.x + h * Math.sin(angle), midpoint.y - h * Math.cos(angle));

        theta = Math.PI/2 - Math.atan2(joint.x - leftMotor.x, leftMotor.y - joint.y);

        return theta;

    }

    public double calculateThetaRight(double toolX, double toolY) {

        Cord tool, distance, midpoint, joint;

        double h, angle, theta;

        tool = new Cord(toolX, toolY);
        distance = new Cord(tool.x - rightMotor.x, tool.y - rightMotor.y);
        midpoint = new Cord((tool.x + rightMotor.x)/2, (tool.y + rightMotor.y)/2);

        h = Math.sqrt(Math.pow(radius,2) - Math.pow(distance.getDistance()/2, 2));
        angle = Math.atan2(distance.y, distance.x);

        joint = new Cord(midpoint.x - h * Math.sin(angle), midpoint.y + h * Math.cos(angle));

        theta = Math.PI/2 - Math.atan2(joint.x - rightMotor.x, rightMotor.y - joint.y);

        return theta;

    }

    private double calculatePWMMotor1(double angle){

        double pwm1 = motor1Point1 + ((angle-thetaMotor1Point1)*(motor1Point2-motor1Point1)/(thetaMotor1Point2-thetaMotor1Point1));

//        System.out.println(pwm1);

        return pwm1;

    }

    private double calculatePWMMotor2(double angle){

        double pwm2 = motor2Point1 + ((angle-thetaMotor2Point1)*(motor2Point2-motor2Point1)/(thetaMotor2Point2-thetaMotor2Point1));

//        System.out.println(pwm2);

        return pwm2;

    }

    /////////////////////////////////////////////////////

    public void drawHorizontalLine(double startX, double endX, double y) {

        try {

            PrintWriter writer = new PrintWriter(new File("drawHorizontalLine.txt"));

            for (double x = startX; x < endX; x+=2) {

                double thetaLeft = Math.toDegrees(calculateThetaLeft(x, y));
                double thetaRight = Math.toDegrees(calculateThetaRight(x, y));

                double pwmLeft = calculatePWMMotor1(thetaLeft);
                double pwmRight = calculatePWMMotor2(thetaRight);

                System.out.println(Math.round(pwmLeft) + "," + Math.round(pwmRight) + ",1500");

                writer.println(Math.round(pwmLeft) + "," + Math.round(pwmRight) + ",1500");
                writer.flush();

            }

        } catch (IOException e ) {
            System.out.println("ERROR: " + e);
        }

    }

    public static void main(String[] args) { new ArmController(); }

}