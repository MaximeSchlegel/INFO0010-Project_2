import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class CookieManagerThomi {
    /*Handle the loading and saving of game linked to a cookie id
    * Generate the cookie id and new game*/
    private int last_cookie_id;
    private Map<String, BatThomi> saved_games;

    public CookieManagerThomi() {
        this.last_cookie_id = 0;
        this.saved_games = new HashMap<>();
    }

    public boolean isUsed(String id) {
        if (Integer.parseInt(id) > this.last_cookie_id) {
            return false;
        }
        return this.saved_games.containsKey(id);
    }

    public Pair<String, BatThomi> getNewGame() {
        this.last_cookie_id++;
        String id = Integer.toString(this.last_cookie_id);
        BatThomi newGame = new BatThomi();
        this.saved_games.put(id, newGame);
        return new Pair<>(id, newGame);
    }

    public BatThomi loadGame(String id) {
        return this.saved_games.get(id);
    }

    public void delete(String id) {
        this.saved_games.remove(id);
    }

}

