package View;

import Model.IModel;
import Model.Model;
import ViewModel.ViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Best Maze Game Ever");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();

        IModel model = new Model();
        ViewModel viewModel = new ViewModel(model);
        MyViewController myViewController = fxmlLoader.getController();
        myViewController.setViewModel(viewModel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
