package me.reportcardsmc.github.playtime;

import me.reportcardsmc.github.playtime.commands.PlaytimeCommand;
import me.reportcardsmc.github.playtime.events.PlayerConnection;
import me.reportcardsmc.github.playtime.utils.players.PlayTimeUtilities;
import me.reportcardsmc.github.playtime.utils.players.PlayerData;
import me.reportcardsmc.github.playtime.utils.players.PlayerSession;
import me.reportcardsmc.github.playtime.utils.players.PlayerStats;
import me.reportcardsmc.github.playtime.utils.server.ServerData;
import me.reportcardsmc.github.playtime.utils.server.ServerStats;
import me.reportcardsmc.github.playtime.utils.SQLiteDatabase;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
        playerData = new ConcurrentHashMap<>();
        lastPlayTimeUpdate = new ConcurrentHashMap<>();
        sessionStarts = new ConcurrentHashMap<>();

        SQLiteDatabase.initialize(getDataFolder());

        serverStats = ServerData.getServerStats();
        if (serverStats == null) {
            this.getLogger().severe("Could not initialize server stats!");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerSession.startSession(player);
            PlayTimeUtilities.updatePlayTime(player);
        }

        Objects.requireNonNull(this.getCommand("playtime")).setExecutor(new PlaytimeCommand());

        this.getServer().getPluginManager().registerEvents(new PlayerConnection(), this);
        Metrics metrics = new Metrics(this, bstatsID);

        // Synchronous task for updating playtime in memory every 10 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayTimeUtilities.updatePlayTime(player);
                }
            }
        }.runTaskTimer(this, 0, 200);

        // Asynchronous task for saving data to the database every 60 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                ServerData.updateServerFile();
                // We shouldn't use Bukkit.getOnlinePlayers() in an async task, so iterate over the tracked data instead.
                for (UUID uuid : playerData.keySet()) {
                    PlayerData.updatePlayerFile(uuid);
                }
            }
        }.runTaskTimerAsynchronously(this, 0, 1200);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ServerData.updateServerFile();

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData.updatePlayerFile(player.getUniqueId());
            PlayerSession.endSession(player);
        }

        SQLiteDatabase.close();
    }
}
