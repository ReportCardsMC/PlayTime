package me.reportcardsmc.github.playtime;

import me.reportcardsmc.github.playtime.commands.PlaytimeCommand;
import me.reportcardsmc.github.playtime.events.PlayerConnection;
import me.reportcardsmc.github.playtime.utils.Data;
import me.reportcardsmc.github.playtime.utils.PTUtil;
import me.reportcardsmc.github.playtime.utils.PlayerStats;
import me.reportcardsmc.github.playtime.utils.Session;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PlayTime extends JavaPlugin {

    public static PlayTime instance;
    public Map<UUID, PlayerStats> playerData;
    public Map<UUID, Long> lastPlayTimeUpdate;
    public Map<UUID, Long> sessionStarts;

    @Override
    public void onEnable() {
        instance = this;
        playerData = new HashMap<>();
        lastPlayTimeUpdate = new HashMap<>();
        sessionStarts = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                Session.startSession(player);
            } catch (IOException e) {
                e.printStackTrace();
            }
            PTUtil.updatePlayTime(player);
        }

        Objects.requireNonNull(this.getCommand("playtime")).setExecutor(new PlaytimeCommand());

        this.getServer().getPluginManager().registerEvents(new PlayerConnection(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        playerData.forEach((uuid, stats) -> {
            try {
                Data.updatePlayerFile(uuid);
                Session.endSession(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
