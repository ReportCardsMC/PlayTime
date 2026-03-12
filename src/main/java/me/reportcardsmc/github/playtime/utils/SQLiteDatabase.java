package me.reportcardsmc.github.playtime.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class SQLiteDatabase {
    private static Connection connection;

    public static void initialize(File dataFolder) {
        try {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            File dbFile = new File(dataFolder, "playtime.db");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);

            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is closed or not initialized.");
        }
        return connection;
    }

    private static void createTables() {
        String playerTable = "CREATE TABLE IF NOT EXISTS player_stats ("
                + "uuid VARCHAR(36) PRIMARY KEY, "
                + "timePlayed BIGINT NOT NULL, "
                + "lastSession BIGINT NOT NULL, "
                + "sessions BIGINT NOT NULL);";

        String serverTable = "CREATE TABLE IF NOT EXISTS server_stats ("
                + "id INTEGER PRIMARY KEY DEFAULT 1, "
                + "totalPlayTime BIGINT NOT NULL, "
                + "uniqueJoins BIGINT NOT NULL, "
                + "totalJoins BIGINT NOT NULL);";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(playerTable);
            stmt.execute(serverTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
