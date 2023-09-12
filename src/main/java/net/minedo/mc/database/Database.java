package net.minedo.mc.database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.util.Map;

public class Database {

    private final String url;
    private final String user;
    private final String password;
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
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ResultSet query(String sqlQuery) {
        ResultSet resultSet = null;

        try {
            Statement statement = this.connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

    public ResultSet queryWithWhereClause(String sqlQuery, Map<Integer, String> replacements) {
        ResultSet resultSet = null;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sqlQuery);

            for (var replacement : replacements.entrySet()) {
                preparedStatement.setString(replacement.getKey(), replacement.getValue());
            }

            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

}