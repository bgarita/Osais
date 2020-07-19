package FXInterface;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author AA07SZZ
 */
public class Tarifa {

    Button btn;
    Label lbl;
    int iClickCount = 0;

    
    public void run() {
        Stage stage = new Stage();
        // Create the button
        btn = new Button();
        btn.setText("Click me please!");
        btn.setOnAction(e -> buttonClick());

        // Create the Label
        lbl = new Label();
        lbl.setText("You have not clicked the button.");

        // Add the label and the button to a layout pane
        BorderPane pane = new BorderPane();
        pane.setTop(lbl);
        pane.setCenter(btn);

        // Add the layout pane to a scene
        Scene scene = new Scene(pane, 250, 150);

        // Add the scene to the stage, set the title
        // and show the stage
        stage.setScene(scene);
        stage.setTitle("Click Counter");
        stage.show();
    } // end start
    
    private void buttonClick(){
        iClickCount++;
        lbl.setText((iClickCount == 1)? "You have clicked once.": "You have clicked " + iClickCount + " times.");
    } // buttonClick
}
