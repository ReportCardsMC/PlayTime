package me.reportcardsmc.github.playtime.utils.players;

import me.reportcardsmc.github.playtime.PlayTime;
import me.reportcardsmc.github.playtime.utils.server.ServerData;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.time.Instant;

public class PlayTimeUtilities {

    public static long updatePlayTime(Player player) {
        long now = Instant.now().toEpochMilli();
        long diff = now - lastPlayTimeUpdate(player);
        try {
            if (!PlayTime.instance.playerData.containsKey(player.getUniqueId()))
                PlayerData.getPlayerData(player.getUniqueId());
        } catch (IOException e) {
            return -1;
        }
        if (!PlayTime.instance.playerData.containsKey(player.getUniqueId())) return -1;
        long current = PlayTime.instance.playerData.get(player.getUniqueId()).timePlayed;
        PlayTime.instance.serverStats.addTotalPlayTime(diff);
        return PlayTime.instance.playerData.get(player.getUniqueId()).setTimePlayed(current + diff);
    }

    public static long lastPlayTimeUpdate(Player player) {
        if (!PlayTime.instance.lastPlayTimeUpdate.containsKey(player.getUniqueId())) {
            PlayTime.instance.lastPlayTimeUpdate.put(player.getUniqueId(), Instant.now().toEpochMilli());
            return PlayTime.instance.lastPlayTimeUpdate.get(player.getUniqueId());
        }
        long lastUpdate = PlayTime.instance.lastPlayTimeUpdate.get(player.getUniqueId());
        PlayTime.instance.lastPlayTimeUpdate.put(player.getUniqueId(), Instant.now().toEpochMilli());
        return lastUpdate;
    }

}
