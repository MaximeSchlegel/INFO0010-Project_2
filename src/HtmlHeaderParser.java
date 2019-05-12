import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

public class HtmlHeaderParser {
    private HashMap<String, String> fields;

    public HtmlHeaderParser(BufferedReader input) throws IOException {
        String line = input.readLine();
        StringTokenizer lineTokenizer = new StringTokenizer(line);
        fields.put("httpMethod", lineTokenizer.nextToken());
        fields.put("httpQuery", lineTokenizer.nextToken());

        do  {
            line = input.readLine();
            lineTokenizer = new StringTokenizer(line);
            String name = lineTokenizer.nextToken();
            fields.put(name, lineTokenizer.nextToken());
        } while (!line.equals(""));
    }

    public String get(String id) {
        return fields.get(id);
    }
}
