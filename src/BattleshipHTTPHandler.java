import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;


public class BattleshipHTTPHandler extends Thread{
    //version of the game
    private static final int VERSION = 2;

    //web folder and files to return
    private static final File WEB_ROOT = new File("./web/");
    private static final String DEFAULT_FILE = "play.html";
    private static final String PLAY_FILE = "play.html";
    private static final String HALL_OF_FAME_FILE = "hall_of_fame.html";
    private static final String FILE_NOT_FOUND = "404.html";
    private static final String METHOD_NOT_SUPPORTED = "not_supported.html";

    //http header variable
    private static String SERVER_DETAILS = "Java Http Server";



    //connection and i/o to the client
    private Socket connectedClient;
    private BufferedReader inFromClient;
    private PrintWriter headerOut;
    private BufferedOutputStream dataOut;

    //reference server for hightscore and saved games
    private BattleshipHTTPServer master;

    private boolean verbose;
    
    BatThomi Game;


    private String cookie;

    public BattleshipHTTPHandler(Socket socket, BattleshipHTTPServer server) {
        super("BattleshipHTTPHandler");

        //initialize the io
        this.connectedClient = socket;
        this.inFromClient = null;
        this.headerOut = null;
        this.dataOut = null;

        this.master = server;
        this.verbose = true;

        System.out.println("Handler created.");
    }

    public BattleshipHTTPHandler(Socket socket, BattleshipHTTPServer server, boolean verbose) {
        super("BattleshipHTTPHandler");

        //initialize the io
        this.connectedClient = socket;
        this.inFromClient = null;
        this.headerOut = null;
        this.dataOut = null;

        this.master = server;
        this.verbose = verbose;

        if (verbose) {
            System.out.println("Handler created.");
        }
    }

