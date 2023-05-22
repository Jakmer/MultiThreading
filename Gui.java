import java.util.Random;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Gui {

    /******************************************************************
     * 
     * Parameters
     * 
     ******************************************************************/

    private int width = 0;
    private int height = 0;
    private int rate = 0;
    private double probability = 0;
    private int threadID = 0;
    private MyThread[][] rects;
    Object object = new Object();

    /******************************************************************
     * 
     * Gui constructor
     * 
     ******************************************************************/

    public Gui(Stage stage, String[] args) {

        /******************************************************************
         * 
         * Reading parameters from terminal
         * 
         ******************************************************************/

        try {
            width = Integer.parseInt(args[0]);

            if (width <= 0)
                throw new Exception(" Width must be greater than 0");

            height = Integer.parseInt(args[1]);

            if (height <= 0)
                throw new Exception(" Height must be greater than 0");

            rate = Integer.parseInt(args[2]);

            if (rate <= 500)
                throw new Exception(" Rate must be greater than 500ms");

            probability = Double.parseDouble(args[3]);

            if (probability < 0.0 | probability > 1.0)
                throw new Exception(" Probability must be greater or equal than 0.0 and lower or equal than 1.0");

        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
            System.exit(0);
        }

        /******************************************************************
         * 
         * Creating board m*n rectangles
         * 
         ******************************************************************/

        Pane root = new Pane();

        rects = new MyThread[height][width];

        double recWidth = Screen.getPrimary().getVisualBounds().getWidth() / width;
        double recHeight = Screen.getPrimary().getVisualBounds().getHeight() / height;

        /******************************************************************
         * 
         * Filling board with rectangles
         * 
         ******************************************************************/

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rects[i][j] = new MyThread(j, i, recWidth, recHeight, object, probability, rate, threadID);
                threadID++;
                root.getChildren().add(rects[i][j]);
            }
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                MyThread[] neighbours = new MyThread[4];

                neighbours[0] = rects[(i - 1 + height) % height][j];
                neighbours[1] = rects[(i + 1) % height][j];
                neighbours[2] = rects[i][(j - 1 + width) % width];
                neighbours[3] = rects[i][(j + 1) % width];

                rects[i][j].getNeighbours(neighbours);
                Thread thread = new Thread(rects[i][j]);
                thread.start();
            }
        }

        /******************************************************************
         * 
         * Creating scene
         * 
         ******************************************************************/

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Lab06");
        stage.setMaximized(true);
        stage.show();
        stage.setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

}

/******************************************************************
 * 
 * Class thread to man rectangles' colors
 * 
 ******************************************************************/

class MyThread extends Rectangle implements Runnable {

    /******************************************************************
     * 
     * Thread parameters
     * 
     ******************************************************************/
    private int ID;
    private MyThread[] neighbours;
    private double probability;
    private int rate;
    private final int ocena = 4;
    public boolean active = true;
    private Object object;
    private Random random = new Random();
    public Color color;

    /******************************************************************
     * 
     * Thread constructor
     * 
     ******************************************************************/

    public MyThread(int x, int y, double width, double height, Object object, double probability, int rate, int ID) {
        super(x * width, y * height, width, height);
        this.ID = ID;
        this.object = object;
        this.probability = probability;
        this.rate = rate;
        this.color = changeRandomColor();
        setFill(color);
        setOnMouseClicked(new MouseClicked());

    }

    /******************************************************************
     * 
     * Overrided method run for thread tasks
     * 
     ******************************************************************/

    @Override
    public void run() {
        while (true) {

            /******************************************************************
             * 
             * Delay for thread
             * 
             ******************************************************************/

            try {
                Thread.sleep(randomTime(rate));
                if (!active) {
                    synchronized (this) {
                        while (!active)
                            wait();
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
            }

            /******************************************************************
             * 
             * Switching functionality for grade 3 and 4
             * 
             ******************************************************************/
            Platform.runLater(() -> {

                switch (ocena) {
                    case 3:
                        if (probability(probability)) {
                            randomColor();
                            setFill(color);
                        }
                        break;
                    case 4:
                        if (probability(probability)) {
                            randomColor();
                            setFill(color);
                        } else {
                            neighbColor();
                            setFill(color);
                        }
                        break;

                    default:
                        System.out.println("Error: Invalid ocena");
                        break;
                }

            });
        }
    }

    /******************************************************************
     * 
     * Changing color on neighbours mean
     * 
     ******************************************************************/

    public void neighbColor() {
        synchronized (object) {
            color = changeNeighbourColor();
        }
    }

    /******************************************************************
     * 
     * Changing on random color
     * 
     ******************************************************************/

    void randomColor() {
        synchronized (object) {
            color = changeRandomColor();
        }
    }

    public Color changeNeighbourColor() {
        System.out.println("Start " + ID);

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

            System.out.println("End " + ID);
            return Color.rgb(red, green, blue);
        }

        System.out.println("End " + ID);
        return color;
    }

    Color changeRandomColor() {
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    int randomTime(int rate) {

        int delay = (int) Math.floor(rate * (random.nextDouble() + 0.5));
        return delay;
    }

    boolean probability(double probability) {
        return (random.nextDouble() <= probability);
    }

    public void getNeighbours(MyThread[] neighbours) {
        this.neighbours = neighbours;
    }

    synchronized void changeActiveFlag(){
        active=!active;
        notify();
    }
}

/******************************************************************
 * 
 * Event handler on mouse click
 * 
 ******************************************************************/

class MouseClicked implements EventHandler<MouseEvent> {

    /******************************************************************
     * 
     * Event handler parameters
     * 
     ******************************************************************/
    MyThread thread;

    /******************************************************************
     * 
     * Changing active flag for thread on click mouse
     * 
     ******************************************************************/

    @Override
    public synchronized void handle(MouseEvent event) {
        event.consume();
        thread = (MyThread) event.getSource();

        double dx = event.getX();
        double dy = event.getY();

        if (thread.contains(dx, dy)) {
            thread.changeActiveFlag();
        }
    }

}
