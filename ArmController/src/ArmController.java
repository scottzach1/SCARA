import ecs100.UI;
import ecs100.UIFileChooser;

import java.io.File;
import java.io.PrintStream;
import java.util.Queue;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.round;

public class ArmController {

    private double radius = 425-166;

    private double motor1Point1 = 1400, thetaMotor1Point1 = 142;
    private double motor1Point2 = 1650, thetaMotor1Point2 = 110;

    private double motor2Point1 = 1400, thetaMotor2Point1 = 66;
    private double motor2Point2 = 1650, thetaMotor2Point2 = 33;

    private Cord leftMotor = new Cord(231, 480);
    private Cord rightMotor = new Cord(363, 480);

    private boolean debug = false, console = false, output = true, gui = false;
    private int penHeight = 1300;

    private ImageManipulator imageManipulator;
    private PrintStream stream;
    private String fName = "instructions.scara";

    public ArmController() {
        setupGUI();
//        calibrate();
        try {
            stream = new PrintStream(fName);

//            drawLine(new Cord(260, 240), new Cord(380, 240));        // horizontal
//            drawLine(new Cord(320, 100), new Cord(320, 240));        // vertical
//            drawCircle(new Cord(320, 100), 25, 100);       // circle
//            drawRectangle(new Cord(300, 160), new Cord(400, 80));    // rectangle

        } catch (Exception e) { System.out.println("Error " + e); }
    }

    /** Sets up GUI using ECS library */
    public void setupGUI() {
        gui = true;
        imageManipulator = new ImageManipulator(gui, console);
        UI.initialise();
        UI.addButton("Load Image", imageManipulator::loadData);
        UI.addButton("Render Image", ()-> imageManipulator.renderImage(15, 15, 1));
        UI.addButton("Run Edge Detection", ()-> imageManipulator.edgeDetection(100));
        UI.addButton("Render Data", ()-> imageManipulator.renderData(15, 15, 1));
        UI.addButton("Draw Image", imageManipulator::getInstructions);
        UI.addButton("Set Output File", ()-> fName = UIFileChooser.save());

        UI.addButton("Check Motor Calibration", this::calibrate);
        UI.addButton("Draw Circle", this::selectCircle);
        UI.addButton("Draw Rectangle", this::selectRectangle);
        UI.addButton("Draw line", this::selectLine);
        UI.addButton("Quit", UI::quit);
        if (this.console) UI.setDivider(0.5);
        else UI.setDivider(0);
    }

    /** Output the current calibration data */
    public void calibrate() {

        double x = 320;
        double y = 100;

        double thetaLeft = calculateThetaLeft(x, y);
        double thetaRight = calculateThetaRight(x, y);

        if (gui) {

            UI.println("------------------------------------------------------------------------");
            UI.println("Results for tool at:  x = " + x + " and y = " + y);

            UI.println("Radius: " + radius);

            UI.println("Theta left (degrees): " + thetaLeft);
            UI.println("Theta right (degrees): " + thetaRight);

            UI.println("PWM left: " + calculatePWMMotor1(thetaLeft));
            UI.println("PWM right: " + calculatePWMMotor2(thetaRight));

            UI.println("------------------------------------------------------------------------");

        } else if (debug) {
            System.out.println("Radius: " + radius);

            System.out.println("Theta left (degrees): " + thetaLeft);
            System.out.println("Theta right (degrees): " + thetaRight);

            System.out.println("PWM left: " + calculatePWMMotor1(thetaLeft));
            System.out.println("PWM right: " + calculatePWMMotor2(thetaRight));
        }

    }

    /** Calculate the value of theta for each motor */
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

    /** Calculate the PWM values for each motor given an angle */
    private double calculatePWMMotor1(double angle){ return motor1Point1 + ((angle-thetaMotor1Point1)*(motor1Point2-motor1Point1)/(thetaMotor1Point2-thetaMotor1Point1)); }
    private double calculatePWMMotor2(double angle){ return motor2Point1 + ((angle-thetaMotor2Point1)*(motor2Point2-motor2Point1)/(thetaMotor2Point2-thetaMotor2Point1)); }

    /** Allows the user to enter custom circle dimensions/position */
    public void selectCircle() {

        if (gui) {
            double x = UI.askInt("Enter the x position of the circle center: ");
            double y = UI.askInt("Enter the y position of the circle center: ");
            double radius = UI.askInt("Enter the circle radius: ");
            double accuracy = UI.askInt("Enter the desired step count: ");

            Cord circlePos = new Cord(x, y);

            drawCircle(circlePos, radius, accuracy);

            UI.println("Outputted file to: " + fName);
        }

    }

