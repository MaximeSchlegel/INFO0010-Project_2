import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.io.*;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BattleshipHTTPServer {
    private int portNumber; // port to listen connection
    private boolean verbose; // verbose mode

    private ExecutorService threadPool; //Thread pool to execute client request

    protected HallOfFame bestGames; // hold the id and score of the best games
    protected CookieManager cookieManager; // hold the on going games

    // keep the pictures in base64 saved for all games
    protected String explosion;
    protected String water;
    protected String mist;

    // place to look for the images
    private static final File WEB_ROOT = new File("./web/");
    private static final String EXPLOSION_FILE = "Explosion.jpg";
    private static final String WATER_FILE = "Wauta.jpg";
    private static final String MIST_FILE = "Claudy.png";

    public BattleshipHTTPServer(int threadPoolSize, int portNumber, boolean verbose) {
        try {
            //save the pictures in base 64
            File explosionFile = new File(WEB_ROOT, EXPLOSION_FILE);
            FileInputStream explosionFileInput = new FileInputStream(explosionFile);
            byte[] explosionBytes = new byte[(int) explosionFile.length()];
            explosionFileInput.read(explosionBytes);
            this.explosion = Base64.getEncoder().encodeToString(explosionBytes);
            explosionFileInput.close();

            File cloudFile = new File(WEB_ROOT, MIST_FILE);
            FileInputStream mistFileInput = new FileInputStream(cloudFile);
            byte[] mistBytes = new byte[(int) cloudFile.length()];
            mistFileInput.read(mistBytes);
            this.mist = Base64.getEncoder().encodeToString(mistBytes);
            mistFileInput.close();

            File waterFile = new File(WEB_ROOT, WATER_FILE);
            FileInputStream waterFileInput = new FileInputStream(waterFile);
            byte[] waterBytes = new byte[(int) waterFile.length()];
            waterFileInput.read(waterBytes);
            this.water = Base64.getEncoder().encodeToString(waterBytes);
            mistFileInput.close();
        }
        catch(Exception e){
            System.out.println("Error when loading image : " + e);
        }

        //initialize the server
        this.portNumber = portNumber;
        this.verbose = verbose;

        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);

        this.bestGames = new HallOfFame();
        this.cookieManager = new CookieManager();
    }

    private void launch() throws Exception {
        try {
            //create the server socket listening on the given port
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
                BattleshipHTTPHandler worker = new BattleshipHTTPHandler(socket, this, this.verbose);
                this.threadPool.execute(worker);

                if (verbose) {
                    System.out.println("Connection handled. (" + new Date() + ")\n");
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static void main(String[] args) {
        /*
         * Main method accept up to 3 args
         * args[0] => number of threads (default 10)
         * args[1] => port to listen on (default 8000)
         * args[2] => verbose seting (default True)
         */

        boolean verbose = true;
        int portNumber = 8000;
        int threadPoolSize = 10;

        // parse the args
        if (args.length == 1) {
            threadPoolSize = Integer.parseInt(args[0]);
        }else if (args.length == 2) {
            threadPoolSize = Integer.parseInt(args[0]);
            portNumber = Integer.parseInt(args[1]);
        } else if (args.length == 3) {
            threadPoolSize = Integer.parseInt(args[0]);
            portNumber = Integer.parseInt(args[1]);
            verbose = Boolean.parseBoolean(args[2]);
        } else if (args.length > 3) {
            System.out.println("Incorrect number of args");
            System.exit(1);
        }

        // initialize the server
        BattleshipHTTPServer server = new BattleshipHTTPServer(threadPoolSize, portNumber, verbose);

        // launch the server
        try {
            server.launch();
        } catch (Exception e) {
            System.err.println("Server Connection error : " + e.getMessage() + "\n");
            e.printStackTrace();
            System.exit(1);
        }
    }
}