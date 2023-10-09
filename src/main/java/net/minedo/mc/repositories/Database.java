package net.minedo.mc.repositories;

import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Database object for making calls to database.
 */
public class Database {

    private final String url;
    private final String user;
    private final String password;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private Connection connection;

    /**
     * Initialize database caller.
     */
    public Database() {
        Dotenv dotenv = Dotenv.load();

        this.url = dotenv.get("DB_URL");
        this.user = dotenv.get("DB_USER");
        this.password = dotenv.get("DB_PASSWORD");
    }

    /**
     * Connect to database.
     */
    public void connect() {
        try {
            this.connection = DriverManager.getConnection(this.url, this.user, this.password);
        } catch (SQLException e) {
            this.logger.severe(String.format("Unable to connect to the database: %s", e.getMessage()));
        }
    }

    /**
     * Disconnect from the database.
     */
    public void disconnect() {
        if (this.connection == null) {
            return;
        }

        try {
            this.connection.close();
        } catch (SQLException exception) {
            this.logger.severe(String.format("Unable to disconnect from the database: %s", exception.getMessage()));
            throw new RuntimeException(exception);
        }
    }

    /**
     * Query without conditions.
     *
     * @param sqlQuery SQL statement
     * @return result set
     */
    public @NotNull ResultSet query(String sqlQuery) {
        ResultSet resultSet;

        try {
            Statement statement = this.connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
        } catch (SQLException exception) {
            this.logger.severe(String.format("Unable to query the database: %s", exception.getMessage()));
            throw new RuntimeException(exception);
        }

        return resultSet;
    }

    /**
     * Query with conditions.
     *
     * @param sqlQuery     SQL statement
     * @param replacements conditions
     * @return result set
     */
    public @NotNull ResultSet queryWithWhereClause(String sqlQuery, HashMap<Integer, String> replacements) {
        ResultSet resultSet;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sqlQuery);

            for (var replacement : replacements.entrySet()) {
                preparedStatement.setString(replacement.getKey(), replacement.getValue());
            }

            resultSet = preparedStatement.executeQuery();
        } catch (SQLException exception) {
            this.logger.severe(
                    String.format("Unable to query with conditions from the database: %s", exception.getMessage())
            );
            throw new RuntimeException(exception);
        }

        return resultSet;
    }

    /**
     * Execute statement with conditions.
     *
     * @param sqlQuery     SQL statement
     * @param replacements conditions
     */
    public void executeStatement(String sqlQuery, HashMap<Integer, ?> replacements) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sqlQuery);

            for (var replacement : replacements.entrySet()) {
                int key = replacement.getKey();
                Object value = replacement.getValue();

                if (value instanceof Timestamp) {
                    preparedStatement.setTimestamp(key, (Timestamp) value);
                } else {
                    preparedStatement.setString(key, (String) value);
                }
            }

            preparedStatement.execute();
        } catch (SQLException exception) {
            this.logger.severe(
                    String.format("Unable to execute statement in the database: %s", exception.getMessage())
            );
            throw new RuntimeException(exception);
        }
    }

}
