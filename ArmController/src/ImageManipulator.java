import ecs100.UI;
import ecs100.UIFileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Queue;
import java.util.Scanner;

public class ImageManipulator {

    private int rows, cols;
    private int[][] imageData;
    private Color[][] image;
    public boolean gui, console, debug;
    public static float LEFT = 15, TOP = 15, PX_SIZE = 3;

    public ImageManipulator(boolean gui, boolean console) {
        this.gui = gui;
        this.console = console;
        this.debug = false;
    }

    public int getRows() { return  rows; }
    public int getCols() { return  cols; }

    /** Slave for LoadImage method, @param Scanner, will return -1 if no +ve int was found.*/
    private int getNextScannerInt(Scanner sc) {
        while(sc.hasNext()) {
            if (sc.hasNextInt()) return sc.nextInt();
            else if(sc.next().equals("#")) sc.nextLine();
            else break; // Invalid non int token.
        }
        return -1; // No number could be found.
    }
    /** Slave for LoadImage method, @param Scanner, gets next color in image file. **/
    private Color getNextColour(Scanner sc, int maxColor) {
        int r = (int) ((double)(1.0*getNextScannerInt(sc)/maxColor*255));   //r
        int g = (int) ((double)(1.0*getNextScannerInt(sc)/maxColor*255));   //g
        int b = (int) ((double)(1.0*getNextScannerInt(sc)/maxColor*255));   //b
        if (r == -1 || g == -1 || b == -1) return null;
        return new Color(r, g, b);
    }

    /** Prompts user to select a file */
    public boolean loadData() {
        String fileName = UIFileChooser.open();
        if (console && gui) UI.println(fileName);
        if (debug) System.out.println(fileName);
        if (fileName.endsWith(".ppm")) return loadPPM(fileName);
        else return loadImage(fileName);
    }

    /** Loads a ppm image to the program. Takes @param fName and returns true if successfully loaded */
    public boolean loadPPM(String fileName) {
        try {
            Scanner sc = new Scanner(new File(fileName));
            if (!sc.next().equals("P3")) { UI.println("Invalid Image Type."); return false; }

            cols = getNextScannerInt(sc);
            rows = getNextScannerInt(sc);
            image = new Color[rows][cols];
            imageData = new int[rows][cols];
            int maxColor = getNextScannerInt(sc);

            for (int row = 0; row < rows; row++)
                for (int col = 0; col < cols; col++)
                    image[row][col] = getNextColour(sc, maxColor);

            if (gui && console) UI.printf("%dx%d image loaded successfully\n", rows, cols);
            if (debug) System.out.printf("%dx%d image loaded successfully\n", rows, cols);
            sc.close();
            return true;

        } catch (Exception e) {
            if (gui && console) UI.println("Exception " + e);
            System.out.println("Exception " + e);
        }
        return false; // If Exception occured.
    }
    /** Loads a bitmap image to the program. Takes @param fName and returns true if successfully loaded */
    public boolean loadImage(String fileName) {
        try {
            BufferedImage img = ImageIO.read(new File(fileName));
            rows = img.getHeight();
            cols = img.getWidth();
            image = new Color[rows][cols];
            imageData = new int[rows][cols];

            for (int row = 0; row < image.length; row++)
                for (int col = 0; col < image[row].length; col++)
                    image[row][col] = new Color(img.getRGB(col, row));

            if (gui && console) UI.printf("%dx%d image loaded successfully\n", rows, cols);
            if (debug) System.out.printf("%dx%d image loaded successfully\n", rows, cols);
            return true;

        } catch (Exception e) {
            if (gui && console) UI.println("Exception " + e);
            System.out.println("Exception " + e);
        }
        return false;
    }

