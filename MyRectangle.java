import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class MyRectangle extends Rectangle {

    public boolean active = true;
    private int i;
    private int j;
    private Random random = new Random();

    public MyRectangle(int j, int i, double width, double height) {
        super(j * width, i * height, width, height);
        this.i = i;
        this.j = j;
        changeRandomColor();

    }

    public void changeNeighbourColor(MyRectangle[] neighbours) {
        System.out.println("Start thread: " + i + " " + j);

        int red = 0, green = 0, blue = 0, divisor = 0;

        for (int k = 0; k <= 3; k++) {
            if (neighbours[k].active) {
                Paint paint = neighbours[k].getFill();
                Color color = (Color) paint;
                red += (int) Math.round(color.getRed() * 255);
                green += (int) Math.round(color.getGreen() * 255);
                blue += (int) Math.round(color.getBlue() * 255);
                divisor++;
            }
        }
        if (divisor != 0) {
            red /= divisor;
            green /= divisor;
            blue /= divisor;

            Color color = Color.rgb(red, green, blue);
            setFill(color);
        }

        System.out.println("End thread: " + i + " " + j);
    }

    public void changeRandomColor() {
        Color color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        setFill(color);
    }

    public int randomTime(int rate) {

        int delay = (int) Math.floor(rate * (random.nextDouble() + 0.5));
        return delay;
    }

    public boolean probability(double probability) {
        return (random.nextDouble() <= probability);
    }

}
