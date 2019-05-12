import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import javafx.util.Pair;


public class BattleshipHTTPHandler implements Runnable{
    //version of the game
    private static final int VERSION = 2;

    //web folder and files to return
    private static final File WEB_ROOT = new File("./web/");
    private static final String FILE_NOT_FOUND = "404.html";
    private static final String METHOD_NOT_SUPPORTED = "not_supported.html";

    //http header variable
    private static String SERVER_DETAILS = "Battleship Http Server";

    //connection and i/o to the client
    private Socket connectedClient;
    private BufferedReader inFromClient;
    private PrintWriter headerOut;
    private BufferedOutputStream dataOut;

    //reference server for hightscore and saved games
    private BattleshipHTTPServer master;

    private boolean verbose;
    private String cookie;
    BatThomi Game;


    public BattleshipHTTPHandler(Socket socket, BattleshipHTTPServer server) {
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

            String id_from_get = "";
            //parse get if there is any
            //parse cookie
            System.out.println("httprequest: "+httpQuerry);

            //did we get variables with the get ?
            if(httpQuerry.indexOf('?') >=0) {
                String[] splitHttpQuery = httpQuerry.split("\\?");
                httpQuerry = splitHttpQuery[0];
                String getVariables = splitHttpQuery[1];
                if (!getVariables.equals("")) {
                    String[] splitedGet = getVariables.split("=|;");
                    while (requestTokenizer.hasMoreTokens() && !splitedGet[0].equals("id")) {
                        id_from_get = requestTokenizer.nextToken();
                        splitedGet = id_from_get.split("=|;");
                    }
                    if (splitedGet[0].equals("id") && splitedGet.length == 2) {
                        id_from_get = splitedGet[1];
                    } else {
                        id_from_get = "";
                    }
                }
                System.out.println("id_from= " + id_from_get);
            }

            //get the host name
            String hostLine = inFromClient.readLine(); //get the second line to extract the host address
            StringTokenizer hostTokenizer = new StringTokenizer(hostLine);
            hostTokenizer.nextToken();
            String httpHost = hostTokenizer.nextToken();

            //get the cookie name
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
            //parse cookie
            if (!this.cookie.equals("")){
                String[] splitedCookie = this.cookie.split("=|;");
                while (CookieTokenizer.hasMoreTokens() && !splitedCookie[0].equals("Battleship")){
                    this.cookie = CookieTokenizer.nextToken();
                }
                if (splitedCookie[0].equals("Battleship") && splitedCookie.length == 2){
                    this.cookie = splitedCookie[1];
                } else {
                    this.cookie = "";
                }
            }
            System.out.println("Cookie: " + this.cookie);


            if (httpMethod.equals("GET")) {
                if (httpQuerry.equals("/")) {
                    if (this.verbose) {
                        System.out.println("Got GET resquest for root");
                    }

                    //if the client required the root, we redirect him on the play page
                    headerOut.println("HTTP/1.1 303 See Other");
                    headerOut.println("Server: " + SERVER_DETAILS);
                    headerOut.println("Date: " + new Date());
                    headerOut.println("Location: " + "http://" +httpHost + "/play.html");
                    headerOut.println("Connection: close");
                    headerOut.println("Content-length: 0");
                    headerOut.println();
                    headerOut.flush();

                } else if (httpQuerry.equals("/play.html")) {
                    if (this.verbose) {
                        System.out.println("Got GET resquest for /play.html");
                    }

                    if (this.master.cookieManager.isUsed(this.cookie)) {
                        this.Game = this.master.cookieManager.getGame(this.cookie);
                    } else {
                        Pair<String, BatThomi> p = this.master.cookieManager.getNewGame();
                        this.cookie = p.getKey();
                        this.Game = p.getValue();
                    }
                    if(this.Game.getNmbTries() >=70){
                        headerOut.println("HTTP/1.1 303 See Other");
                        headerOut.println("Server: " + SERVER_DETAILS);
                        headerOut.println("Date: " + new Date());
                        headerOut.println("Location: " + "http://" +httpHost + "/hall_of_fame.html");
                        headerOut.println("Connection: close");
                        headerOut.println("Content-length: 0");
                        headerOut.println();
                        headerOut.flush();
                    }
                    if(this.Game.check_win()){
                        headerOut.println("HTTP/1.1 303 See Other");
                        headerOut.println("Server: " + SERVER_DETAILS);
                        headerOut.println("Date: " + new Date());
                        headerOut.println("Location: " + "http://" +httpHost + "/win.html");
                        headerOut.println("Connection: close");
                        headerOut.println("Content-length: 0");
                        headerOut.println();
                        headerOut.flush();
                    }
                    //with the id ?
                    if(!id_from_get.equals("")) {
                        //got get from javascript ajax need only to send one number
                        try {
                            int id = Integer.parseInt(id_from_get);
                            //first update gamestate
                            int value = this.Game.boom(id);
                            // send HTTP Headers
                            headerOut.println("HTTP/1.1 200 OK");
                            headerOut.println("Server: " + httpHost);
                            headerOut.println("Date: " + new Date());
                            headerOut.println("Content-type: " + "text/html");
//
                            headerOut.println("Content-length: 1");
                            headerOut.println(); // blank line between headers and content, very important !
                            headerOut.flush(); // flush character output stream buffer
//
                            headerOut.print(value);
                            headerOut.flush();

                        } catch (Exception e) {
                            System.out.println("Wrong variable through GET: " + e);
                        }
                    }
                    else {
                        // send HTTP Headers
                        headerOut.println("HTTP/1.1 200 OK");
                        headerOut.println("Server: " + httpHost);
                        headerOut.println("Date: " + new Date());
                        headerOut.println("Content-type: " + "text/html");
                        if (this.verbose) {
                            System.out.println("Got the cookies figured out");
                            System.out.println("Cookie: " + this.cookie);
                        }
                        sendPlay();
                    }
                } else if (httpQuerry.equals("/hall_of_fame.html")) {
                    if (this.verbose) {
                        System.out.println("Got GET resquest for /hall_of_fame.html");
                    }
                    //return the hall of fame page
                    this.sendHallOfFame();

                } else if (httpQuerry.equals("/win.html")) {
                    if (this.verbose) {
                        System.out.println("Got GET resquest for /hall_of_fame.html");
                    }
                    //return the hall of fame page
                    this.sendWin();

                }

                else {
                    //this page does not exist
                    this.fileNotFound(httpQuerry);
                }

            } else if (httpMethod.equals("POST")) {
                if (this.verbose) {
                    System.out.println("Got POST resquest");
                }
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

    private void sendPlay() throws IOException {
        String play_html = "";
        play_html += "<!DOCTYPE html>\r\n";
        play_html += "<html lang=\"en\">\r\n";
        play_html += "<head>\r\n";
        play_html += "<meta charset=\"UTF-8\">\r\n";
        play_html += "<title>Battleship - Play</title>\r\n";
        play_html += "<link rel=\"icon\" type=\"image/jpg\" href=\"" + "data:image/png;base64,/9j/4QAWRXhpZgAATU0AKgAAAAgAAAAAAAD//gA7Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcgSlBFRyB2NjIpLCBxdWFsaXR5ID0gOTAK/9sAQwABAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB/9sAQwEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB/8AAEQgAGQAZAwEiAAIRAQMRAf/EABgAAAMBAQAAAAAAAAAAAAAAAAgJCgAH/8QAMRAAAQQCAQIDAg8AAAAAAAAABAIDBQYBBwAIEhETIQkUChYaMUFCU1hik5ei0dLX/8QAGAEAAgMAAAAAAAAAAAAAAAAABwgEBQb/xAAmEQABBAIBAwQDAQAAAAAAAAADAQIEBQYHERITFAAhIiMIFRdB/9oADAMBAAIRAxEAPwB8UYMO6p1+RnK/XI4dKHTp63WSKqtci2nimARlSdgnTAosD3yRMDjQUvEJdOkTBARkOkvtt5HXr5tO8NG9L+wLXpo6Dr+2F/EtrXlgnIQbYFYfanbjCAGy0XGhPlRNvEfhFSYwhIJB0eM4/k9zClBYSnlvtDNURO3ej3asXJ5U07R3aluOCd8xxDaJ/U1pjbUI2S2lXYSMaAzLxjoxCHWM5PS92YeYZdbRXp/qq2hqOryWs0HZuWnZV9Z7usbOUWVBV+wpbcSDd9ekIewbry7Rbi8utyNfW3DzTSno61wk5HkKS1mrrM241kNdW2Riw6e1hK2Rbwo3mWVOpivjOsQwynGGw8VFbJ8NzguKguhpVcvQ4v6I/GOw3RgeQ55jroN1kGDZdEB/PLuWWjp85r4sOBbmonZVE7srHpVux5awM5YcgIXHQxTRGMWQymrRm3ojfuldZ7lgg5SJB2DVQ5lyImQCImWgplh8qFtEBIRxLrz4hMDaombiFIceeUpsJt7Lq8Od3Oq+Yr7Vf5qv7cG/o9ldH3XQ7Fo0hds2eaKfas+7Kme5gK01LZEvHAC2mck6m8tZ4AdkPARJH2YV+WgLdLvF2ZmZzKyUxHiEF3p/H+7+eEmDcwLASyK6wBZx0I8KTorSCDKUL1H5DBF+4DToiGYA/wBwmEa0qI9rk9K7k2NXWK3lhR32P2uMWUM7kNRXbGfs61hFaQUSWRjBhklAJ6CdMjNSLLViyYvMcg1WRTbvwgm4bO1LsfWYnSrUYQi+U6bqjM0Rt+wyLMU7Lje7pOdj26KE4agfxyrIyTBlOemMPI+fis4zrNkMt4xNa0DS9hPquGuLq28r8fXKUSNbZcSn6U9zrisemM5+twK+bhpvdGa2yYoC2lIVxIwVCJ4LS1AqDUncVF6JvDuXqq+/PH+cemL1jufY2m4dhX68yBKOBbzmT7GI+sqLUUmYwDIzJCuuIE8oXIATB9McgWORqK5qr7+mM0br7I11boO7VOrXKHnII8QtomIuw8IQWIyUwQbCkSUYOgzETMMMqj5MVSCGCBX3POEIzhLWWt/KTbH9y+k/rVP/AObcmL5uSMZ0tr/GGTBVdTI6JZAvKkm0tJHyE1yN6OZjUZ8XqjuEVXcN5X4pxWbX2Vle65lPYbDlxbaXRR5USuPEroNM8ceYUBjDMtOCEslvcjscJJKlQCuKoe2pzK//2Q=="+ "\" />\r\n";
        play_html += "</head>\r\n";
        play_html += "<body>\r\n";
        play_html += "<h1>Play</h1>\r\n";
        play_html += "<script type=\"text/javascript\">\r\n";
        play_html += "var gamestate = " + java.util.Arrays.toString(this.Game.peekabou()) + ";\r\n";
        play_html += "        var background = new Image();\r\n";
        play_html +=  "       background.src= \"data:image/png;base64," + this.master.wauta  + "\";\r\n";
        play_html += "        nuage = new Image();\r\n";
        play_html += "       nuage.src = \"data:image/png;base64," + this.master.claudy + "\";\r\n";
        play_html += "        explosion = new Image();\r\n";
        play_html += "       explosion.src = \"data:image/png;base64," + this.master.explosion + "\";\r\n";
        play_html += "        var context;\r\n";
        play_html += "        function charger(){\r\n";
        play_html += "        canvas = document.getElementById('field');\r\n";
        play_html += "        context = canvas.getContext('2d');\r\n";
        play_html += "        draw();\r\n";
        play_html += "        canvas.style.display = 'block';\r\n";
        play_html += "        }\r\n";
        play_html += "        function draw(){\r\n";
        play_html += "        context.drawImage(background,0,0,500,500);\r\n";
        play_html += "        var x;\r\n";
        play_html += "        var y;\r\n";
        play_html += "        for(var id=0;id<100;id++)\r\n";
        play_html += "        {\r\n";
        play_html += "        //print the corresponding picture on screen\r\n";
        play_html += "        if(gamestate[id] >0 && gamestate[id] <8)\r\n";
        play_html += "        context.drawImage(explosion,((id%10))*50,Math.floor(id/10)*50,50,50);\r\n";
        play_html += "        else if(gamestate[id] ==8 )\r\n";
        play_html += "        context.drawImage(nuage,((id%10))*50,Math.floor(id/10)*50,50,50);\r\n";
        play_html += "        //ici faut avoir un truc pour choisir quel image\r\n";
        play_html += "        context.drawImage(nuage,(x)*50,(y)*50,50,50);\r\n";
        play_html += "\r\n";
        play_html += "        }\r\n";
        play_html += "        }\r\n";
        play_html += "        function shoot(){\r\n";
        play_html += "        //get mouse coord\r\n";
        play_html += "        var mx = event.clientX-  document.getElementById(\"field\").offsetLeft -  document.getElementById(\"parent\").offsetLeft + document.body.scrollLeft;";
        play_html += "        var my = event.clientY- document.getElementById(\"field\").offsetTop -  document.getElementById(\"parent\").offsetTop + document.body.scrollTop;";
        play_html += "\r\n";
        play_html += "\r\n";
        play_html += "\r\n";
        play_html += "        //convertir en identité grid?\r\n";
        play_html += "        var id = Math.floor(mx/50) + Math.floor(my/50)*10;\r\n";
        play_html += "        //alert(id);\r\n";
        play_html += "\r\n";
        play_html += "        //TODO: do ajax to send grid identity\r\n";
        play_html += "        var request = new XMLHttpRequest();\r\n";
        play_html += "\r\n";
        play_html += "        request.onreadystatechange = function() {\r\n";
        play_html += "        if (this.readyState == 4 && this.status == 200) {\r\n";
        play_html += "        //change the gamestate\r\n";
        play_html += "        gamestate[id] = parseInt(this.responseText);\r\n";
        play_html += "        //redraw\r\n";
        play_html += "        draw();\r\n";
        play_html += "        }\r\n";
        play_html += "        };\r\n";
        play_html += "        request.open(\"GET\", \"/play.html?id=\" + id, true);\r\n";
        play_html += "        //request.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\r\n";
        play_html += "        request.send();\r\n";

        play_html += "\r\n";
        play_html += "        }\r\n";
        play_html += "\r\n";
        play_html += "        </script>\r\n";
        play_html += "        <!-- faut remplacer les valeurs d'une facon ou d'une autre -->\r\n";
        play_html += "        <div id=\"parent\" style=\"position: relative;max-width: 500px;max-height: 500px;\">\r\n";
        play_html += "        <canvas id=\"field\"  style=\"display: none;position: relative;left: 10px;top: 0px;z-index: 1;\" width=\"500\" height=\"500\" ></canvas>\r\n";
        play_html += "        </div>\r\n";
        play_html += "\r\n";
        play_html += "        <script type=\"text/javascript\">\r\n";
        play_html += "        window.onload = charger;\r\n";
        play_html += "        document.getElementById(\"field\").onclick=shoot;\r\n";
        play_html += "        </script>\r\n";
        play_html += "\r\n";
        play_html += "        </body>\r\n";
        play_html += "        </html>\r\n";

        this.headerOut.println("HTTP/1.1 200 OK");
        this.headerOut.println("Server: " + SERVER_DETAILS);
        this.headerOut.println("Date: " + new Date());
        this.headerOut.println("Content-type: " + "text/html");
        headerOut.println("Set-Cookie: " + "Battleship=" + this.cookie);
        this.headerOut.println("Content-length: " + play_html.getBytes().length);
        this.headerOut.println();
        this.headerOut.flush();

        headerOut.print(play_html);
        headerOut.flush();

    }

    private void sendHallOfFame() throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "   <head>\n" +
            "<link rel=\"icon\" type=\"image/jpg\" href=\"" + "data:image/png;base64,/9j/4QAWRXhpZgAATU0AKgAAAAgAAAAAAAD//gA7Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcgSlBFRyB2NjIpLCBxdWFsaXR5ID0gOTAK/9sAQwABAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB/9sAQwEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB/8AAEQgAGQAZAwEiAAIRAQMRAf/EABgAAAMBAQAAAAAAAAAAAAAAAAgJCgAH/8QAMRAAAQQCAQIDAg8AAAAAAAAABAIDBQYBBwAIEhETIQkUChYaMUFCU1hik5ei0dLX/8QAGAEAAgMAAAAAAAAAAAAAAAAABwgEBQb/xAAmEQABBAIBAwQDAQAAAAAAAAADAQIEBQYHERITFAAhIiMIFRdB/9oADAMBAAIRAxEAPwB8UYMO6p1+RnK/XI4dKHTp63WSKqtci2nimARlSdgnTAosD3yRMDjQUvEJdOkTBARkOkvtt5HXr5tO8NG9L+wLXpo6Dr+2F/EtrXlgnIQbYFYfanbjCAGy0XGhPlRNvEfhFSYwhIJB0eM4/k9zClBYSnlvtDNURO3ej3asXJ5U07R3aluOCd8xxDaJ/U1pjbUI2S2lXYSMaAzLxjoxCHWM5PS92YeYZdbRXp/qq2hqOryWs0HZuWnZV9Z7usbOUWVBV+wpbcSDd9ekIewbry7Rbi8utyNfW3DzTSno61wk5HkKS1mrrM241kNdW2Riw6e1hK2Rbwo3mWVOpivjOsQwynGGw8VFbJ8NzguKguhpVcvQ4v6I/GOw3RgeQ55jroN1kGDZdEB/PLuWWjp85r4sOBbmonZVE7srHpVux5awM5YcgIXHQxTRGMWQymrRm3ojfuldZ7lgg5SJB2DVQ5lyImQCImWgplh8qFtEBIRxLrz4hMDaombiFIceeUpsJt7Lq8Od3Oq+Yr7Vf5qv7cG/o9ldH3XQ7Fo0hds2eaKfas+7Kme5gK01LZEvHAC2mck6m8tZ4AdkPARJH2YV+WgLdLvF2ZmZzKyUxHiEF3p/H+7+eEmDcwLASyK6wBZx0I8KTorSCDKUL1H5DBF+4DToiGYA/wBwmEa0qI9rk9K7k2NXWK3lhR32P2uMWUM7kNRXbGfs61hFaQUSWRjBhklAJ6CdMjNSLLViyYvMcg1WRTbvwgm4bO1LsfWYnSrUYQi+U6bqjM0Rt+wyLMU7Lje7pOdj26KE4agfxyrIyTBlOemMPI+fis4zrNkMt4xNa0DS9hPquGuLq28r8fXKUSNbZcSn6U9zrisemM5+twK+bhpvdGa2yYoC2lIVxIwVCJ4LS1AqDUncVF6JvDuXqq+/PH+cemL1jufY2m4dhX68yBKOBbzmT7GI+sqLUUmYwDIzJCuuIE8oXIATB9McgWORqK5qr7+mM0br7I11boO7VOrXKHnII8QtomIuw8IQWIyUwQbCkSUYOgzETMMMqj5MVSCGCBX3POEIzhLWWt/KTbH9y+k/rVP/AObcmL5uSMZ0tr/GGTBVdTI6JZAvKkm0tJHyE1yN6OZjUZ8XqjuEVXcN5X4pxWbX2Vle65lPYbDlxbaXRR5USuPEroNM8ceYUBjDMtOCEslvcjscJJKlQCuKoe2pzK//2Q=="+ "\" />\r\n" +
            "       <meta charset=\"UTF-8\">\n" +
            "       <title>Battleship - Hall of Fame</title>\n" +
            "   </head>\n" +
            "   <body>\n" +
            "       <h1>Hall of Fame</h1>\n");

        ArrayList<Pair<String, Integer>> halloffame = this.master.bestGames.getScore();
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

    private void sendWin() throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "   <head>\n" +
                        "<link rel=\"icon\" type=\"image/jpg\" href=\"" + "data:image/png;base64,/9j/4QAWRXhpZgAATU0AKgAAAAgAAAAAAAD//gA7Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcgSlBFRyB2NjIpLCBxdWFsaXR5ID0gOTAK/9sAQwABAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB/9sAQwEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB/8AAEQgAGQAZAwEiAAIRAQMRAf/EABgAAAMBAQAAAAAAAAAAAAAAAAgJCgAH/8QAMRAAAQQCAQIDAg8AAAAAAAAABAIDBQYBBwAIEhETIQkUChYaMUFCU1hik5ei0dLX/8QAGAEAAgMAAAAAAAAAAAAAAAAABwgEBQb/xAAmEQABBAIBAwQDAQAAAAAAAAADAQIEBQYHERITFAAhIiMIFRdB/9oADAMBAAIRAxEAPwB8UYMO6p1+RnK/XI4dKHTp63WSKqtci2nimARlSdgnTAosD3yRMDjQUvEJdOkTBARkOkvtt5HXr5tO8NG9L+wLXpo6Dr+2F/EtrXlgnIQbYFYfanbjCAGy0XGhPlRNvEfhFSYwhIJB0eM4/k9zClBYSnlvtDNURO3ej3asXJ5U07R3aluOCd8xxDaJ/U1pjbUI2S2lXYSMaAzLxjoxCHWM5PS92YeYZdbRXp/qq2hqOryWs0HZuWnZV9Z7usbOUWVBV+wpbcSDd9ekIewbry7Rbi8utyNfW3DzTSno61wk5HkKS1mrrM241kNdW2Riw6e1hK2Rbwo3mWVOpivjOsQwynGGw8VFbJ8NzguKguhpVcvQ4v6I/GOw3RgeQ55jroN1kGDZdEB/PLuWWjp85r4sOBbmonZVE7srHpVux5awM5YcgIXHQxTRGMWQymrRm3ojfuldZ7lgg5SJB2DVQ5lyImQCImWgplh8qFtEBIRxLrz4hMDaombiFIceeUpsJt7Lq8Od3Oq+Yr7Vf5qv7cG/o9ldH3XQ7Fo0hds2eaKfas+7Kme5gK01LZEvHAC2mck6m8tZ4AdkPARJH2YV+WgLdLvF2ZmZzKyUxHiEF3p/H+7+eEmDcwLASyK6wBZx0I8KTorSCDKUL1H5DBF+4DToiGYA/wBwmEa0qI9rk9K7k2NXWK3lhR32P2uMWUM7kNRXbGfs61hFaQUSWRjBhklAJ6CdMjNSLLViyYvMcg1WRTbvwgm4bO1LsfWYnSrUYQi+U6bqjM0Rt+wyLMU7Lje7pOdj26KE4agfxyrIyTBlOemMPI+fis4zrNkMt4xNa0DS9hPquGuLq28r8fXKUSNbZcSn6U9zrisemM5+twK+bhpvdGa2yYoC2lIVxIwVCJ4LS1AqDUncVF6JvDuXqq+/PH+cemL1jufY2m4dhX68yBKOBbzmT7GI+sqLUUmYwDIzJCuuIE8oXIATB9McgWORqK5qr7+mM0br7I11boO7VOrXKHnII8QtomIuw8IQWIyUwQbCkSUYOgzETMMMqj5MVSCGCBX3POEIzhLWWt/KTbH9y+k/rVP/AObcmL5uSMZ0tr/GGTBVdTI6JZAvKkm0tJHyE1yN6OZjUZ8XqjuEVXcN5X4pxWbX2Vle65lPYbDlxbaXRR5USuPEroNM8ceYUBjDMtOCEslvcjscJJKlQCuKoe2pzK//2Q=="+ "\" />\r\n" +
                        "       <meta charset=\"UTF-8\">\n" +
                        "       <title>Battleship - Hall of Fame</title>\n" +
                        "   </head>\n" +
                        "   <body>\n" +
                        "       <h1>You Win</h1>\n");

        responseBuilder.append(

                " <form method=\"post\">\n" +
                        "    <p>Enter your name: <input type=\"text\" name=\"nom\" style=\"border-radius: 5px;\" placeholder=\"name\"></p>\n" +
                        "    <input type=\"submit\" value=\"Submit\" style=\"background: blue; color: white; border-radius: 5px; height: 35px; width: 70px;\">" +
                        " </body>\n" +
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
