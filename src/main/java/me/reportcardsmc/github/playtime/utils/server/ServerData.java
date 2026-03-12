package me.reportcardsmc.github.playtime.utils.server;

import me.reportcardsmc.github.playtime.PlayTime;
import me.reportcardsmc.github.playtime.utils.SQLiteDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerData {

    public static ServerStats getServerStats() {
        ServerStats serverStats = null;
        String query = "SELECT totalPlayTime, uniqueJoins, totalJoins FROM server_stats WHERE id = 1";

        try (PreparedStatement pstmt = SQLiteDatabase.getConnection().prepareStatement(query)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long totalPlayTime = rs.getLong("totalPlayTime");
                    long uniqueJoins = rs.getLong("uniqueJoins");
                    long totalJoins = rs.getLong("totalJoins");
                    serverStats = new ServerStats(totalPlayTime, totalJoins, uniqueJoins);
                } else {
                    serverStats = new ServerStats(0, 0, 0);
                    insertNewServerStats(serverStats);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return serverStats;
    }

    private static void insertNewServerStats(ServerStats stats) {
        String insert = "INSERT INTO server_stats(id, totalPlayTime, uniqueJoins, totalJoins) VALUES(1, ?, ?, ?)";
        try (PreparedStatement pstmt = SQLiteDatabase.getConnection().prepareStatement(insert)) {
            pstmt.setLong(1, stats.getTotalPlayTime());
            pstmt.setLong(2, stats.getUniqueJoins());
            pstmt.setLong(3, stats.getTotalJoins());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateServerFile() {
        if (PlayTime.instance.serverStats == null) return;
        ServerStats stats = PlayTime.instance.serverStats;
        String update = "UPDATE server_stats SET totalPlayTime = ?, uniqueJoins = ?, totalJoins = ? WHERE id = 1";

        try (PreparedStatement pstmt = SQLiteDatabase.getConnection().prepareStatement(update)) {
            pstmt.setLong(1, stats.getTotalPlayTime());
            pstmt.setLong(2, stats.getUniqueJoins());
            pstmt.setLong(3, stats.getTotalJoins());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                 insertNewServerStats(stats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
