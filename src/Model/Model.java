package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.SimpleCompressorOutputStream;
import IO.SimpleDecompressorInputStream;
import Server.Configurations;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

public class Model extends Observable implements IModel {
    private Maze maze;
    private int playerRow;
    private int playerCol;
    private Solution solution;

    private final Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
    private final Server mazeSolvingServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());



    public Model() {
        (new Thread(mazeGeneratingServer::start)).start();
        (new Thread(mazeSolvingServer::start)).start();
    }

    @Override
    public void generateMaze(int rows, int cols) {

        maze = CommunicateWithServer_MazeGenerating(rows, cols);
        setChanged();
        notifyObservers("maze generated");
        // start position:
        Position pos = maze.getStartPosition();
        movePlayer(pos.getRowIndex(), pos.getColumnIndex());
    }

    private static Maze CommunicateWithServer_MazeGenerating(int r, int c) {
        final Maze[] maze = new Maze[1];
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{r, c};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])fromServer.readObject();
                        System.out.println(compressedMaze.toString());
                        System.out.println();
                        InputStream is = new SimpleDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[1000000000];
                        is.read(decompressedMaze);
                        maze[0] = new Maze(decompressedMaze);
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }
        return maze[0];
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void updatePlayerLocation(MovementDirection direction) {
        int [][] mat = maze.getMaze();
        switch (direction) {
            case UP -> {
                if (playerRow > 0)
                    movePlayer(playerRow - 1, playerCol);
            }
            case DOWN -> {
                if (playerRow < mat.length - 1)
                    movePlayer(playerRow + 1, playerCol);
            }
            case LEFT -> {
                if (playerCol > 0)
                    movePlayer(playerRow, playerCol - 1);
            }
            case RIGHT -> {
                if (playerCol < mat[0].length - 1)
                    movePlayer(playerRow, playerCol + 1);
            }
            case LEFT_UP -> {
                if (playerCol > 0 & playerRow > 0)
                    movePlayer(playerRow-1, playerCol-1);
            }
            case RIGHT_UP -> {
                if (playerCol < mat[0].length -1 & playerRow > 0)
                    movePlayer(playerRow -1 , playerCol +1);
            }
            case LEFT_DOWN -> {
                if (playerCol > 0 & playerRow < mat.length - 1)
                    movePlayer(playerRow + 1 , playerCol -1 );
            }
            case RIGHT_DOWN -> {
                if (playerCol < mat[0].length -1 & playerRow < mat.length -1)
                    movePlayer(playerRow + 1, playerCol + 1);
            }
        }

    }

    private void movePlayer(int row, int col){
        int [][] mat = maze.getMaze();
        if (mat[row][col] != 1) {
            this.playerRow = row;
            this.playerCol = col;
            setChanged();
            notifyObservers("player moved");
        }
        if (row == maze.getGoalPosition().getRowIndex() & col == maze.getGoalPosition().getColumnIndex() )
            win();
    }

    public void win(){
        setChanged();
        notifyObservers("player won");
    }

    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    @Override
    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public void solveMaze() {
        //solve the maze
        if (maze == null){
            setChanged();
            notifyObservers("no maze");
        }else {
            solution = CommunicateWithServer_SolveSearchProblem(maze);
            setChanged();
            notifyObservers("maze solved");
        }
    }

    private static Solution CommunicateWithServer_SolveSearchProblem(Maze m) {
        final Solution[] mazeSolution = new Solution[1];
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(m);
                        toServer.flush();
                        mazeSolution[0] = (algorithms.search.Solution)fromServer.readObject();

                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }
        return mazeSolution[0];
    }


    @Override
    public Solution getSolution() {
        return solution;
    }

    public void quit(){
        mazeGeneratingServer.stop();
        mazeSolvingServer.stop();
    }

    public void updateProp(String gen, String sol){
        try {
            OutputStream output = new FileOutputStream("resources/config.properties");

            try {
                Configurations config = Configurations.getInstance();
                config.writeConfig("2",gen,sol);
                Properties prop = new Properties();
                prop.setProperty("threadPoolSize", "2");
                prop.setProperty("mazeGeneratingAlgorithm", gen);
                prop.setProperty("mazeSearchingAlgorithm", sol);
                prop.store(output, (String)null);
            } catch (Throwable var8) {
                try {
                    output.close();
                } catch (Throwable var7) {
                    var8.addSuppressed(var7);
                }

                throw var8;
            }

            output.close();
        } catch (IOException var9) {
            var9.printStackTrace();
        }
    }

    public void saveMaze(File file){
        try {
            OutputStream out = new SimpleCompressorOutputStream(new FileOutputStream(file));
            out.write(maze.toByteArray());
            out.flush();
            out.close();
        } catch (IOException var8) {
            var8.printStackTrace();
        }
    }

    public void loadMaze(File file){
        byte[] savedMazeBytes = new byte[0];

        try {
            InputStream in = new SimpleDecompressorInputStream(new FileInputStream(file));
            savedMazeBytes = new byte[10000000];
            in.read(savedMazeBytes);
            in.close();
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        maze = new Maze(savedMazeBytes);
        movePlayer(maze.getStartPosition().getRowIndex(), maze.getStartPosition().getColumnIndex());

        setChanged();
        notifyObservers("maze generated");

    }
}
