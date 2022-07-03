package View;

import Model.IModel;
import Model.Model;
import ViewModel.ViewModel;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Font.loadFont("resources/font/atari_full.ttf", 12.0);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent root = fxmlLoader.load();
        root.getStylesheets().add("MainStyle.css");
        primaryStage.setTitle("Poke-maze");
        primaryStage.setScene(new Scene(root));



        primaryStage.setResizable(true);
        primaryStage.show();



        IModel model = new Model();
        ViewModel viewModel = new ViewModel(model);
        MyViewController myViewController = fxmlLoader.getController();
        myViewController.setViewModel(viewModel);
        primaryStage.setOnCloseRequest(windowEvent -> myViewController.quit());
        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                myViewController.mazeDisplayer.resized();
            }
        });


        primaryStage.fullScreenProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                myViewController.mazeDisplayer.resized();
            }
        });

        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                myViewController.mazeDisplayer.resized();
            }
        });



    }




    public static void main(String[] args) {
        launch(args);
    }
}
