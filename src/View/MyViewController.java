package View;

import ViewModel.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
        int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());

        viewModel.generateMaze(rows, cols);
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
        keyEvent.consume();
    }

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
            default -> System.out.println("Not implemented change: " + change);
        }
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




}
