package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

public class MazeDisplayer extends Canvas {
    private Maze maze;
    private double zoomFactor = 1.0;
    MediaPlayer player;
    private Solution solution;
    // player position:
    private int playerRow = 0;
    private int playerCol = 0;
    // wall and player images:
    StringProperty imageFileNameSolution = new SimpleStringProperty();
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();

    public MazeDisplayer(){
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());

    }

    public void resized(){
        draw();
    }


    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public double prefWidth(double v) {
        return this.getParent().getScene().getWidth() - 150;
    }

    @Override
    public double prefHeight(double v) {
        return this.getParent().getScene().getHeight() - 50;
    }

    @Override
    public void resize(double width, double height) {
        setHeight(height);
        setWidth(width);
    }

    public void setPlayerPosition(int row, int col) {
        this.playerRow = row;
        this.playerCol = col;
        draw();
    }


    public void setSolution(Solution solution) {
        this.solution = solution;
        draw();
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public String getImageFileNameSolution() {
        return imageFileNameSolution.get();
    }
    public String imageFileNameSolutionProperty(){
        return imageFileNameSolution.get();
    }
    public String imageFileNameWallProperty() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public void setImageFileNameSolution(String imageFileNameSolution) {
        this.imageFileNameSolution.set(imageFileNameSolution);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }



    public String imageFileNamePlayerProperty() {
        return imageFileNamePlayer.get();
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }


    public void drawMaze(Maze maze) {
        String musicFile = "./resources/music/03 Title Screen.mp3";     // For example
        Media media = new Media(new File(musicFile).toURI().toString());
        if (player != null)
            player.stop();
        player = new MediaPlayer(media);
        player.play();
        this.maze = maze;
        this.solution = null;
        draw();
    }





    private void draw() {
        resize(prefWidth(0), prefHeight(0));
        if(maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.getRows();
            int cols = maze.getColumns();


            double cellHeight = canvasHeight / rows * zoomFactor;
            double cellWidth = canvasWidth / cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            drawMazeWalls(graphicsContext, cellHeight, cellWidth, rows, cols);
            if(solution != null)
                drawSolution(graphicsContext, cellHeight, cellWidth);
            drawPlayer(graphicsContext, cellHeight, cellWidth);
        }
    }



    private void drawSolution(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        graphicsContext.setFill(Color.GREEN);
        Image solImage = null;
        Image tropImage = null;
        try{
           solImage = new Image(new FileInputStream(getImageFileNameSolution()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no Solve image file");
        }
        try{
            tropImage = new Image(new FileInputStream("./resources/images/trophy2.png"));
        } catch(FileNotFoundException e){
            System.out.println("There is no trophy image");
        }
        ArrayList<AState> list = solution.getSolutionPath();
        for(AState a: list){
            int col = a.getColumn();
            int row = a.getRow();
            double x = col * cellWidth;
            double y = row * cellHeight;
            if (col == maze.getGoalPosition().getColumnIndex() && row == maze.getGoalPosition().getRowIndex()){
                if (tropImage == null){
                    graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                } else {
                    graphicsContext.drawImage(tropImage, x, y, cellWidth, cellHeight);
                }
            }else {

                if (solImage == null)
                    graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                else
                    graphicsContext.drawImage(solImage, x, y, cellWidth, cellHeight);
            }
        }
    }

    @Override
    public boolean isResizable() {
        return true;
    }



    private void drawMazeWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols)  {
        graphicsContext.setFill(Color.RED);

        Image wallImage = null;
        Image tropImage = null;
        Image groundImage = null;

        try {
            groundImage = new Image(new FileInputStream("./resources/images/surface.png"));
        } catch (FileNotFoundException e) {
            System.out.println("There is no ground image file");
        }

        try{
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");
        }
        try {
             tropImage = new Image(new FileInputStream("./resources/images/trophy2.png"));
        } catch (FileNotFoundException e){
            System.out.println("There is no trophy image");
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(maze.getMaze()[i][j] == 1){
                    //if it is a wall:
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    if(wallImage == null)
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                } else{
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    if(groundImage == null)
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(groundImage, x, y, cellWidth, cellHeight);
                }
            }
        }
        int x = maze.getGoalPosition().getColumnIndex();
        int y = maze.getGoalPosition().getRowIndex();
        graphicsContext.drawImage(tropImage, x*cellWidth, y*cellHeight, cellWidth, cellHeight);
    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = getPlayerCol() * cellWidth;
        double y = getPlayerRow() * cellHeight;
        graphicsContext.setFill(Color.GREEN);

        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no player image file");
        }
        if(playerImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(playerImage, x, y, cellWidth, cellHeight);
    }

    public void playerWon()  {
        String musicFile = "./resources/music/53 Ending.mp3";     // For example
        Media media = new Media(new File(musicFile).toURI().toString());
        player.stop();
        player = new MediaPlayer(media);
        player.play();
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        Random rand = new Random();
        GraphicsContext graphicsContext = getGraphicsContext2D();
        Image fireGif = null;
        try{
            fireGif = new Image(new FileInputStream("./resources/images/firework.gif"));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");
        }
        for (int i = 1; i < 10; i++){
            int x = rand.nextInt((int)canvasWidth);
            int y = rand.nextInt((int)canvasHeight);
            graphicsContext.drawImage(fireGif, x, y, 400, 400);
        }



    }
}
