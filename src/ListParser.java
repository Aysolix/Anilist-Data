import com.bastiaanjansen.url.URIBuilder;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class ListParser {
    String username;

    public ListParser() {
        this.username = "";
    }

    public HttpRequest fetchJSONResponse(String username) throws URISyntaxException {
        this.username = username;

        // https://anilist.co/graphiql
        String query = String.format("""
                {
                  MediaListCollection(userName: "%s", type: ANIME, status: COMPLETED, sort: SCORE_DESC) {
                    user {
                        id
                        name
                    }
                    lists {
                      name
                      isCustomList
                      status
                      entries {
                        id
                        score
                        media {
                          id
                          title {
                            romaji
                            english
                          }
                        }
                      }
                    }
                  }
                }
                """, username);

        URI baseUri = URI.create("https://graphql.anilist.co");
        URI uri = new URIBuilder(baseUri)
                .withQueryParameter("query", query)
                .build();
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(query))
                .build();
    }

    public JSONObject readJSONFile(String file) throws IOException {
        File f = new File(file);
        JSONObject animeListJSON = null;
        if (f.exists()){
            InputStream is = new FileInputStream(file);
            String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
            animeListJSON = new JSONObject(jsonTxt);
        }
        return animeListJSON;
    }

    public LinkedHashMap<String, Object[]> getEntries(JSONObject animeListJSON) throws JSONException {
        JSONObject data = animeListJSON.getJSONObject("data");
        JSONObject mediaListCollection = data.getJSONObject("MediaListCollection");
        JSONObject user = mediaListCollection.getJSONObject("user");
        int id = user.getInt("id");
        username = user.get("name").toString();
        JSONArray lists = mediaListCollection.getJSONArray("lists");
        JSONObject list = lists.getJSONObject(0);
        JSONArray entries = list.getJSONArray("entries");

        LinkedHashMap<String, Object[]> animeListScores = new LinkedHashMap<>();
        for (int i = 0; i < entries.length(); i++) {
            JSONObject entry = entries.getJSONObject(i);
            float score = entry.getFloat("score");
            JSONObject media = entry.getJSONObject("media");
            JSONObject title = media.getJSONObject("title");
            String romajiTitle = title.get("romaji").toString();
            String englishTitle = title.get("english").toString();

            // Build list for value of hashmap
            Object[] values = {score, id, username};


            if (!englishTitle.equals("null")) {
                if (romajiTitle.contains("OVA") && !englishTitle.contains("OVA"))
                    englishTitle = englishTitle + " OVA";
                animeListScores.put(englishTitle, values);
            } else
                animeListScores.put(romajiTitle, values);
        }
        return animeListScores;
    }
}
