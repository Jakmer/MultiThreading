import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
    static String[] args;
    public static void main(String[] arg) {
        args=arg;
        Application.launch(arg);   
    }

    @Override
    public void start(Stage stage){
        new Gui(stage, args);
    }
}