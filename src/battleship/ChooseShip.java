package battleship;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChooseShip {
    public static boolean error;
    private int shipType;
    private int shipPos;

    public void showDialogue() {
        error = false;
        Stage window = new Stage();
        window.setTitle("Add new ship");
        window.initOwner(Main.parentWindow);
        Scene scene = new Scene(new Group(), 450, 250);
        GridPane dlgFrame = new GridPane();
        dlgFrame.setVgap(4);
        dlgFrame.setHgap(10);
        dlgFrame.setPadding(new Insets(5, 5, 5, 5));
        dlgFrame.add(new Text("Choose a ship from the list: "), 0, 0);
        final ComboBox<String> shipList = new ComboBox<>();
        for (int i = 0; i < 5; i++)
            shipList.getItems().add(new Ship(i, 0, 0, 0, 0).getName());
        dlgFrame.add(shipList, 1, 0);
        ToggleGroup position = new ToggleGroup();
        RadioButton vert = new RadioButton("Vertical");
        RadioButton hor = new RadioButton("Horizontal");
        vert.setToggleGroup(position);
        hor.setToggleGroup(position);
        dlgFrame.add(vert, 1, 0);
        dlgFrame.add(hor, 1, 1);
        Button set = new Button("Set");
        Button cancel = new Button("Cancel");
        cancel.setCancelButton(true);
        set.setOnAction(e -> {
            String errorString = null;
            if (!vert.isSelected() && !hor.isSelected()) {
                errorString = "Position is not selected.";
                error = true;
            }
            if (shipList.getSelectionModel().getSelectedIndex() == -1) {
                errorString = "Ship is not selected.";
                error = true;
            }
            if (error) {
                Alert info = new Alert(Alert.AlertType.ERROR);
                info.setTitle("Error");
                info.setHeaderText(null);
                info.setContentText(errorString);
                info.showAndWait();
                error = false;
            } else {
                if (hor.isSelected())
                    shipPos = 0;
                else
                    shipPos = 1;
                shipType = shipList.getSelectionModel().getSelectedIndex();
                window.close();
            }

        });
        cancel.setOnAction(e -> {
            error = true;
            window.close();
        });
        dlgFrame.add(set, 1, 2);
        dlgFrame.add(cancel, 2, 2);
        set.setDefaultButton(true);
        Group root = (Group) scene.getRoot();
        root.getChildren().add(dlgFrame);
        window.setScene(scene);
        window.showAndWait();
    }

    public int getShipType() {
        return shipType;
    }

    public int getShipPos() {
        return shipPos;
    }
}