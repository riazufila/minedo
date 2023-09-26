package net.minedo.mc.repositories;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.util.HashMap;
import java.util.logging.Logger;

public class Database {

    private final String url;
    private final String user;
    private final String password;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private Connection connection;

    public Database() {
        Dotenv dotenv = Dotenv.load();

        this.url = dotenv.get("DB_URL");
        this.user = dotenv.get("DB_USER");
        this.password = dotenv.get("DB_PASSWORD");
    }

    public void connect() {
        try {
            this.connection = DriverManager.getConnection(this.url, this.user, this.password);
        } catch (SQLException e) {
            this.logger.severe(String.format("Unable to connect to the database: %s", e.getMessage()));
        }
    }

    public void disconnect() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                this.logger.severe(String.format("Unable to disconnect from the database: %s", e.getMessage()));
            }
        }
    }

    public ResultSet query(String sqlQuery) {
        ResultSet resultSet = null;

        try {
            Statement statement = this.connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
        } catch (SQLException e) {
            this.logger.severe(String.format("Unable to query the database: %s", e.getMessage()));
        }

        return resultSet;
    }

    public ResultSet queryWithWhereClause(String sqlQuery, HashMap<Integer, String> replacements) {
        ResultSet resultSet = null;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sqlQuery);

            for (var replacement : replacements.entrySet()) {
                preparedStatement.setString(replacement.getKey(), replacement.getValue());
            }

            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            this.logger.severe(
                    String.format("Unable to query with conditions from the database: %s", e.getMessage())
            );
        }

        return resultSet;
    }

    public void executeStatement(String sqlQuery, HashMap<Integer, String> replacements) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sqlQuery);

            for (var replacement : replacements.entrySet()) {
                preparedStatement.setString(replacement.getKey(), replacement.getValue());
            }

            preparedStatement.execute();
        } catch (SQLException e) {
            this.logger.severe(
                    String.format("Unable to execute statement in the database: %s", e.getMessage())
            );
        }
    }

}
