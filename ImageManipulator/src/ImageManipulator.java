import ecs100.UI;
import ecs100.UIFileChooser;

import java.awt.*;
import java.io.File;
import java.util.Scanner;

public class ImageManipulator {

    int rows, cols, maxColor, PX_SIZE, LEFT, TOP;
    Color[][] image;
    Boolean gui;

    public ImageManipulator() { this.gui = false; }

    public void setupGUI() {
        UI.initialise();
        UI.addButton("Load Image", ()-> this.loadImage(UIFileChooser.open()));
        UI.addButton("Render Image", this::renderImage);
        gui = true;
        PX_SIZE = 1;
        LEFT = 15; TOP = 15;
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

    public boolean loadImage(String fName) {
        try {
            Scanner sc = new Scanner(new File(fName));
            if (!sc.next().equals("P3")) { UI.println("Invalid Image Type."); return false; }

            cols = getNextScannerInt(sc);
            rows = getNextScannerInt(sc);
            image = new Color[rows][cols];
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

    public void renderImage() {
        if (!gui || image == null) return;
        UI.clearGraphics();
        UI.setImmediateRepaint(false);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                UI.setColor(image[row][col]);
                UI.fillRect(LEFT + col * PX_SIZE, TOP + row * PX_SIZE, PX_SIZE, PX_SIZE);
            }
        }

        UI.repaintGraphics();
    }
    
    public static void main(String[] arguments) {
        ImageManipulator im = new ImageManipulator();
        im.setupGUI();
        return;
    }
}