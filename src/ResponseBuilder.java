import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Set;

public class ResponseBuilder {

    public String executeResponse(String username) {
        StringBuilder animeListString = new StringBuilder();
        ListBuilder lb = new ListBuilder();
        lb.buildList("animelist.json", username);
        LinkedHashMap<String, Object[]> animeListScores = lb.getAnimeListScores();
        DatabaseCommunicator dc = new DatabaseCommunicator();
        try {
            for (String anime : animeListScores.keySet()) {
                animeListString.append("    ").append(anime).append("\n");
                String animeStr = anime;
                if (anime.contains("'"))
                    animeStr = animeStr.replace("'", "''");

                String userStr = animeListScores.get(anime)[2].toString();
                if (userStr.contains("'"))
                    userStr = userStr.replace("'", "''");

                dc.executeSQL(String.format("exec dbo.UpdateAnimeList '%s', %2d, %2d, '%s'",
                        animeStr, Math.round((Float) animeListScores.get(anime)[0]),
                        (int) animeListScores.get(anime)[1], userStr));
            }
            return animeListString.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
