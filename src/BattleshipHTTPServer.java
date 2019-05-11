import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BattleshipHTTPServer {
    //version of the game
    private static final int VERSION = 2;

    //port to listen connection
    private int portNumber;

    // verbose mode
    private boolean verbose = true;

    //hold the id, score and date of completion of the best game
    protected int[][] best_games;

    //hold the on goinging games
    protected Map<Integer, Integer> saved_games;


    public BattleshipHTTPServer(int portNumber, boolean verbose) {
        this.portNumber = portNumber;
        this.verbose = verbose;
        this.best_games = new int[10][3];
        this.saved_games = new HashMap<>();
    }

    private void launch () throws Exception {
        try {
            //create the server socket listening on the gien port
            ServerSocket serverSocket = new ServerSocket(this.portNumber);

            if (this.verbose) {
                System.out.println("Server socket created.");
                System.out.println("Listening for connections on port : " + this.portNumber + " ...\n");
            }

            //listen the socket until the server is shutdown
            while (true) {
                Socket socket = serverSocket.accept();

                if (verbose) {
                    System.out.println("Connection opened. (" + new Date() + ")");
                }

                //create the worker to handle the connection
                BattleshipHTTPHandler worker = new BattleshipHTTPHandler(socket, this);
                worker.start();

                if (verbose) {
                    System.out.println("Connection handled. (" + new Date() + ")\n");
                }

            }
        } catch (Exception e) {
            throw e;
        }
    }


    public static void main (String[] args) {
        /*
        * Main method accept up to 2 args
        * args[0] => port to listen on
        * args[1] => verbose mode for the server
        */
        int portNumber = 2511;
        boolean verbose = true;

        if (args.length == 1) {
            portNumber = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            portNumber = Integer.parseInt(args[0]);
            verbose = Boolean.parseBoolean(args[1]);
        } else if (args.length > 2) {
            System.out.println("Incorrect number of args");
            System.exit(1);
        }

        BattleshipHTTPServer server = new BattleshipHTTPServer(portNumber, verbose);

        try{
            server.launch();
        } catch (Exception e) {
            System.err.println("Server Connection error : " + e.getMessage() + "\n");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
