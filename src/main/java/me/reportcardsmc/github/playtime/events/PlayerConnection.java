package me.reportcardsmc.github.playtime.events;

import me.reportcardsmc.github.playtime.PlayTime;
import me.reportcardsmc.github.playtime.utils.players.PlayerData;
import me.reportcardsmc.github.playtime.utils.players.PlayTimeUtilities;
import me.reportcardsmc.github.playtime.utils.players.PlayerSession;
import me.reportcardsmc.github.playtime.utils.server.ServerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

public class PlayerConnection implements Listener {
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        PlayTime.instance.serverStats.addTotalJoin();
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!new File(Paths.get(PlayTime.instance.getDataFolder() + "/players/" + uuid + ".json").toString()).exists()) PlayTime.instance.serverStats.addUniqueJoin();
        try {
            ServerData.updateServerFile();
        } catch (IOException e) {
            Bukkit.getLogger().warning("Can't save server data to file");
        }
        try {
            PlayerSession.startSession(event.getPlayer());
        } catch (IOException e) {
            Bukkit.getLogger().warning("Can't start session for: " + event.getPlayer().getName());
        }
        playerLoop(event.getPlayer());
        PlayTimeUtilities.updatePlayTime(event.getPlayer());
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent event) {
        try {
            PlayerSession.endSession(event.getPlayer());
            try {
                ServerData.updateServerFile();
            } catch (IOException ignored) {}
        } catch (IOException e) {
            Bukkit.getLogger().warning("Can't save data file for: " + event.getPlayer().getName());
        }
    }

    private void playerLoop(Player player) {
        final int[] i = {0, 0};
        new BukkitRunnable() {
            @Override
            public void run() {
                i[0] = i[0] + 1;
                i[1] = i[1] + 1;
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }
                if (i[0] >= 20) {
                    PlayTimeUtilities.updatePlayTime(player);
                    i[0] = 0;
                }
                if (i[1] >= 120) {
                    i[1] = 0;
                    PlayTimeUtilities.updatePlayTime(player);
                    try {
                        PlayerData.updatePlayerFile(player.getUniqueId());
                    } catch (IOException e) {
                        Bukkit.getLogger().warning("Can't save file for: " + player.getName());
                    }
                }
            }
        }.runTaskTimer(PlayTime.instance, 0, 10);
    }
}
