import java.io.File;
import java.io.PrintWriter;

import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

public class BackUpOfArthur {

    double thetaLeft, thetaRight;

    // Calibration

    double r = 517 - 273;

    double motorLeftPoint1 = 1300;
    double motorRightPoint1 = 1400;
    double thetaLeftPoint1 = 130;
    double thetaRightPoint1 = 70;

    double motorLeftPoint2 = 1500;
    double motorRightPoint2 = 1600;
    double thetaLeftPoint2 = 128;
    double thetaRightPoint2 = 54;




    // Left

    double xMotor1 = 312.0;
    double yMotor1 = 472.0;

    // Right

    double xMotor2 = 439.0;
    double yMotor2 = 476.0;

    // PWM Fields:

    public ArmController() {

//        drawCircle(320, 240, 20, 150);
//        drawHorizontalLine(240, 240, 320);

        calculateMotorSignal(400, 240);
//        printData();

    }

    public void calculateTheta(double toolX, double toolY) {

        double xA, yA;
        double alpha;

        // Motor 1:

        double distanceX1 = toolX - xMotor1;
        double distanceY1 = toolY - yMotor1;

        xA = 0.5 * (toolX + xMotor1);
        yA = 0.5 * (toolY + yMotor1);

        double distance1 = Math.sqrt(distanceX1 * distanceX1 + distanceY1 * distanceY1);
        double h1 = Math.sqrt((r*r) - Math.pow(distance1/2, 2));

        alpha = Math.atan2(distanceY1, distanceX1);

        double xJoint1 = xA + h1*Math.sin(alpha);
        double yJoint1 = yA - h1*Math.cos(alpha);

        thetaLeft = Math.PI/2 - Math.atan2(xJoint1 - xMotor1, yMotor1 - yJoint1);

        // Motor 2:

        double distanceX2 = toolX - xMotor2;
        double distanceY2 = toolY - yMotor2;

        xA = 0.5 * (toolX + xMotor2);
        yA = 0.5 * (toolY + yMotor2);

        double distance2 = Math.sqrt(distanceX2 * distanceX2 + distanceY2 * distanceY2);
        double h2 = Math.sqrt((r*r) - Math.pow(distance2/2, 2));

        alpha = Math.atan2(distanceY2, distanceX2);

        double xJoint2 = xA - h2*Math.sin(alpha);
        double yJoint2 = yA + h2*Math.cos(alpha);

        thetaRight = Math.PI/2 - Math.atan2(xJoint2 - xMotor2, yMotor2 - yJoint2);

        // Output:

        System.out.println("xJoint1: " + xJoint1);
        System.out.println("yJoint1: " + yJoint1);

        System.out.println("xJoint2: " + xJoint2);
        System.out.println("yJoint2: " + yJoint2);

    }

    /*
     * Use theta calculated in calculateTheta and convert this into PWM
     */

    public void calculateMotorSignal(double x, double y) {

        double gradient;

        calculateTheta(x, y);

        thetaLeft = Math.toDegrees(thetaLeft);
        thetaRight = Math.toDegrees(thetaRight);

        System.out.println("thetaLeft: " + thetaLeft);
        System.out.println("thetaRight: " + thetaRight);

        gradient = (motorLeftPoint2 - motorLeftPoint1)/(thetaLeftPoint2 - thetaLeftPoint1);
        System.out.println("g: "  + gradient);
        double pwmL = motorLeftPoint1 + (thetaLeft - thetaLeftPoint1) * gradient;

        gradient = (motorRightPoint2 - motorRightPoint1)/(thetaRightPoint2 - thetaRightPoint1);
        System.out.println("g: "  + gradient);
        double pwmR = motorRightPoint1 + (thetaRight - thetaRightPoint1) * gradient;

        System.out.println();
        System.out.println("PWM Left: " + pwmL);
        System.out.println("PWM Right: " + pwmR);

    }

    /*
     * Calculate PWM values for a circle
     */

    public void printData() {
        for (int x=1400; x<=1700; x+=50)
            for (int y=1400; y<=1700; y+=50) {
                System.out.println(x + "," + y + ", 1500");
            }
    }

//    public void drawCircle(int xCenter, int yCenter, int radius, int accuracy) {
//
//        System.out.println("Drawing circle");
//
//        try {
//
//
//            for (int i = 0; i < accuracy; i++) {
//
//                double angle =  i * Math.PI * 2 / accuracy;
//                double x = xCenter + radius * cos(angle);
//                double y = yCenter + radius * sin(angle);
//
//                calculateMotorSignal(x,y);
//
////                System.out.println(round(pwmLeft) + "," + round(pwmRight) + "," + "1500");
//
//                writer.println(round(pwmLeft) + "," + round(pwmRight) + "," + "1500");
//
//            }
//
//            writer.close();
//
//        } catch (Exception e ) {
//
//            System.out.println("ERROR: " + e);
//
//        }
//
//    }

    public static void main(String[] args) {

        new BackUpOfArthur();

    }

}
