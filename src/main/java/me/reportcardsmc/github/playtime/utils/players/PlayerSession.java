package me.reportcardsmc.github.playtime.utils.players;

import me.reportcardsmc.github.playtime.PlayTime;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.time.Instant;

public class PlayerSession {

    public static long getCurrentSession(Player player) {
        if (!PlayTime.instance.sessionStarts.containsKey(player.getUniqueId())) return -1;
        Long start = PlayTime.instance.sessionStarts.get(player.getUniqueId());
        return Instant.now().toEpochMilli() - start;
    }

    public static boolean startSession(Player player) throws IOException {
        if (PlayTime.instance.sessionStarts.containsKey(player.getUniqueId())) return false;
        PlayTime.instance.sessionStarts.put(player.getUniqueId(), Instant.now().toEpochMilli());
//        Data.getPlayerData(player.getUniqueId());
        return true;
    }

    public static boolean endSession(Player player) throws IOException {
        if (!PlayTime.instance.sessionStarts.containsKey(player.getUniqueId())) return false;
        PlayerStats playerStats = PlayTime.instance.playerData.get(player.getUniqueId());
        long session = Instant.now().toEpochMilli() - PlayTime.instance.sessionStarts.get(player.getUniqueId());
        playerStats.addSession(1);
        playerStats.setLastSession(session);
        PlayTimeUtilities.updatePlayTime(player);
        PlayerData.updatePlayerFile(player.getUniqueId());
//        ServerStats serverStats = PlayTime.instance.serverStats;
//        serverStats.addTotalPlayTime(session);
//        ServerData.updateServerFile();

        PlayTime.instance.sessionStarts.remove(player.getUniqueId());
        PlayTime.instance.playerData.remove(player.getUniqueId());
        PlayTime.instance.lastPlayTimeUpdate.remove(player.getUniqueId());
        return true;
    }

}
