import java.util.ArrayList;
import java.util.Random;

//import java.util.ArrayList;
//import java.util.Random;
//
public class BattleshipGame {
    private static int VERRSION = 2;
    private static int GAME_LENGTH = 70;

    private ArrayList<ArrayList<Byte>> ship;
    private ArrayList<byte[]> history;

    private boolean verbose;


    public BattleshipGame() {
        this.verbose = true;

        this.history = new ArrayList<>();

        this.initializeShip(2);
        this.initializeShip(3);
        this.initializeShip(3);
        this.initializeShip(4);
        this.initializeShip(5);

        System.out.println("New game created.");
        this.displayBoard();
    }

    public BattleshipGame(boolean verbose) {
        this.verbose = verbose;

        this.history = new ArrayList<>();

        this.initializeShip(2);
        this.initializeShip(3);
        this.initializeShip(3);
        this.initializeShip(4);
        this.initializeShip(5);

        if (verbose) {
            System.out.println("New game created.");
            this.displayBoard();
        }
    }

    private void initializeShip(int len) {
        Random r = new Random();

        boolean done = false;
        boolean cellUsed;

        int begin, orientation;

        ArrayList<Byte> position;

        while (!done) {
            //choose the starting cell of the ship and its orientation
            begin = r.nextInt(99);
            orientation = r.nextInt(3);

            //test if the ship can be place (enough space on the line/column)
            cellUsed = false;
            if (orientation == 0
                    && begin - 10 * len >= 0) {
                position = new ArrayList<>();
                for (int i=0; i < len; i++) {
                    for(ArrayList<Byte> toTest : this.ship) {
                        if (toTest.contains((byte) (begin - 10 * i))) {
                            cellUsed = true;
                        }
                    }
                    position.add((byte) (begin - 10 * i));
                }
                if (!cellUsed) {
                    this.ship.add(position);
                    done = true;
                }
            } else if (orientation == 1
                    && (begin - begin % 10) - ((begin + len) - (begin + len) % 10) == 0) {
                position = new ArrayList<>();
                for (int i=0; i < len; i++) {
                    for(ArrayList<Byte> toTest : this.ship) {
                        if (toTest.contains((byte) (begin + i))) {
                            cellUsed = true;
                        }
                    }
                    position.add((byte) (begin + i));
                }
                if (!cellUsed) {
                    this.ship.add(position);
                    done = true;
                }
            } else if (orientation == 2
                    && begin + 10 * len <= 99) {
                position = new ArrayList<>();
                for (int i=0; i < len; i++) {
                    for(ArrayList<Byte> toTest : this.ship) {
                        if (toTest.contains((byte) (begin + 10 * i))) {
                            cellUsed = true;
                        }
                    }
                    position.add((byte) (begin + 10 * i));
                }
                if (!cellUsed) {
                    this.ship.add(position);
                    done = true;
                }
            } else if ((begin - begin % 10) - ((begin - len) - ((begin - len) % 10)) == 0) {
                position = new ArrayList<>();
                for (int i=0; i < len; i++) {
                    for(ArrayList<Byte> toTest : this.ship) {
                        if (toTest.contains((byte) (begin - i))) {
                            cellUsed = true;
                        }
                    }
                    position.add((byte) (begin - i));
                }
                if (!cellUsed) {
                    this.ship.add(position);
                    done = true;
                }
            }
        }
    }

    private void displayBoard () {
        //Display the discovered board to the user
        String[] board = new String[100];
        for (int i=0; i < 100; i++) {
            board[i] = "~";
        }
        for (int i=0; i < this.ship.size(); i++) {
            for (byte position : this.ship.get(i)) {
                if (i == 0) {
                    board[position] = "D";
                } else if (i == 1) {
                    board[position] = "S";
                } else if (i == 2) {
                    board[position] = "U";
                } else if (i == 3) {
                    board[position] = "B";
                } else {
                    board[position] = "A";
                }
            }
        }

        //The display
        System.out.println("    A B C D E F G H I J ");
        System.out.println("  -----------------------");
        StringBuilder line;
        for (int i=0; i < 10; i++) {
            line = new StringBuilder(i + " |");
            for(int j=0; j < 10; j++) {
                line.append(" ");
                line.append(board[10 * j + i]);
            }
            line.append(" |");
            if (i == 2) { line.append("   ~: Empty"); }
            if (i == 3) { line.append("   D: Destoyer"); }
            if (i == 4) { line.append("   S: Submarine"); }
            if (i == 5) { line.append("   U: Cruiser"); }
            if (i == 6) { line.append("   B: Battleship"); }
            if (i == 7) { line.append("   A: Carrier"); }
            System.out.println(line);
        }
        System.out.println("  -----------------------");
    }

//
//    private void shootHandler(byte position) throws Exception {
//        //handle the shhot dialogue this the client
//        //Expected exception: can't reach server (in send method or read)
//        if (0 > position || 99 < position) {
//            sendError();
//            return;
//        }
//
//        for (int shipNumber = 0; shipNumber < this.ship.size(); shipNumber++) {
//            //test each ship
//            for (byte i: this.ship.get(shipNumber)) {
//                if (i == position) {
//                    byte[] shoot = new byte[2];
//                    shoot[0] = position;
//                    shoot[1] = (byte) (shipNumber + 1);
//                    this.history.add(shoot);
//                    sendHit(shipNumber + 1);  // 0 is for a miss
//                    return;
//                }
//            }
//        }
//
//        // player have miss
//        byte[] shoot = new byte[2];
//        shoot[0] = position;
//        this.history.add(shoot);
//        sendMiss();
//    }
//
//
}
