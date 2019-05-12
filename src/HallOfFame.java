import java.util.ArrayList;
import javafx.util.Pair;

public class HallOfFame {
    private ArrayList<Pair<String,Pair<String,Integer>>> bestScores;

    private static final int SIZE = 10;

    public HallOfFame() {
        this.bestScores = new ArrayList<>();
    }

    public void addScore(String cookie, String id, int score) {
        int i = 0;
        for (i=0; i < this.bestScores.size(); i++) {
            if (score > this.bestScores.get(i).getValue().getValue()) {
                this.bestScores.add(i, new Pair<>(cookie,new Pair<>(id,score)));
                if (this.bestScores.size() > SIZE) {
                    this.bestScores.remove(SIZE);
                }
                return;
            }
        }
        //le mettre dedans a la fin
        if(i < SIZE)
            this.bestScores.add( new Pair<>(cookie,new Pair<>(id,score)));
    }

    public ArrayList<Pair<String,Pair<String,Integer>>> getScore() {
        return this.bestScores;
    }
}