    /** Combines vertical and horizontal edge detection on image[][]. Saves to imageData[][] */
    public void edgeDetection(int thr) {
        int[][] verticalEdges = edgeDetectionSlave(true, thr);
        int[][] horizontalEdges = edgeDetectionSlave(false, thr);
        int max = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                imageData[row][col] = (int) Math.sqrt(Math.pow(verticalEdges[row][col], 2) + Math.pow(horizontalEdges[row][col], 2));
                if (imageData[row][col] > max) max = imageData[row][col];
            }
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
//                imageData[row][col] = (int) ((1.0 * imageData[row][col] / max) * 255); // Normalise Data
//                if (imageData[row][col] > 150) imageData[row][col]+=50;               // Amplify Peaks
                if (imageData[row][col] > 255) imageData[row][col] = 255;               // Trim > 255
            }
        }
    }
    /** Runs edge detection on image[][], @param vertical tells us which direction, used as a slave for edgeDetection() */
    private int[][] edgeDetectionSlave(boolean vertical, int thr) {

        int[][] sobel, output = new int[rows][cols];

        if(vertical) sobel = new int[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        else sobel = new int[][]{{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};

        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < cols - 1; col++) {

                int[][] newSobel = new int[sobel.length][sobel[0].length];

                for (int r = 0; r < 3; r++)
                    for (int c = 0; c < 3; c++)
                        newSobel[r][c] = sobel[r][c] * getLuminosityFromColor(image[row - 1 + r][col - 1 + c]);

                int sum = 0;
                for (int[] r : newSobel) for (int c : r) sum += c; // For every value of every cell add to total
                if (sum > thr) output[row][col] = sum;
            }
        }
        return output;
    }

    public void imageToData() {
        for (int row = 0; row < image.length; row++) {
            for (int col = 0; col < image[row].length; col++) {
                imageData[row][col] = 255 - getLuminosityFromColor(image[row][col]);
            }
        }
    }

    /** Returns a luminosity / 255 given an @param color c. */
    private int getLuminosityFromColor(Color c) {
//        https://stackoverflow.com/questions/596216/formula-to-determine-brightness-of-rgb-color
//        (0.2126*R + 0.7152*G + 0.0722*B)
        return (int) (c.getRed()*0.2126 + c.getGreen()*0.7152 + c.getBlue()*0.0722);
    }

    /** Render the original image[][] on the display. gui must be enabled. */
    public void renderImage() {
        if (!gui || image == null) return;
        UI.clearGraphics();
        UI.setDivider(0.0);
        UI.setImmediateRepaint(false);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                UI.setColor(image[row][col]);
                UI.fillRect(LEFT + col * PX_SIZE, TOP + row * PX_SIZE, PX_SIZE, PX_SIZE);
            }
        }
        UI.repaintGraphics();
    }
    /** Render the new data extracted from image[][]. gui must be enabled. */
    public void renderData() {
        if (!gui || image == null) return;
        UI.clearGraphics();
        UI.setDivider(0.0);
        UI.setImmediateRepaint(false);

        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                if (imageData[row][col] > 255 || imageData[row][col] < 0)
                    UI.println(row + ", " + col + " exceeds maximum value 255: " + imageData[row][col]);
                else {
                    int intensity = imageData[row][col];
                    UI.setColor(new Color(intensity, intensity, intensity));
                    UI.fillRect(LEFT + col * ImageManipulator.PX_SIZE, TOP + row * ImageManipulator.PX_SIZE, ImageManipulator.PX_SIZE, ImageManipulator.PX_SIZE);
                }

        UI.repaintGraphics();
    }

    public void renderInstructions(Queue<Instruction> instructions) {
        UI.clearGraphics();
        UI.setImmediateRepaint(true);
//        UI.setDivider(0.0);
//        UI.println("Rendering " + instructions.size() + " instructions. Hopefully this works.");
        UI.setColor(Color.black);
        UI.fillRect(LEFT, TOP, imageData[0].length*PX_SIZE, imageData.length*PX_SIZE);
        UI.setColor(Color.white);
        while(!instructions.isEmpty()) {
            UI.setColor(new Color((int) (Math.random()*200+55), (int) (Math.random()*200+55), (int) (Math.random()*200+55)));
            Instruction instruction = instructions.poll();
            UI.sleep(150);
            UI.drawLine(LEFT + instruction.start.x*PX_SIZE, TOP + instruction.start.y*PX_SIZE, LEFT + instruction.end.x*PX_SIZE, TOP + instruction.end.y*PX_SIZE);
        }
    }

    /** Return a Queue of Instructions to draw image */
    public Queue<Instruction> getInstructions() {
        CannyTracer tracer = new CannyTracer();
        Queue<Instruction>  instructions = tracer.cannyTrace(imageData);
//        UI.println(instructions.size() + " instructions were found.");
        return instructions;
    }
}