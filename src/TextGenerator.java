import java.io.*;
import java.lang.Math;

import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.round;
import static java.lang.StrictMath.sin;

public class TextGenerator {

    String filename = "code.txt";

    public TextGenerator() {

//        drawLine(1000, 1000, 2000, 2000, 500);
//        drawCircle(1500, 1500, 75, 300);
        drawHorizLine(1300, 1700, 1500);
    }

    public void drawHorizLine(int x1, int x2, int y) {

        System.out.println("Drawing line");

        try {

            PrintWriter writer = new PrintWriter(new File(filename));

            while(x1<x2) {
                writer.println(round(x1) + "," + round(y) + "," + "1500");
                x1+=4;
                y+=3;
            }

            writer.close();

        } catch (Exception e ) {

            System.out.println("ERROR: " + e);

        }


    }

    public void drawCircle(int xCenter, int yCenter, int radius, int accuracy) {

        System.out.println("Drawing circle");

        try {

            PrintWriter writer = new PrintWriter(new File(filename));

            for (int i = 0; i < accuracy; i++) {

                double angle =  i * Math.PI * 2 / accuracy;
                double x = xCenter + radius * cos(angle);
                double y = yCenter + radius * sin(angle);

                writer.println(round(x) + "," + round(y) + "," + "1500");

            }

            writer.close();

        } catch (Exception e ) {

            System.out.println("ERROR: " + e);

        }

    }

    public static void main(String[] args) {
        TextGenerator tg = new TextGenerator();
    }

}