    /** Allows the user to enter custom rectangle dimensions/position */
    public void selectRectangle() {

        if (gui) {
            double x1 = UI.askInt("Enter the x position of the top left corner: ");
            double y1 = UI.askInt("Enter the y position of the top left corner: ");

            double x2 = UI.askInt("Enter the x position of the bottom right corner: ");
            double y2 = UI.askInt("Enter the y position of the bottom right corner: ");

            Cord topLeft = new Cord(x1, y1);
            Cord bottomRight = new Cord(x2, y2);

            drawRectangle(topLeft, bottomRight);

            UI.println("Outputted file to: " + fName);
        }

    }

    /** Allows the user to enter custom line positions */
    public void selectLine() {

        if (gui) {
            double x1 = UI.askInt("Enter the x position of the start point: ");
            double y1 = UI.askInt("Enter the y position of the start point: ");

            double x2 = UI.askInt("Enter the x position of the end point: ");
            double y2 = UI.askInt("Enter the y position of the end point: ");

            Cord start = new Cord(x1, y1);
            Cord end = new Cord(x2, y2);

            drawLine(start, end);

            UI.println("Outputted file to: " + fName);
        }

    }

    /** Draw a horizontal line */
    public void drawHorizontalLine(double startX, double endX, double y) {
        if (debug) System.out.println("drawHorizontalLine");
        for (double x = startX; x < endX; x+=2) {
            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));
            writeMotorCommand(pwmLeft, pwmRight);
        }
    }

    /** Draw a vertical line */
    public void drawVerticalLine(double startY, double endY, double x) {
        if (debug)System.out.println("drawVerticalLine");
        for (double y = startY; y < endY; y += 2) {
            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));
            writeMotorCommand(pwmLeft, pwmRight);
        }
    }

    /** Draw a generic line */
    public void drawLine(Cord start, Cord end){
        double rise, run, m;
        // Deal with edge cases.
        if (start.equalTo(end)) return;
        if (start.y == end.y) { drawHorizontalLine(start.x, end.x,  start.y); return; }
        if (start.x == end.x) { drawVerticalLine(start.y, end.y, start.x); return; }
        if (end.x < start.x) end.swapValues(start);
        rise = end.y - start.y;
        run = end.x - start.x;
        m = rise / run;
        if (debug) System.out.println("m: " + m);
        double y = start.y;
        for (double x = start.x; x <= end.x; x++, y += m) {
            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));
            writeMotorCommand(pwmLeft, pwmRight);
        }
    }

    /** Draw a circle around center point. Accuracy tells us step size. */
    public void drawCircle(Cord center, double radius, double accuracy) {
        double angle, x, y, pwmLeft, pwmRight;
        for (int i = 0; i < accuracy; i++) {
            angle = i * Math.PI * 2 / accuracy;
            x = center.x + radius * cos(angle);
            y = center.y + radius * sin(angle);
            pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));
            writeMotorCommand(pwmLeft, pwmRight);
        }
    }

    /** Draws a rectangle out of the two points, auto detects corners.
     * Draws ClockWise starting from top left corner. */
    public void drawRectangle(Cord topLeft, Cord botRight) {
        if (topLeft.x > botRight.x) topLeft.swapX(botRight);
        if (topLeft.y < botRight.y) topLeft.swapY(botRight);
        double x, y;
        for (y = topLeft.y, x = topLeft.x; x < botRight.x; x+=2) {              // Top Line
            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));
            writeMotorCommand(pwmLeft, pwmRight);
        }
        for (x = botRight.x, y = topLeft.y; y > botRight.y; y-=2) {             // Right Line
            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));
            writeMotorCommand(pwmLeft, pwmRight);
        }
        for (y = botRight.y, x = botRight.x; x > topLeft.x; x-=2) {             // Bottom Line
            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));
            writeMotorCommand(pwmLeft, pwmRight);
        }
        for (x = topLeft.x, y = botRight.y; y < topLeft.y; y+=2) {              // Left Line
            double pwmLeft = calculatePWMMotor1(calculateThetaLeft(x, y));
            double pwmRight = calculatePWMMotor2(calculateThetaRight(x, y));
            writeMotorCommand(pwmLeft, pwmRight);
        }
    }

    /** Outputs next command via PrintStream, will also output to console if debug = true */
    public void writeMotorCommand(double pwmLeft, double pwmRight) {
        if (output) stream.println(round(pwmLeft) + "," + round(pwmRight) + "," + penHeight);
        if (debug) System.out.println(round(pwmLeft) + "," + round(pwmRight) + "," + penHeight);
    }

    /** Switches pen writing state depending on @param up. */
    public void setPen(Boolean up) {
        if (up)  penHeight = 1300;
        else     penHeight = 1500;
    }

    /** Draw Instructions */
    public void drawInstructions (Queue<Instruction> instructions) {
        if (instructions == null) return;
        Instruction instruction;
        while (!instructions.isEmpty()) {
            instruction = instructions.poll();
            // NOTE: We will need to do some sorcery to draw these x, y coordinates at a suitable location on the real canvas.
            drawLine(instruction.start, instruction.end);
        }
    }

    public static void main(String[] args) { ArmController Arm = new ArmController(); }
}
