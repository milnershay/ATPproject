package View;

import ViewModel.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PropController implements Initializable {
    public ViewModel viewModel;
    @FXML public javafx.scene.control.ChoiceBox genAlgo;
    @FXML public javafx.scene.control.ChoiceBox solvAlgo;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll("MyMazeGenerator", "SimpleMazeGenerator","EmptyMazeGenerator");
        ObservableList<String> list2 = FXCollections.observableArrayList();
        list2.addAll("BestFirstSearch", "BreadthFirstSearch", "DepthFirstSearch");
        //populate the Choicebox;
        genAlgo.setItems(list);
        solvAlgo.setItems(list2);

    }

    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
    }
    public void updateProp(){
        String gen = genAlgo.getValue().toString();
        String solve = solvAlgo.getValue().toString();
        viewModel.updateProp(gen, solve);
        Stage stage = (Stage) genAlgo.getScene().getWindow();
        stage.close();
    }



}
