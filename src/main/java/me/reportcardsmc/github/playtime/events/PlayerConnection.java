package me.reportcardsmc.github.playtime.events;

import me.reportcardsmc.github.playtime.PlayTime;
import me.reportcardsmc.github.playtime.utils.Data;
import me.reportcardsmc.github.playtime.utils.PTUtil;
import me.reportcardsmc.github.playtime.utils.Session;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class PlayerConnection implements Listener {
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        try {
            Session.startSession(event.getPlayer());
        } catch (IOException e) {
            Bukkit.getLogger().warning("Can't start session for: " + event.getPlayer().getName());
        }
        playerLoop(event.getPlayer());
        PTUtil.updatePlayTime(event.getPlayer());
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent event) {
        try {
            Session.endSession(event.getPlayer());
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
                    PTUtil.updatePlayTime(player);
                    i[0] = 0;
                }
                if (i[1] >= 120) {
                    i[1] = 0;
                    PTUtil.updatePlayTime(player);
                    try {
                        Data.updatePlayerFile(player.getUniqueId());
                    } catch (IOException e) {
                        Bukkit.getLogger().warning("Can't save file for: " + player.getName());
                    }
                }
            }
        }.runTaskTimer(PlayTime.instance, 0, 10);
    }
}
