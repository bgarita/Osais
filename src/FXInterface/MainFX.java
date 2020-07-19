
package FXInterface;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author bgarita
 */
public class MainFX extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        String[] args = {};
        //Company.main(args);
        //Tarifa t = new Tarifa();
        //t.run();
        MainFX.runT();
        /*
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
        */
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public static void runT(){
        Tarifa t = new Tarifa();
        t.run();
    }
}
