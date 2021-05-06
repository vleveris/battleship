package battleship;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChooseSettings {
    public ChooseSettings() {
        Stage window = new Stage();
        window.setTitle("Welcome to battleship");
        window.initOwner(Main.parentWindow);
        Main.size = 10;
        Scene scene = new Scene(new Group(), 450, 250);
        GridPane dlgFrame = new GridPane();
        dlgFrame.setVgap(4);
        dlgFrame.setHgap(10);
        dlgFrame.setPadding(new Insets(5, 5, 5, 5));
        dlgFrame.add(new Text("Select your preferred game level: "), 0, 0);
        RadioButton easy = new RadioButton("Easy (8x8 grid)");
        RadioButton medium = new RadioButton("Medium (10x10 grid)");
        RadioButton difficult = new RadioButton("Difficult (12x12 grid)");
        ToggleGroup selectedItem = new ToggleGroup();
        easy.setToggleGroup(selectedItem);
        medium.setToggleGroup(selectedItem);
        difficult.setToggleGroup(selectedItem);
        medium.setSelected(true);
        CheckBox repeat = new CheckBox("Skip a turn if enemie's ship was touched");
        dlgFrame.add(easy, 1, 0);
        dlgFrame.add(medium, 2, 0);
        dlgFrame.add(difficult, 3, 0);
        dlgFrame.add(repeat, 4, 0);
        Button set = new Button("Set");
        set.setOnAction(e -> {
            if (medium.isSelected())
                Main.size = 10;
            else if (easy.isSelected())
                Main.size = 8;
            else
                Main.size = 12;
            if (repeat.isSelected())
                Main.repeat = true;
            window.close();
        });
        dlgFrame.add(set, 5, 0);
        set.setDefaultButton(true);
        Group root = (Group) scene.getRoot();
        root.getChildren().add(dlgFrame);
        window.setScene(scene);
        window.showAndWait();
    }
}