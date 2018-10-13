import ecs100.UI;
import ecs100.UIFileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public class ImageManipulator {

    int rows, cols, maxColor;
    Color[][] image;
    int[][] imageData;
    Boolean gui;
    int max;
    ArmController controller;

    public ImageManipulator() {
        this.gui = false;
        this.controller = new ArmController();
    }

    public void setupGUI() {
        UI.initialise();
        UI.addButton("Load PPM", ()-> this.loadPPM(UIFileChooser.open()));
        UI.addButton("Load Image", ()-> this.loadImage(UIFileChooser.open()));
        UI.addButton("Render Image", ()-> this.renderImage(15, 15, 1));
        UI.addButton("Render Data", ()-> this.renderData(15, 15, 1));
        UI.addButton("Edges", ()-> this.edgeDetection(100));
        UI.addButton("Load to Canny", ()-> controller.loadCannyMap(imageData));
        UI.addButton("Canny to Instructions", ()-> controller.cannyToInstructions(5, 100));
        UI.addButton("Print Instructions", ()-> controller.printCanny());
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.5);
        gui = true;
    }

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
    private Color getNextColour(Scanner sc) {
        int r = (int) ((double)(1.0*getNextScannerInt(sc)/maxColor*255));   //r
        int g = (int) ((double)(1.0*getNextScannerInt(sc)/maxColor*255));   //g
        int b = (int) ((double)(1.0*getNextScannerInt(sc)/maxColor*255));   //b
        if (r == -1 || g == -1 || b == -1) return null;
        return new Color(r, g, b);
    }

    public boolean loadPPM(String fName) {
        try {
            Scanner sc = new Scanner(new File(fName));
            if (!sc.next().equals("P3")) { UI.println("Invalid Image Type."); return false; }

            cols = getNextScannerInt(sc);
            rows = getNextScannerInt(sc);
            image = new Color[rows][cols];
            imageData = new int[rows][cols];
            maxColor = getNextScannerInt(sc);

            for (int row = 0; row < rows; row++)
                for (int col = 0; col < cols; col++)
                    image[row][col] = getNextColour(sc);

            if(gui) UI.printf("%dx%d image loaded successfully\n", rows, cols);
            System.out.printf("%dx%d image loaded successfully\n", rows, cols);

            return true;

        } catch (Exception e) {
            if(gui) UI.println("Exception " + e);
            System.out.println("Exception " + e);
        }
        return false; // If Exception occured.
    }
    public boolean loadImage(String fname) {
        try {
            BufferedImage img = ImageIO.read(new File(fname));
            rows = img.getHeight();
            cols = img.getWidth();
            image = new Color[rows][cols];
            imageData = new int[rows][cols];

            for (int row = 0; row < image.length; row++)
                for (int col = 0; col < image[row].length; col++)
                    image[row][col] = new Color(img.getRGB(col, row));

            if(gui) UI.printf("%dx%d image loaded successfully\n", rows, cols);
            System.out.printf("%dx%d image loaded successfully\n", rows, cols);

            return true;

        } catch (Exception e) {
            if(gui) UI.println("Exception " + e);
        }
        return false;
    }

    public void edgeDetection(int thr) {
        int[][] verticalEdges = edgeDetectionSlave(true, thr);
        int[][] horizontalEdges = edgeDetectionSlave(false, thr);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                imageData[row][col] = (int) Math.sqrt(Math.pow(verticalEdges[row][col],2) + Math.pow(horizontalEdges[row][col], 2));
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
    private int[][] edgeDetectionSlave(boolean vertical, int thr) {
        
        int[][] sobel, output = new int[rows][cols];

        if(vertical) sobel = new int[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        else sobel = new int[][]{{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};

        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < cols - 1; col++) {

                int[][] newSobel = new int[sobel.length][sobel[0].length];

                for (int r = 0; r < 3; r++)
                    for (int c = 0; c < 3; c++)
                        newSobel[r][c] = sobel[r][c] * getLuminocityFromColor(image[row - 1 + r][col - 1 + c]);

                int sum = 0;
                for (int[] r : newSobel) for (int c : r) sum += c; // For every value of every cell add to total
                if (sum > thr) output[row][col] = sum;
            }
        }
        return output;
    }

    private int getLuminocityFromColor(Color c) {
//        https://stackoverflow.com/questions/596216/formula-to-determine-brightness-of-rgb-color
//        (0.2126*R + 0.7152*G + 0.0722*B)
        return (int) (c.getRed()*0.2126 + c.getGreen()*0.7152 + c.getBlue()*0.0722);
    }

    private void renderImage(int left, int top, int pxSize) {
        if (!gui || image == null) return;
        UI.clearGraphics();
        UI.setImmediateRepaint(false);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                UI.setColor(image[row][col]);
                UI.fillRect(left + col * pxSize, top + row * pxSize, pxSize, pxSize);
            }
        }
        UI.repaintGraphics();
    }
    private void renderData(int left, int top, int pxSize) {
        if (!gui || image == null) return;
        UI.clearGraphics();
        UI.setImmediateRepaint(false);

        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                if (imageData[row][col] > 255 || imageData[row][col] < 0)
                    UI.println(row + ", " + col + " exceeds maximum value 255: " + imageData[row][col]);
                else {
                    int intensity = imageData[row][col];
                    UI.setColor(new Color(intensity, intensity, intensity));
                    UI.fillRect(left + col * pxSize, top + row * pxSize, pxSize, pxSize);
                }

        UI.repaintGraphics();
    }

    public static void main(String[] arguments) {
        ImageManipulator im = new ImageManipulator();
        im.setupGUI();
    }
}