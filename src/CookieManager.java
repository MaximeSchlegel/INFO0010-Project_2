import javafx.util.Pair;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;



public class CookieManager {
    /*Handle the loading and saving of game linked to a cookie id
    * Generate the cookie id and new game*/
    private Map<String, BatThomi> saved_game;
    private String cookieCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz" + "0123456789"; //length = 62


    public CookieManager() {
        this.saved_game = new HashMap<>();
    }

    public boolean isUsed(String id) {
        return this.saved_game.containsKey(id);
    }

    public Pair<String,BatThomi> getNewGame() {
        Random rand = new Random();
        String id = "";
        for (int i = 0; i < 26; i++)
            id += cookieCharacters.charAt(rand.nextInt(cookieCharacters.length()));
        while(this.isUsed(id)) {
            id = "";
            for (int i = 0; i < 26; i++)
                id += cookieCharacters.charAt(rand.nextInt(cookieCharacters.length()));
        }

        int [] boats = {2,3,3,4,5};
        System.out.println("Got this far");
        BatThomi newGame = new BatThomi(boats);
        this.saved_game.put(id, newGame);
        return new Pair<>(id,newGame);
    }

    public BatThomi getGame(String id){
        if(this.isUsed(id))
            return this.saved_game.get(id);
        return null;
    }

    public BatThomi loadGame(String id) {
        return this.saved_game.get(id);
    }

    public void delete(String id) {
        this.saved_game.remove(id);
    }

}





