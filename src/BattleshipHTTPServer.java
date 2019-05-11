import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import javax.imageio.*;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BattleshipHTTPServer {
    //version of the game
    private static final int VERSION = 2;

    //port to listen connection
    private int portNumber;

    // verbose mode
    private boolean verbose;

    //hold the id, score and date of completion of the best game
    protected HallOfFame bestGames;

    //hold the on going games
    protected CookieManager cookieManager;

    //Thread pool to execute client request
    private ExecutorService threadPool;


    //keep the pictures in base64 saved for all games
    public String explosion;
    public String wauta;
    public String claudy;


    public BattleshipHTTPServer(int threadPoolSize, int portNumber, boolean verbose,String expl_dir,String wauta_dir,String claudy_dir) {

        try {
            //save the pictures in base 64
            File fexpl = new File(expl_dir);
            FileInputStream fin_expl = new FileInputStream(expl_dir);
            byte[] bytes_expl = new byte[(int) fexpl.length()];
            fin_expl.read(bytes_expl);
            explosion = Base64.getEncoder().encodeToString(bytes_expl);

            File fcloud = new File(claudy_dir);
            FileInputStream fin_cloud = new FileInputStream(claudy_dir);
            byte[] bytes_cloud = new byte[(int) fcloud.length()];
            fin_cloud.read(bytes_cloud);
            claudy = Base64.getEncoder().encodeToString(bytes_cloud);
            File fwater = new File(wauta_dir);
            FileInputStream fin_water = new FileInputStream(wauta_dir);
            byte[] bytes_water = new byte[(int) fwater.length()];
            fin_water.read(bytes_water);
            wauta = Base64.getEncoder().encodeToString(bytes_water);
        }
        catch(Exception e){
            System.out.println("Il y a eu un problème dans la lecture des fichiers images.");
        }

        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
        this.portNumber = portNumber;
        this.verbose = verbose;
        this.bestGames = new HallOfFame();
        this.cookieManager = new CookieManager();
    }

    private void launch() throws Exception {
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
         * Main method accept up to 2 args
         * args[0] => port to listen on
         * args[1] => verbose mode for the server
         */
        int portNumber = 2511;
        boolean verbose = true;
        int threadPoolSize = 25;

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

        BattleshipHTTPServer server = new BattleshipHTTPServer(threadPoolSize, portNumber, verbose, "Explosion.jpg","Wauta.jpg","Claudy.png");

        try {
            server.launch();
        } catch (Exception e) {
            System.err.println("Server Connection error : " + e.getMessage() + "\n");
            e.printStackTrace();
            System.exit(1);
        }
    }

}