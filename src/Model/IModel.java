package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.io.File;
import java.util.Observer;

public interface IModel {
    void generateMaze(int rows, int cols);
    Maze getMaze();
    void updatePlayerLocation(MovementDirection direction);
    int getPlayerRow();
    int getPlayerCol();
    void assignObserver(Observer o);
    void solveMaze();
    Solution getSolution();
    void quit();

    void updateProp(String gen, String solve);

    void saveMaze(File file);

    void loadMaze(File file);
}
