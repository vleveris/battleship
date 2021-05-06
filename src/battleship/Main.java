package battleship;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class Main extends Application {
    public static Ship[] playerShips = new Ship[5];
    public static Stage parentWindow;
    public static int size;
    public static boolean repeat = false;
    /**
     * @author Vytautas Lėveris, VU MIF PS 1 k., 5 gr.
     */

    private final int[] lengths = {5, 4, 3, 3, 2};
    private final Ship[] enemyShips = new Ship[5];
    private final GridPane playerBoard = new GridPane();
    private final GridPane playerBoard2 = new GridPane();
    private final GridPane enemyBoard = new GridPane();
    private final VBox vbox = new VBox(10);
    private final Label note = new Label("Place your ships by clicking on a desired cell. Press on a ship to cancel the decision. Press CTRL+enter to start the game.");
    private final Label turnLabel = new Label("It's your turn!");
    private final PCPlayer pcPlayer = new PCPlayer();
    List<GridPane> list = new ArrayList<>();
    private Button[][] playerButtons;
    private Button[][] playerButtons2;
    private Button[][] enemyButtons;
    private boolean changeShipButton = true;
    private int escapeCode;
    private int whichScene = 0;
    private int turn = 0;
    private int playerScore = 0, pcScore = 0;
    private boolean isWinner = false;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        parentWindow = stage;
        stage.setTitle("Battleship game");
        stage.setResizable(false);
        stage.show();
        new ChooseSettings();
        stage.setTitle(stage.getTitle() + " [making plan]");
        setupBoard(playerBoard);
        setupBoard(playerBoard2);
        setupBoard(enemyBoard);
        playerButtons = new Button[size][size];
        playerButtons = new Button[size][size];
        enemyButtons = new Button[size][size];
        playerButtons2 = new Button[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                playerButtons[j][i] = new Button(Character.toString(65 + j) + (i + 1));
                playerButtons2[j][i] = new Button(Character.toString(65 + j) + (i + 1));
                enemyButtons[j][i] = new Button(Character.toString(65 + j) + (i + 1));
                playerButtons[j][i].setPrefSize(37, 37);
                playerButtons[j][i].setMaxSize(37, 37);
                playerButtons[j][i].setMinSize(37, 37);
                playerButtons2[j][i].setPrefSize(37, 37);
                playerButtons2[j][i].setMinSize(37, 37);
                playerButtons2[j][i].setMaxSize(37, 37);
                playerBoard.add(playerButtons[j][i], j, i);
                enemyButtons[j][i].setPrefSize(37, 37);
                enemyButtons[j][i].setMinSize(37, 37);
                enemyButtons[j][i].setMaxSize(37, 37);
                enemyBoard.add(enemyButtons[j][i], j, i);
                playerBoard2.add(playerButtons2[j][i], j, i);
                playerButtons[j][i].setOnAction(e -> processChoosing(GridPane.getColumnIndex((Button) e.getSource()), GridPane.getRowIndex((Button) e.getSource())));
                enemyButtons[j][i].setOnAction(buttonEvent -> {
                    if (turn == 0) {
                        Button workingButton = (Button) buttonEvent.getSource();
                        workingButton.setOnAction(wbEvent -> {
                        });
                        shoot(GridPane.getColumnIndex(workingButton), GridPane.getRowIndex(workingButton));
                    }
                });
            }
        playerBoard.setStyle("-fx-background-color: BEIGE;");
        enemyBoard.setStyle("-fx-background-color: BEIGE;");
        vbox.setAlignment(Pos.CENTER);
        note.setStyle("-fx-font: normal bold 20px 'serif' ");
        vbox.getChildren().addAll(note, playerBoard);
        Scene scene = new Scene(vbox);
        scene.setOnKeyPressed(E -> {
            if (new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN).match(E)) {
                boolean cont = true;
                escapeCode = 1;
                for (int i = 0; i < 5; i++)
                    if (playerShips[i] == null) {
                        cont = false;
                        break;
                    }
                if (!cont) {
                    Stage window = new Stage();
                    window.setTitle("Warning");
                    window.initOwner(parentWindow);
                    Scene s = new Scene(new Group(), 450, 250);
                    GridPane dlgFrame = new GridPane();
                    dlgFrame.setVgap(4);
                    dlgFrame.setHgap(10);
                    dlgFrame.setPadding(new Insets(5, 5, 5, 5));
                    dlgFrame.add(new Text("Not all ships have been positioned. Choose one of the options: "), 0, 0);
                    Button conPlace = new Button("Continue placing ships manually");
                    Button randomRemaining = new Button("Automatically place remaining ships at random positions");
                    Button randomAll = new Button("Place all ships automatically and ignore chosen ones");
                    dlgFrame.add(conPlace, 1, 0);
                    dlgFrame.add(randomRemaining, 2, 0);
                    dlgFrame.add(randomAll, 3, 0);
                    conPlace.setOnAction(event -> {
                        escapeCode = 0;
                        window.close();
                    });
                    randomRemaining.setOnAction(Event -> {
                        for (int i = 0; i < 5; i++)
                            if (playerShips[i] == null)
                                do {
                                }
                                while (!placeNewShip((int) (Math.random() * size), (int) (Math.random() * size), (int) (Math.random() * size) % 2, i, playerShips, true));
                        window.close();
                    });
                    randomAll.setOnAction(e -> {
                        for (int i = 0; i < 5; i++) {
                            playerShips[i] = null;
                            do {
                            }
                            while (!placeNewShip((int) (Math.random() * size), (int) (Math.random() * size), (int) (Math.random() * size) % 2, i, playerShips, true));
                        }
                        window.close();
                    });
                    Group root = (Group) s.getRoot();
                    root.getChildren().add(dlgFrame);
                    window.setScene(s);
                    window.showAndWait();
                }
                if (escapeCode == 1) {
                    changeShipButton = false;
                    for (int i = 0; i < 5; i++)
                        do {
                        }
                        while (!placeNewShip((int) (Math.random() * size), (int) (Math.random() * size), (int) (Math.random() * size) % 2, i, enemyShips, false));
                    list.add(enemyBoard);
                    list.add(playerBoard2);
                    list.add(playerBoard);
                    for (int i = 0; i < size; i++)
                        for (int j = 0; j < size; j++)
                            playerButtons[i][j].setOnAction(EV -> {
                            });
                    note.setText("Lets destroy your enemie's ships!");
                    Text note2 = new Text("Note: to switch between grids, use CTRL+TAB and CTRL+SHIFT+TAB.");
                    note2.setStyle("-fx-font: normal bold 20px 'serif' ");
                    turnLabel.setStyle("-fx-font: normal bold 20px 'serif' ");
                    vbox.getChildren().setAll(note, turnLabel, note2, list.get(whichScene));
                    changeParentTitle();
                    scene.setOnKeyPressed(keyEvent -> {
                        if (new KeyCodeCombination(KeyCode.TAB, KeyCombination.CONTROL_DOWN).match(keyEvent)) {
                            if (whichScene < 2)
                                vbox.getChildren().set(3, list.get(++whichScene));
                            else {
                                whichScene = 0;
                                vbox.getChildren().set(3, list.get(whichScene));
                            }
                            changeParentTitle();
                        }
                        if (new KeyCodeCombination(KeyCode.TAB, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN).match(keyEvent)) {
                            if (whichScene > 0)
                                vbox.getChildren().set(3, list.get(--whichScene));
                            else {
                                whichScene = 2;
                                vbox.getChildren().set(3, list.get(2));
                            }
                            changeParentTitle();
                        }
                    });

                }
            }
        });
        stage.setScene(scene);
    }

    private void processChoosing(int x, int y) {
        ChooseShip newShip = new ChooseShip();
        newShip.showDialogue();
        if (ChooseShip.error)
            return;
        int pos = newShip.getShipPos();
        int type = newShip.getShipType();
        boolean isPlacementValid = placeNewShip(x, y, pos, type, playerShips, true);
        if (!isPlacementValid) {
            Alert unable = new Alert(Alert.AlertType.ERROR);
            unable.setTitle("ADDING SHIP");
            unable.setHeaderText(null);
            unable.setContentText("Unable to place this ship in a specified location. Either this location is already occupied or ship placement is illegal due to invalid coordinates.");
            unable.showAndWait();
        }
    }

    private boolean placeNewShip(int initialX, int initialY, int pos, int type, Ship[] shipArray, boolean placeOnBoard) {
        boolean next = false;
        if (pos == 0 && initialX + lengths[type] - 1 < size)
            next = true;
        else if (pos == 1 && initialY + lengths[type] - 1 < size)
            next = true;
        if (next) {
            int x = initialX;
            int y = initialY;
            for (int i = 0; i < 5; i++)
                if (shipArray[i] != null && type == shipArray[i].getType()) {
                    next = false;
                    break;
                }
            for (int i = 0; i < lengths[type]; i++) {
                for (int j = 0; j < 5; j++)
                    if (shipArray[j] != null && shipArray[j].isOnGivenCoordinates(x, y))
                        next = false;
                if (pos == 0) x++;
                else y++;
            }
        }
        if (next) {
            shipArray[type] = new Ship(type, lengths[type], initialX, initialY, pos);
            if (placeOnBoard)
                placeShipOnBoard(shipArray[type], playerButtons, playerBoard, 0);
        }
        return next;
    }

    public void placeShipOnBoard(Ship ship, Button[][] buttons, GridPane board, int colour) {
        int[][] posArray = ship.getPosArray();
        int i;
        int length = ship.getLength();
        for (i = 0; i < length; i++)
            buttons[posArray[i][0]][posArray[i][1]].setVisible(false);
        Button newButton = new Button();
        if (ship.getPosition() == 0)
            newButton.setPrefSize(37 * length, 37);
        else
            newButton.setPrefSize(37, 37 * length);
        newButton.setMinSize(newButton.getPrefWidth(), newButton.getPrefHeight());
        newButton.setMaxSize(newButton.getPrefWidth(), newButton.getPrefHeight());
        Tooltip tt = new Tooltip(ship.getName() + " (" + Character.toString(posArray[0][0] + 65) + (posArray[0][1] + 1) + "-" + Character.toString(posArray[length - 1][0] + 65) + (posArray[length - 1][1] + 1) + ")");
        newButton.setTooltip(tt);
        newButton.setGraphic(new ImageView(new Image(this.getClass().getResource("resources/images/" + ship.getFName() + "_" + (ship.getPosition() == 0 ? "h" : "v") + ".png").toString())));
        if (colour == 0)
            newButton.setStyle("-fx-background-color: #0000ff;");
        else
            newButton.setStyle("-fx-background-color: #ff0000;");
        GridPane.setColumnSpan(newButton, posArray[length - 1][0] - posArray[0][0] + 1);
        GridPane.setRowSpan(newButton, posArray[length - 1][1] - posArray[0][1] + 1);
        board.add(newButton, posArray[0][0], posArray[0][1]);
        ObservableList<Node> workingCollection = FXCollections.observableArrayList(board.getChildren());
        int swapNum = 0;
        for (i = 0; i < workingCollection.size(); i++)
            if (workingCollection.get(i) == buttons[posArray[0][0]][posArray[0][1]]) {
                swapNum = i;
                break;
            }
        Collections.swap(workingCollection, workingCollection.size() - 1, swapNum);
        board.getChildren().setAll(workingCollection);
        newButton.requestFocus();
        newButton.setOnAction(E -> {
                    if (changeShipButton) {
                        int j, index = 0;
                        for (j = 0; j < 5; j++)
                            if (playerShips[j] != null && playerShips[j].isOnGivenCoordinates(GridPane.getColumnIndex((Button) E.getSource()), GridPane.getRowIndex((Button) E.getSource()))) {
                                index = j;
                                break;
                            }
                        for (j = 0; j < Objects.requireNonNull(playerShips[index]).getLength(); j++)
                            playerButtons[playerShips[index].getPosArray()[j][0]][playerShips[index].getPosArray()[j][1]].setVisible(true);
                        ObservableList<Node> tempCollection = FXCollections.observableArrayList(playerBoard.getChildren());
                        int swap1 = 0;
                        int swap2 = 0;
                        for (j = 0; j < tempCollection.size(); j++)
                            if (tempCollection.get(j) == E.getSource()) {
                                swap2 = j;
                                break;
                            }
                        for (j = 0; j < tempCollection.size(); j++)
                            if (tempCollection.get(j) == buttons[playerShips[index].getPosArray()[0][0]][playerShips[index].getPosArray()[0][1]]) {
                                swap1 = j;
                                break;
                            }
                        Collections.swap(tempCollection, swap1, swap2);
                        tempCollection.remove(tempCollection.get(swap1));
                        playerBoard.getChildren().setAll(tempCollection);
                        playerButtons[playerShips[index].getPosArray()[0][0]][playerShips[index].getPosArray()[0][1]].requestFocus();
                        playerShips[index] = null;
                    }
                }
        );
    }

    private void setupBoard(GridPane board) {
        board.setMinSize(400, 400);
        board.setAlignment(Pos.CENTER);
    }

    private void changeParentTitle() {
        String changeTitle = switch (whichScene) {
            case 0 -> " [your oponent's ocean]";
            case 1 -> " [your ocean]";
            case 2 -> " [your plan]";
            default -> null;
        };
        parentWindow.setTitle("Battleship game" + changeTitle);
    }

    void shoot(int x, int y) {
        String tooltipText;
        boolean hit = false;
        int i;
        Ship[] shipArray;
        Button[][] buttonArray;
        GridPane board;
        if (turn == 0) {
            board = enemyBoard;
            buttonArray = enemyButtons;
            shipArray = enemyShips;
        } else {
            buttonArray = playerButtons2;
            board = playerBoard2;
            shipArray = playerShips;
        }
        Ship ship = null;
        for (i = 0; i < 5; i++)
            if (shipArray[i].isOnGivenCoordinates(x, y)) {
                hit = true;
                break;
            }
        if (hit) {
            shipArray[i].shoot();
            if (shipArray[i].isSunk()) {
                ship = shipArray[i];
                placeShipOnBoard(shipArray[i], buttonArray, board, 1);
                playSound("sink");
            } else {
                buttonArray[x][y].setStyle("-fx-background-color: #0000ff;");
                buttonArray[x][y].setGraphic(new ImageView(new Image(this.getClass().getResource("resources/images/strike.png").toString())));
                tooltipText = buttonArray[x][y].getText();
                buttonArray[x][y].setText(" ");
                buttonArray[x][y].setTooltip(new Tooltip(tooltipText + " (touched)"));
                playSound("touched");
            }
        } else {
            playSound("missed");
            buttonArray[x][y].setTooltip(new Tooltip("In the water"));
        }
        gameController(ship, hit);
    }

    public void playSound(String fileName) {
        try {
            InputStream str = getClass().getResourceAsStream("resources/sounds/" + fileName + ".wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(str));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(0);
            clip.start();
        } catch (Exception ignored) {
        }
    }

    private void gameController(Ship ship, boolean hit) {
        boolean skipTurn = false;
        if (hit && repeat)
            skipTurn = true;
        if (turn == 1)
            pcPlayer.submitResults(ship, hit);
        if (turn == 0 && ship != null)
            playerScore++;
        else if (turn == 1 && ship != null)
            pcScore++;
        if (playerScore == 5 || pcScore == 5) {
            isWinner = true;
            Text finish = new Text();
            if (playerScore > 4) {
                finish.setText("Congratulations! You are the winner!");
                playSound("win");
            } else {
                finish.setText("Sorry, computer was better this time.");
                playSound("lose");
            }
            Platform.runLater(() -> {
                parentWindow.setTitle("Battleship - results");
                vbox.getChildren().setAll(finish);
            });
        }
        if (!skipTurn && !isWinner) {
            turn++;
            if (turn == 2)
                turn = 0;
            Platform.runLater(() -> {
                switch (turn) {
                    case 0 -> turnLabel.setText("It's your turn!");
                    case 1 -> turnLabel.setText("It's your oponent's turn.");
                }
            });
        }
        if (turn == 1 && !isWinner) {
            Runnable r = () -> {
                try {
                    Thread.sleep((int) (Math.random() * 1000 + 500));
                } catch (InterruptedException ignored) {
                }
                pcPlayer.go();
                shoot(pcPlayer.x, pcPlayer.y);
            };
            Thread thread = new Thread(r);
            thread.start();
        }
    }

}