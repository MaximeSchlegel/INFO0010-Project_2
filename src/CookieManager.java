import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.util.Pair;



public class CookieManager {
    private Map<String, BatThomi> savedGames;

    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz" + "0123456789"; //length = 62
    private static final int COOKIE_LENGTH = 26;
    private static final int[] SHIP_SIZE = {2,3,3,4,5};

    public CookieManager() {
        this.savedGames = new HashMap<>();
    }

    public boolean isUsed(String id) {
        return this.savedGames.containsKey(id);
    }

    public Pair<String,BatThomi> getNewGame() {
        Random rand = new Random();
        StringBuilder idBuilder;
        String id;

        //generate an used id
        do {
            idBuilder = new StringBuilder();
            for (int i = 0; i < COOKIE_LENGTH; i++) {
                idBuilder.append(ALLOWED_CHARACTERS.charAt(rand.nextInt(ALLOWED_CHARACTERS.length())));
            }
            id = idBuilder.toString();
        } while(this.isUsed(id));

        //create a new game
        BatThomi newGame = new BatThomi(SHIP_SIZE);

        this.savedGames.put(id, newGame);
        return new Pair<>(id,newGame);
    }

    public BatThomi getGame(String id){
        if(this.isUsed(id)) {
            return this.savedGames.get(id);
        }
        return null;
    }

    public void deleteGame(String id) {
        if(this.isUsed(id)){
            this.savedGames.remove(id);
        }
    }

}





