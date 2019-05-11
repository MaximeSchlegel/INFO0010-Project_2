import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CookieManager {
    /*Handle the loading and saving of game linked to a cookie id
    * Generate the cookie id and new game*/
    private Map<String, BattleshipGame> saved_game;
    private String cookieCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz" + "0123456789"; //length = 62

    public CookieManager() {
        this.saved_game = new HashMap<>();
    }

    public boolean isUsed(String id) {
        return this.saved_game.containsKey(id);
    }

    public BattleshipGame getNewGame() {
        Random rand = new Random();
        String id = "";
        while(this.isUsed(id)) {
            id = "";
            for (int i = 0; i < 26; i++)
                id += cookieCharacters.charAt(rand.nextInt(cookieCharacters.length()));
        }

        BattleshipGame newGame = new BattleshipGame();
        this.saved_game.put(id, newGame);
        return newGame;
    }

    public BattleshipGame getGame(String id){
        if(this.isUsed(id))
            return this.saved_game.get(id);
        return null;
    }

    public BattleshipGame loadGame(String id) {
        return this.saved_game.get(id);
    }

    public void delete(String id) {
        this.saved_game.remove(id);
    }

}



