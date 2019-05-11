import java.io.*;
import java.net.Socket;
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

    //connection socket
    private Socket socket;

    //reference server for hightscore and saved games
    private BattleshipHTTPServer server;

    public BattleshipHTTPHandler(Socket socket, BattleshipHTTPServer server) {
        super("BattleshipHTTPHandler");
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {

        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;

        String fileRequested = null;

        try {
            // we read characters from the client via input stream on the socket
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            // we get character output stream to client (for headers)
            out = new PrintWriter(this.socket.getOutputStream());
//            // get binary output stream to client (for requested data)
//            dataOut = new BufferedOutputStream(this.socket.getOutputStream());

            String request = in.readLine(); //get the first line of the request => method and target ressource
            StringTokenizer parser = new StringTokenizer(request); // parse the line to get the token
            String method = parser.nextToken();
            System.out.println(method);

            String host = in.readLine();
            StringTokenizer parser = new StringTokenizer(request); // parse the line to get the token


            if (method.equals("GET")) {
                String target = parser.nextToken();
                System.out.println(target);

                if (target.equals("/")) {
                    //if the client required the root, we redirect him on the play page
                    out.println("HTTP/1.1 303 See Other");
                    out.println("Server:" + " localhost:2511");
                    out.println("Date: " + new Date());
                    out.println("Location: " + "localhost:2511" + "/play.html");
                    out.println("Content-length: 0");
                    out.println();
                    out.flush();

                } else if (target.equals("/play.html")) {
                    //proc the launch of the game

                } else if (target.equals("/hall_of_fame.html")) {
                    //return the hall of fame page

                } else {
                    //this page does not exist
                    out.println("HTTP/1.1 404 Not Found");
                    out.println("Server:" + " localhost:2511");
                    out.println("Date: " + new Date());
                    out.println("Content-length: 0");
                    out.println();
                    out.flush();
                }

//                while (parser.hasMoreTokens()) {
//                    String token = parser.nextToken();
//                    System.out.println(token);
//                }

            } else if (method.equals("POST")) {

            } else {
                out.println("HTTP/1.1 405 Method Not Allowed");
                out.println("Server:" + " localhost:2511");
                out.println("Date: " + new Date());
                out.println("Content-length: 0");
                out.println();
                out.flush();

//                dataOut.write(response.getBytes(), 0, response.getBytes().length);
//                dataOut.flush();
            }



//            String response = "Hello World !";
//
//            // send HTTP Headers
//            out.println("HTTP/1.1 200 OK");
//            out.println("Server:" + " localhost:2511");
//            out.println("Date: " + new Date());
//            out.println("Content-type: " + "text/html");
//            out.println("Content-length: " + response.getBytes().length);
//            out.println("Set-Cookie: " + "Battleship=" + "123456789");
//            out.println(); // blank line between headers and content, very important !
//            out.flush(); // flush character output stream buffer
//
//            dataOut.write(response.getBytes(), 0, response.getBytes().length);
//            dataOut.flush();

        } catch (Exception e){
            e.printStackTrace();
        }
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