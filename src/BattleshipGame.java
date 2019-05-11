//import java.util.ArrayList;
//import java.util.Random;
//
public class BattleshipGame {
    private int id;
    private int version;
//
//    private ArrayList<ArrayList<Byte>> ship;
//    private ArrayList<byte[]> history;
//
//    private static int gameLength = 70;
//
//    private void initializeShip(int len) {
//        Random r = new Random();
//        boolean done = false, cellUsed;
//        int begin, orientation;
//        ArrayList<Byte> position;
//        while (!done) {
//            begin = r.nextInt(99);
//            orientation = r.nextInt(3);
//            cellUsed = false;
//            if (orientation == 0
//                    && begin - 10 * len >= 0) {
//                position = new ArrayList<>();
//                for (int i=0; i < len; i++) {
//                    for(ArrayList<Byte> toTest : this.ship) {
//                        if (toTest.contains((byte) (begin - 10 * i))) {
//                            cellUsed = true;
//                        }
//                    }
//                    position.add((byte) (begin - 10 * i));
//                }
//                if (!cellUsed) {
//                    this.ship.add(position);
//                    done = true;
//                }
//            } else if (orientation == 1
//                    && (begin - begin % 10) - ((begin + len) - (begin + len) % 10) == 0) {
//                position = new ArrayList<>();
//                for (int i=0; i < len; i++) {
//                    for(ArrayList<Byte> toTest : this.ship) {
//                        if (toTest.contains((byte) (begin + i))) {
//                            cellUsed = true;
//                        }
//                    }
//                    position.add((byte) (begin + i));
//                }
//                if (!cellUsed) {
//                    this.ship.add(position);
//                    done = true;
//                }
//            } else if (orientation == 2
//                    && begin + 10 * len <= 99) {
//                position = new ArrayList<>();
//                for (int i=0; i < len; i++) {
//                    for(ArrayList<Byte> toTest : this.ship) {
//                        if (toTest.contains((byte) (begin + 10 * i))) {
//                            cellUsed = true;
//                        }
//                    }
//                    position.add((byte) (begin + 10 * i));
//                }
//                if (!cellUsed) {
//                    this.ship.add(position);
//                    done = true;
//                }
//            } else if ((begin - begin % 10) - ((begin - len) - ((begin - len) % 10)) == 0) {
//                position = new ArrayList<>();
//                for (int i=0; i < len; i++) {
//                    for(ArrayList<Byte> toTest : this.ship) {
//                        if (toTest.contains((byte) (begin - i))) {
//                            cellUsed = true;
//                        }
//                    }
//                    position.add((byte) (begin - i));
//                }
//                if (!cellUsed) {
//                    this.ship.add(position);
//                    done = true;
//                }
//            }
//        }
//    }
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
