package me.reportcardsmc.github.playtime.utils.players;

import me.reportcardsmc.github.playtime.PlayTime;
import me.reportcardsmc.github.playtime.utils.SQLiteDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerData {

    public static PlayerStats getPlayerData(UUID uuid) {
        PlayerStats playerStats = null;
        String query = "SELECT timePlayed, lastSession, sessions FROM player_stats WHERE uuid = ?";

        try (PreparedStatement pstmt = SQLiteDatabase.getConnection().prepareStatement(query)) {

            pstmt.setString(1, uuid.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long timePlayed = rs.getLong("timePlayed");
                    long lastSession = rs.getLong("lastSession");
                    long sessions = rs.getLong("sessions");
                    playerStats = new PlayerStats(timePlayed, lastSession, sessions, 0); // Assuming 0 for totalSessionTime as it's not saved
                } else if (org.bukkit.Bukkit.getPlayer(uuid) != null) {
                    // If player is online but doesn't exist in DB, create new stats
                    playerStats = new PlayerStats(0, 0, 0, 0);
                    insertNewPlayer(uuid, playerStats);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (playerStats != null) {
            PlayTime.instance.playerData.putIfAbsent(uuid, playerStats);
        }

        return playerStats;
    }

    private static void insertNewPlayer(UUID uuid, PlayerStats stats) {
        String insert = "INSERT INTO player_stats(uuid, timePlayed, lastSession, sessions) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = SQLiteDatabase.getConnection().prepareStatement(insert)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setLong(2, stats.getTimePlayed());
            pstmt.setLong(3, stats.getLastSession());
            pstmt.setLong(4, stats.getSessions());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayerFile(UUID uuid) {
        if (!PlayTime.instance.playerData.containsKey(uuid)) return;
        PlayerStats stats = PlayTime.instance.playerData.get(uuid);
        String update = "UPDATE player_stats SET timePlayed = ?, lastSession = ?, sessions = ? WHERE uuid = ?";

        try (PreparedStatement pstmt = SQLiteDatabase.getConnection().prepareStatement(update)) {
            pstmt.setLong(1, stats.getTimePlayed());
            pstmt.setLong(2, stats.getLastSession());
            pstmt.setLong(3, stats.getSessions());
            pstmt.setString(4, uuid.toString());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                 insertNewPlayer(uuid, stats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
