package View;

import ViewModel.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MyViewController implements Initializable, Observer {
    public ViewModel viewModel;
    public boolean control = false;
    public boolean mouse = false;

    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
    }

    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label playerRow;
    public Label playerCol;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
    }



    public void generateMaze(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("WARNING");

        try {
            int rows = Integer.parseInt(textField_mazeRows.getText());
            int cols = Integer.parseInt(textField_mazeColumns.getText());
            if (rows < 4 || cols < 4) {
                alert.setContentText("Maze size to small, row / col needs to be larger then 4");
                alert.setHeaderText("size warning");
                alert.show();
            } else {
                viewModel.generateMaze(rows, cols);
            }
        } catch (NumberFormatException e){
            alert.setContentText("No number found please try again");
            alert.setHeaderText("No Number");
            alert.show();
        }
    }

    public void solveMaze(ActionEvent actionEvent) {
        viewModel.solveMaze();
    }

    public void saveFile(ActionEvent actionEvent){
        FileChooser fc = new FileChooser();
        fc.setTitle("Save maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fc.setInitialDirectory(new File("./resources"));
        File chosen = fc.showSaveDialog(null);
        viewModel.saveMaze(chosen);
    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fc.setInitialDirectory(new File("./resources"));
        File chosen = fc.showOpenDialog(null);
        viewModel.loadMaze(chosen);
    }

    public void keyPressed(KeyEvent keyEvent) {
        viewModel.movePlayer(keyEvent);
        if (keyEvent.getCode() == KeyCode.CONTROL){
            control = true;
            zoom();
        }
        if (keyEvent.getCode().isArrowKey()){
            shiftMap(keyEvent);
        }

        if (keyEvent.getCode() == KeyCode.HOME){
            mazeDisplayer.setTranslateY(0.0);
            mazeDisplayer.setTranslateX(0.0);
        }
        keyEvent.consume();
    }

    public void shiftMap(KeyEvent e){
        System.out.println(mazeDisplayer.getTranslateX());
        System.out.println(mazeDisplayer.getTranslateY());
        switch (e.getCode()) {
            case DOWN -> mazeDisplayer.setTranslateY(mazeDisplayer.getTranslateY() + 25);
            case UP -> mazeDisplayer.setTranslateY(mazeDisplayer.getTranslateY() - 25);
            case LEFT -> mazeDisplayer.setTranslateX(mazeDisplayer.getTranslateX() - 25);
            case RIGHT -> mazeDisplayer.setTranslateX(mazeDisplayer.getTranslateX() + 25);
        }
    }

    public void keyReleased(KeyEvent keyEvent){
        if (keyEvent.getCode() == KeyCode.CONTROL){
            control = false;
        }
    }

    public void zoom(){
        mazeDisplayer.setOnScroll(new EventHandler<ScrollEvent>() {
            double zoomFactor;
            @Override
            public void handle(ScrollEvent event) {
                if (control){
                    if (event.getDeltaY()<0){
                        zoomFactor = 0.95;
                    }else
                        zoomFactor = 1.05;
                    mazeDisplayer.setScaleX(mazeDisplayer.getScaleX()*zoomFactor);
                    mazeDisplayer.setScaleY(mazeDisplayer.getScaleY()*zoomFactor);

                }
            }
        });
    }

    double mouseX = 500;
    double mouseY = 450;





    public void setPlayerPosition(int row, int col){
        mazeDisplayer.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change){
            case "maze generated" -> mazeGenerated();
            case "player moved" -> playerMoved();
            case "maze solved" -> mazeSolved();
            case "player won" -> playerWon();
            case "no maze" -> noMaze();
            default -> System.out.println("Not implemented change: " + change);
        }
    }

    public void noMaze(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("WARNING");
        alert.setContentText("You tried solving before generating a maze");
        alert.setHeaderText("No Maze");
        alert.show();
    }

    private void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution());
    }

    private void playerWon(){
        mazeDisplayer.playerWon();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("CONGRATULATIONS!");
        alert.setHeaderText("YOU WON");
        alert.show();
    }

    private void playerMoved() {
        setPlayerPosition(viewModel.getPlayerRow(), viewModel.getPlayerCol());

    }

    private void mazeGenerated() {
        mazeDisplayer.drawMaze(viewModel.getMaze());
    }

    @FXML public javafx.scene.control.Button closeButton;
    @FXML
    public void quit(){
        viewModel.quit();
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }


    public void prop() throws IOException {
        Stage propStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("prop.fxml"));
        Parent root = fxmlLoader.load();
        PropController propController = fxmlLoader.getController();
        propController.setViewModel(this.viewModel);
        propStage.setTitle("Settings");
        propStage.setScene(new Scene(root));
        propStage.setResizable(false);
        propStage.show();
    }


    public void about(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Poke-maze was created by Shay Milner and Idan Gal");
        alert.setHeaderText("About");
        alert.show();
    }




}
