package ViewModel;

import Model.IModel;
import Model.MovementDirection;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {
    private IModel model;

    public ViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this); //Observe the Model for it's changes
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    public Maze getMaze(){
        return model.getMaze();
    }

    public int getPlayerRow(){
        return model.getPlayerRow();
    }

    public int getPlayerCol(){
        return model.getPlayerCol();
    }

    public Solution getSolution(){
        return model.getSolution();
    }

    public void generateMaze(int rows, int cols){
        model.generateMaze(rows, cols);
    }

    public void movePlayer(KeyEvent keyEvent){
        MovementDirection direction;
        switch (keyEvent.getCode()){
            case NUMPAD8 -> direction = MovementDirection.UP;
            case NUMPAD2 -> direction = MovementDirection.DOWN;
            case NUMPAD4 -> direction = MovementDirection.LEFT;
            case NUMPAD6 -> direction = MovementDirection.RIGHT;
            case NUMPAD7 -> direction = MovementDirection.LEFT_UP;
            case NUMPAD9 -> direction = MovementDirection.RIGHT_UP;
            case NUMPAD1 -> direction = MovementDirection.LEFT_DOWN;
            case NUMPAD3 -> direction = MovementDirection.RIGHT_DOWN;
            default -> {
                // no need to move the player...
                return;
            }
        }
        model.updatePlayerLocation(direction);
    }

    public void solveMaze(){
        model.solveMaze();
    }

    public void quit(){
        model.quit();
    }

    public void updateProp(String gen, String solve){
        model.updateProp(gen, solve);
    }

    public void saveMaze(File file){
        model.saveMaze(file);
    }

    public void loadMaze(File file){
        model.loadMaze(file);
    }


}
