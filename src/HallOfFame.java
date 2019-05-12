import java.util.ArrayList;
import javafx.util.Pair;

public class HallOfFame {
    private ArrayList<Pair<String, Integer>> bestScores;

    private static final int SIZE = 10;

    public HallOfFame() {
        this.bestScores = new ArrayList<>();
    }

    public void addScore(String id, int score) {
        for (int i=0; i < SIZE; i++) {
            if (score >  this.bestScores.get(i).getValue()) {
                this.bestScores.add(i, new Pair<>(id, score));
                if (this.bestScores.size() > SIZE) {
                    this.bestScores.remove(SIZE);
                }
                return;
            }
        }
    }

    public ArrayList<Pair<String, Integer>> getScore() {
        return this.bestScores;
    }
}
