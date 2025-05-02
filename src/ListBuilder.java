import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;

public class ListBuilder {
    private final ListParser lp;
    private LinkedHashMap<String, Object[]> animeListScores;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    public ListBuilder() {
        this.lp = new ListParser();
    }

    public void buildList(String filename, String username) {
        try {
            HttpRequest request = lp.fetchJSONResponse(username);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            PrintWriter out = new PrintWriter(filename);
            out.print(response.body());
            out.close();
            JSONObject animeListJSON = lp.readJSONFile(filename);
            animeListScores = lp.getEntries(animeListJSON);
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedHashMap<String, Object[]> getAnimeListScores() {
        // Standardise scores
        float max = 0f;
        for (String anime : animeListScores.keySet()) {
            float score = (float) animeListScores.get(anime)[0];
            if (score > max) max = score;
        }

        if (max > 5 && max <= 10) {
            for (String anime : animeListScores.keySet()) {
                float score = (float) animeListScores.get(anime)[0];
                score = score * 10;
                Object[] values = {score, animeListScores.get(anime)[1], animeListScores.get(anime)[2]};
                animeListScores.put(anime, values);
            }
        }

        return animeListScores;
    }
}
