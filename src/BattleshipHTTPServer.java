import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.io.*;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BattleshipHTTPServer {

    private int portNumber; //port to listen connection on
    private boolean verbose; //verbose mode

    private ExecutorService threadPool; //Thread pool to execute clients requests

    protected HallOfFame bestGames; //hold the id and score of the best games
    protected CookieManager cookieManager; //hold the on going games

    //keep the pictures in base64 saved for all games
    protected String explosion;
    protected String water;
    protected String mist;
    protected String water50;
    protected String mistyWater;

    //Path to the images files
    private static final File WEB_ROOT = new File("./web/");
    private static  final String EXPLOSION_PATH = "explosion50.jpg";
    private static  final String WATER_PATH = "Wauta.jpg";
    private static  final String MIST_PATH = "Claudy.png";
    private static  final String WATER50_PATH = "wauta50.jpg";
    private static  final String MISTYWATER_PATH = "Clauda50.png";


    public BattleshipHTTPServer(int threadPoolSize, int portNumber, boolean verbose) {
        try {
            //save the pictures in base 64
            File explosionFile = new File(WEB_ROOT, EXPLOSION_PATH);
            FileInputStream explosionFileInput = new FileInputStream(explosionFile);
            byte[] explosionBytes = new byte[(int) explosionFile.length()];
            explosionFileInput.read(explosionBytes);
            this.explosion = Base64.getEncoder().encodeToString(explosionBytes);
            explosionFileInput.close();

            File mistFile = new File(WEB_ROOT, MIST_PATH);
            FileInputStream mistFileInput = new FileInputStream(mistFile);
            byte[] mistBytes = new byte[(int) mistFile.length()];
            mistFileInput.read(mistBytes);
            this.mist = Base64.getEncoder().encodeToString(mistBytes);
            mistFileInput.close();

            File waterFile = new File(WEB_ROOT, WATER_PATH);
            FileInputStream waterFileInput = new FileInputStream(waterFile);
            byte[] waterBytes = new byte[(int) waterFile.length()];
            waterFileInput.read(waterBytes);
            this.water = Base64.getEncoder().encodeToString(waterBytes);
            waterFileInput.close();

            File water50File = new File(WEB_ROOT, WATER50_PATH);
            FileInputStream water50FileInput = new FileInputStream(water50File);
            byte[] water50Bytes = new byte[(int) water50File.length()];
            water50FileInput.read(water50Bytes);
            this.water50 = Base64.getEncoder().encodeToString(water50Bytes);
            water50FileInput.close();

            File mistyWaterFile = new File(WEB_ROOT, MISTYWATER_PATH);
            FileInputStream mistyWaterFileInput = new FileInputStream(mistyWaterFile);
            byte[] mistyWaterBytes = new byte[(int) mistyWaterFile.length()];
            mistyWaterFileInput.read(mistyWaterBytes);
            this.mistyWater = Base64.getEncoder().encodeToString(mistyWaterBytes);
            mistyWaterFileInput.close();
        }
        catch (Exception e) {
            System.out.println("An error occured when loading the images:" + e.getMessage());
        }

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

                if (this.verbose) {
                    System.out.println("Connection opened. (" + new Date() + ")");
                }

                //create the worker to handle the connection
                BattleshipHTTPHandler worker = new BattleshipHTTPHandler(socket, this, this.verbose);
                this.threadPool.execute(worker);

                if (this.verbose) {
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
         * args[2] => verbose mode (default true)
         */
        int threadPoolSize = 10;
        int portNumber = 8000;
        boolean verbose = true;

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
            System.out.println("Incorrect number of args used");
            System.exit(1);
        }

        BattleshipHTTPServer server = new BattleshipHTTPServer(threadPoolSize, portNumber, verbose);

        try {
            server.launch();
        } catch (Exception e) {
            System.err.println("Server Connection error : " + e.getMessage() + "\n");
            e.printStackTrace();
            System.exit(1);
        }
    }
}