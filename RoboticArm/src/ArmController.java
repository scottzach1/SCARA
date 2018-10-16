import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ArmController {

    // Calibration

    private double radius = 425-166;

    private double motor1Point1 = 1400, thetaMotor1Point1 = 142;
    private double motor1Point2 = 1650, thetaMotor1Point2 = 110;

    private double motor2Point1 = 1400, thetaMotor2Point1 = 66;
    private double motor2Point2 = 1650, thetaMotor2Point2 = 33;

    private Cord leftMotor = new Cord(231, 480);
    private Cord rightMotor = new Cord(363, 480);

    private int penHeight = 1300;

    private PrintStream stream;

    public ArmController() {

        double x = 320;
        double y = 100;

        double thetaLeft = calculateThetaLeft(x, y);
        double thetaRight = calculateThetaRight(x, y);

        System.out.println(radius);

        System.out.println(thetaLeft);
        System.out.println(thetaRight);

        System.out.println(calculatePWMMotor1(thetaLeft));
        System.out.println(calculatePWMMotor2(thetaRight));

        try {

            stream = new PrintStream("d");

//            drawLine(new Cord(260, 240), new Cord(380, 240));        // horizontal
//            drawLine(new Cord(320, 100), new Cord(320, 240));        // vertical
            drawCircle(new Cord(320, 100), 25, 100);       // circle
//            drawRectangle(new Cord(300, 160), new Cord(400, 80));    // rectangle

            stream.close();

        } catch (Exception e) { System.out.println("Exception " + e); }

    }

    /* Calculate the value of theta for the left motor */
    public double calculateThetaLeft(double toolX, double toolY) {

        Cord tool, distance, midpoint, joint;
        double h, angle, theta;

        tool = new Cord(toolX, toolY);
        distance = new Cord(tool.x - leftMotor.x, tool.y - leftMotor.y);
        midpoint = new Cord((tool.x + leftMotor.x)/2, (tool.y + leftMotor.y)/2);

        h = Math.sqrt(Math.pow(radius,2) - Math.pow(distance.getDistance()/2, 2));
        angle = Math.atan2(distance.y, distance.x);

        joint = new Cord(midpoint.x + h * sin(angle), midpoint.y - h * cos(angle));

        theta = Math.PI/2 - Math.atan2(joint.x - leftMotor.x, leftMotor.y - joint.y);

        return Math.toDegrees(theta);

    }

    /* Calculate the value of theta for the right motor */
    public double calculateThetaRight(double toolX, double toolY) {

        Cord tool, distance, midpoint, joint;

        double h, angle, theta;

        tool = new Cord(toolX, toolY);
        distance = new Cord(tool.x - rightMotor.x, tool.y - rightMotor.y);
        midpoint = new Cord((tool.x + rightMotor.x)/2, (tool.y + rightMotor.y)/2);

        h = Math.sqrt(Math.pow(radius,2) - Math.pow(distance.getDistance()/2, 2));
        angle = Math.atan2(distance.y, distance.x);

        joint = new Cord(midpoint.x - h * sin(angle), midpoint.y + h * cos(angle));

        theta = Math.PI/2 - Math.atan2(joint.x - rightMotor.x, rightMotor.y - joint.y);

        return Math.toDegrees(theta);

    }

    /* Calculate the PWM values for each motor given an angle */
    private double calculatePWMMotor1(double angle){ return motor1Point1 + ((angle-thetaMotor1Point1)*(motor1Point2-motor1Point1)/(thetaMotor1Point2-thetaMotor1Point1)); }
    private double calculatePWMMotor2(double angle){ return motor2Point1 + ((angle-thetaMotor2Point1)*(motor2Point2-motor2Point1)/(thetaMotor2Point2-thetaMotor2Point1)); }

    /////////////////////////////////////////////////////

    /* Draw a horizontal line */
    public void drawHorizontalLine(double startX, double endX, double y) {

        System.out.println("drawHorizontalLine");

            for (double x = startX; x < endX; x+=2) {

                double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
                double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));

                writeMotorCommand(pwmLeft, pwmRight);
//                printMotorCommand(pwmLeft, pwmRight);

            }
    }

    /* Draw a vertical line */
    public void drawVerticalLine(double startY, double endY, double x) {

        System.out.println("drawVerticalLine");

        for (double y = startY; y < endY; y += 2) {

            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));

            writeMotorCommand(pwmLeft, pwmRight);
//                printMotorCommand(pwmLeft, pwmRight);

        }
    }

    /* Draw a generic line */
    public void drawLine(Cord start, Cord end){

        double rise, run, m;

        if (start.equalTo(end)) return;

        if (start.y == end.y) {
            drawHorizontalLine(start.x, end.x,  start.y);
            return;
        }
        if (start.x == end.x) {
            drawVerticalLine(start.y, end.y, start.x);
            return;
        }

        if (end.x < start.x) end.swapValues(start);

        rise = end.y - start.y;
        run = end.x - start.x;
        m = rise / run;
        System.out.println("m: " + m);

        double y = start.y;
        for (double x = start.x; x <= end.x; x++) {

            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));

//            writeMotorCommand(pwmLeft, pwmRight);
            printMotorCommand(pwmLeft, pwmRight);

            y += m;
        }

    }

    public void drawCircle(Cord center, double radius, double accuracy) {

        for (int i = 0; i < accuracy; i++) {

            double angle = i * Math.PI * 2 / accuracy;
            double x = center.x + radius * cos(angle);
            double y = center.y + radius * sin(angle);

            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));

            writeMotorCommand(pwmLeft, pwmRight);
//            printMotorCommand(pwmLeft, pwmRight);

        }

    }

    public void drawRectangle(Cord topLeft, Cord botRight) {
        if (topLeft.x > botRight.x) topLeft.swapX(botRight);
        if (topLeft.y < botRight.y) topLeft.swapY(botRight);

        double x, y;

        y = topLeft.y; // Top
        for (x = topLeft.x; x < botRight.x; x+=2) {
            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));
            writeMotorCommand(pwmLeft, pwmRight);
//                printMotorCommand(pwmLeft, pwmRight);
        }
        x = botRight.x; // Right
        for (y = topLeft.y; y > botRight.y; y-=2) {
            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));
            writeMotorCommand(pwmLeft, pwmRight);
//                printMotorCommand(pwmLeft, pwmRight);
        }
        y = botRight.y; // Bot
        for (x = botRight.x; x > topLeft.x; x-=2) {
            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));
            writeMotorCommand(pwmLeft, pwmRight);
//                printMotorCommand(pwmLeft, pwmRight);
        }
        x = topLeft.x; // Left
        for (y = botRight.y; y < topLeft.y; y+=2) {
            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));
            writeMotorCommand(pwmLeft, pwmRight);
//                printMotorCommand(pwmLeft, pwmRight);
        }

    }

    public void writeMotorCommand(double pwmLeft, double pwmRight) {

        System.out.println(Math.round(pwmLeft) + "," + Math.round(pwmRight) + "," + penHeight);
        stream.println(Math.round(pwmLeft) + "," + Math.round(pwmRight) + "," + penHeight);

    }

    public void printMotorCommand(double pwmLeft, double pwmRight) {

        System.out.println(pwmLeft + "," + pwmRight + "," + penHeight);

    }

    public void togglePen() {
        if (penHeight == 1500) {
            penHeight = 1300;
        } else {
            penHeight = 1500;
        }
    }

    public static void main(String[] args) { new ArmController(); }

}