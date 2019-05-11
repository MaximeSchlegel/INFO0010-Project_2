import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class CookieManager {
    /*Handle the loading and saving of game linked to a cookie id
    * Generate the cookie id and new game*/
    private boolean verbose;
    private int last_cookie_id;
    private Map<String, BattleshipGame> saved_game;

    public CookieManager() {
        this.verbose = true;
        this.last_cookie_id = 0;
        this.saved_game = new HashMap<>();

        System.out.println("New cookie manager created");
    }

    public CookieManager(boolean verbose) {
        this.verbose = verbose;
        this.last_cookie_id = 0;
        this.saved_game = new HashMap<>();

        if (verbose) {
            System.out.println("New cookie manager created");
        }
    }

    public boolean isUsed(String id) {
        if (Integer.parseInt(id) > this.last_cookie_id) {
            return false;
        }
        return this.saved_game.containsKey(id);
    }

    public Pair<String, BattleshipGame> getNewGame() {
        this.last_cookie_id++;
        String id = Integer.toString(this.last_cookie_id);
        BattleshipGame newGame = new BattleshipGame(this.verbose);
        this.saved_game.put(id, newGame);
        return new Pair<>(id, newGame);
    }

    public BattleshipGame loadGame(String id) {
        return this.saved_game.get(id);
    }

    public void delete(String id) {
        this.saved_game.remove(id);
    }

}
