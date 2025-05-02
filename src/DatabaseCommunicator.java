import java.sql.*;

public class DatabaseCommunicator {
    private Statement statement;

    // Connect to your database.
    // Replace server name, username, and password with your credentials
    public DatabaseCommunicator() {
        String connectionUrl = "";
        String user = "";
        String password = "";

        try {
            Connection connection = DriverManager.getConnection(connectionUrl, user, password);
            statement = connection.createStatement();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeSQL(String query) throws SQLException {
        statement.execute(query);
    }
}