    @Override
    public void run() {
        try {
            this.inFromClient = new BufferedReader(new InputStreamReader(this.connectedClient.getInputStream()));
            this.headerOut = new PrintWriter(this.connectedClient.getOutputStream());
            this.dataOut = new BufferedOutputStream(this.connectedClient.getOutputStream());

            //get the method and the querried ressource
            String requestLine = inFromClient.readLine(); //get the first line of the request => method and target ressource
            StringTokenizer requestTokenizer = new StringTokenizer(requestLine); // parse the line to get the token
            String httpMethod = requestTokenizer.nextToken();
            String httpQuerry = requestTokenizer.nextToken();
            System.out.println(httpMethod + " " + httpQuerry);

            //get the host name
            String hostLine = inFromClient.readLine(); //get the second line to extract the host address
            StringTokenizer hostTokenizer = new StringTokenizer(hostLine);
            hostTokenizer.nextToken();
            String httpHost = hostTokenizer.nextToken();
            System.out.println("Host :" + httpHost);

            //get the cookie name ???
            String Cookieline = inFromClient.readLine(); //get the second line to extract the host address
            StringTokenizer CookieTokenizer = new StringTokenizer(Cookieline);
            String iscookie = CookieTokenizer.nextToken();

            while(!iscookie.equals("Cookie:")) {
                System.out.println("Cookie lline :" + iscookie);
                Cookieline = inFromClient.readLine(); //get the second line to extract the host address
                CookieTokenizer = new StringTokenizer(Cookieline);

                try {
                    iscookie = CookieTokenizer.nextToken();
                    this.cookie = CookieTokenizer.nextToken();
                } catch (java.util.NoSuchElementException e) {
                    System.out.println("No cookies here :/");
                    this.cookie = "";//il n'y a pas de cookie
                    break;
                }
            }


            if (httpMethod.equals("GET")) {
                if (httpQuerry.equals("/")) {
                    //if the client required the root, we redirect him on the play page
                    headerOut.println("HTTP/1.1 303 See Other");
                    headerOut.println("Server: " + httpHost);
                    headerOut.println("Date: " + new Date());
                    headerOut.println("Location: " + "http://" +httpHost + "/play.html");
                    headerOut.println("Connection: close");
                    headerOut.println("Content-length: 0");
                    headerOut.println();
                    headerOut.flush();

                } else if (httpQuerry.equals("/play.html")) {
                    //proc the launch of the game
                    String response = "Play";
                    //String id = Cookie.get();
                    String id = "";
                    if(this.master.cookieManager.isUsed(id)){
                        this.Game = this.master.cookieManager.getGame(id);
                    }
                    else{
                        this.Game = this.master.cookieManager.getNewGame().getValue();
                    }

                    // send HTTP Headers
                    headerOut.println("HTTP/1.1 200 OK");
                    headerOut.println("Server: " + httpHost);
                    headerOut.println("Date: " + new Date());
                    headerOut.println("Content-type: " + "text/html");
                    headerOut.println("Content-length: " + response.getBytes().length);
                    headerOut.println("Set-Cookie: " + "Battleship=" + "123456789");
                    headerOut.println(); // blank line between headers and content, very important !
                    headerOut.flush(); // flush character output stream buffer

                    headerOut.println(response);
                    headerOut.flush();


                    /////


                    /////
                } else if (httpQuerry.equals("/hall_of_fame.html")) {
                    //return the hall of fame page
                    this.sendHallOfFame();

                } else {
                    //this page does not exist
                    this.fileNotFound(httpQuerry);
                }

            } else if (httpMethod.equals("POST")) {

            } else {
                this.methodNotSupported(httpMethod);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return fileData;
    }

    private void fileNotFound(String fileRequested) throws IOException {
        File file = new File(WEB_ROOT, FILE_NOT_FOUND);
        int fileLength = (int) file.length();
        byte[] fileData = readFileData(file, fileLength);

        this.headerOut.println("HTTP/1.1 404 File Not Found");
        this.headerOut.println("Server: " + SERVER_DETAILS);
        this.headerOut.println("Date: " + new Date());
        this.headerOut.println("Content-type: text/html");
        this.headerOut.println("Content-length: " + fileLength);
        this.headerOut.println();
        this.headerOut.flush();

        this.dataOut.write(fileData, 0, fileLength);
        this.dataOut.flush();

        if (verbose) {
            System.out.println("File " + fileRequested + " not found");
        }
    }

    private void methodNotSupported(String methodUsed) throws IOException {
        File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
        int fileLength = (int) file.length();
        byte[] fileData = readFileData(file, fileLength);

        this.headerOut.println("HTTP/1.1 405 Method Not Allowed");
        this.headerOut.println("Server: " + SERVER_DETAILS);
        this.headerOut.println("Date: " + new Date());
        this.headerOut.println("Content-type: text/html");
        this.headerOut.println("Content-length: " + fileLength);
        this.headerOut.println();
        this.headerOut.flush();

        this.dataOut.write(fileData, 0, fileLength);
        this.dataOut.flush();

        if (this.verbose) {
            System.out.println("File " + methodUsed + " not found");
        }
    }

    private void sendHallOfFame() throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "   <head>\n" +
            "       <meta charset=\"UTF-8\">\n" +
            "       <title>Battleship - Hall of Fame</title>\n" +
            "   </head>\n" +
            "   <body>\n" +
            "       <h1>Hall of Fame</h1>\n");

        ArrayList<Pair<String, Integer>> halloffame = this.master.best_games.getScore();
        if (halloffame.size() == 0) {
            responseBuilder.append("Nobody beat the game. Try it !");
        } else {
            responseBuilder.append(
                    "       <table>\n" +
                    "           <tr><th>Username</th><th>Score</th></tr>\n");
            for(Pair<String, Integer> score: halloffame) {
                responseBuilder.append(
                    "           <tr><th>" + score.getKey() + "</th><th>" + score.getValue() + "</th></tr>");
            }
            responseBuilder.append(
                    "       </table>\n");
        }

        responseBuilder.append(

            "   </body>\n" +
            "</html>");

        String response = responseBuilder.toString();


        // send HTTP Headers
        this.headerOut.println("HTTP/1.1 200 OK");
        this.headerOut.println("Server: " + SERVER_DETAILS);
        this.headerOut.println("Date: " + new Date());
        this.headerOut.println("Content-type: " + "text/html");
        this.headerOut.println("Content-length: " + response.getBytes().length);
        this.headerOut.println();
        this.headerOut.flush();

        this.dataOut.write(response.getBytes(),0, response.getBytes().length);
        this.dataOut.flush();
    }
}

//TODO: test the version of the incomming demand
//TODO: test the method used
//TODO: test the page demand
//TODO: test the cookie
//      => if exist: load the game
//      => create a new game and a new cookie
//TODO: Game data structure
//TODO: write the answer
//TODO:
