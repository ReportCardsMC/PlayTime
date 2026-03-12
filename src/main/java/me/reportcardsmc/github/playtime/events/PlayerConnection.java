package me.reportcardsmc.github.playtime.events;

import me.reportcardsmc.github.playtime.PlayTime;
import me.reportcardsmc.github.playtime.utils.players.PlayTimeUtilities;
import me.reportcardsmc.github.playtime.utils.players.PlayerData;
import me.reportcardsmc.github.playtime.utils.players.PlayerSession;
import me.reportcardsmc.github.playtime.utils.server.ServerData;
import me.reportcardsmc.github.playtime.utils.SQLiteDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerConnection implements Listener {
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        PlayTime.instance.serverStats.addTotalJoin();

        // Check if player is unique by querying SQLite
        String uuid = event.getPlayer().getUniqueId().toString();

        Bukkit.getScheduler().runTaskAsynchronously(PlayTime.instance, () -> {
            boolean unique = true;
            try (PreparedStatement pstmt = SQLiteDatabase.getConnection().prepareStatement("SELECT uuid FROM player_stats WHERE uuid = ?")) {
                pstmt.setString(1, uuid);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        unique = false;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (unique) {
                PlayTime.instance.serverStats.addUniqueJoin();
            }

            ServerData.updateServerFile();

            // Back to main thread for non-thread-safe state
            Bukkit.getScheduler().runTask(PlayTime.instance, () -> {
                PlayerSession.startSession(event.getPlayer());
                PlayTimeUtilities.updatePlayTime(event.getPlayer());
            });
        });
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent event) {
        PlayerSession.endSession(event.getPlayer());
        Bukkit.getScheduler().runTaskAsynchronously(PlayTime.instance, () -> {
            ServerData.updateServerFile();
        });
    }
}
