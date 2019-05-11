import java.util.ArrayList;
import javafx.util.Pair;

public class HallOfFame {
    private ArrayList<Pair<String, Integer>> bestScores;
    private int size;

    public HallOfFame() {
        this.bestScores = new ArrayList<>();
        this.size = 10;
    }

    public void addScore(String name, int score) {
        for (int i=0; i < this.size; i++) {
            if (score >  this.bestScores.get(i).getValue()) {
                this.bestScores.add(i, new Pair<>(name, score));
                if (this.bestScores.size() > this.size) {
                    this.bestScores.remove(this.size);
                }
                return;
            }
        }
    }

    public ArrayList<Pair<String, Integer>> getScore() {
        return this.bestScores;
    }
}
