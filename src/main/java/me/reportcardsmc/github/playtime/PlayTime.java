package me.reportcardsmc.github.playtime;

import me.reportcardsmc.github.playtime.commands.PlaytimeCommand;
import me.reportcardsmc.github.playtime.events.PlayerConnection;
import me.reportcardsmc.github.playtime.utils.players.PlayTimeUtilities;
import me.reportcardsmc.github.playtime.utils.players.PlayerData;
import me.reportcardsmc.github.playtime.utils.players.PlayerSession;
import me.reportcardsmc.github.playtime.utils.players.PlayerStats;
import me.reportcardsmc.github.playtime.utils.server.ServerData;
import me.reportcardsmc.github.playtime.utils.server.ServerStats;
import org.bstats.bukkit.Metrics;
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
    public ServerStats serverStats;
    int bstatsID = 12542;

    @Override
    public void onEnable() {
        instance = this;
        playerData = new HashMap<>();
        lastPlayTimeUpdate = new HashMap<>();
        sessionStarts = new HashMap<>();
        try {
            serverStats = ServerData.getServerStats();
        } catch (IOException e) {
            e.printStackTrace();
            this.getPluginLoader().disablePlugin(this);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                PlayerSession.startSession(player);
            } catch (IOException e) {
                e.printStackTrace();
            }
            PlayTimeUtilities.updatePlayTime(player);
        }

        Objects.requireNonNull(this.getCommand("playtime")).setExecutor(new PlaytimeCommand());

        this.getServer().getPluginManager().registerEvents(new PlayerConnection(), this);
        Metrics metrics = new Metrics(this, bstatsID);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            ServerData.updateServerFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        playerData.forEach((uuid, stats) -> {
//            try {
//                PlayerData.updatePlayerFile(uuid);
//                PlayerSession.endSession(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
    }
}
