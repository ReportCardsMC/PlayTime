package me.reportcardsmc.github.playtime.utils.players;

import com.google.gson.Gson;
import me.reportcardsmc.github.playtime.PlayTime;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    public static PlayerStats getPlayerData(UUID uuid) throws IOException {
        Gson gson = new Gson();
        File folder = new File(Paths.get(PlayTime.instance.getDataFolder() + "/players").toString());
        if (!folder.exists()) folder.mkdirs();
        File playerFile = new File(folder, uuid.toString() + ".json");
        if (!playerFile.exists() && Bukkit.getPlayer(uuid) != null) {
            if (!playerFile.createNewFile()) throw new IOException("Can't create player data folder");
            List<String> lines = Arrays.asList(
                    "// CHANGING ANY OF THESE VALUES CAN BREAK SOMETHING, KNOW WHAT YOU'RE DOING BEFORE CHANGING ANYTHING.",
                    "{",
                    "\"playTime\": 0,",
                    "\"lastSession\": 0,",
                    "\"sessions\": 0",
                    "}"
            );
            Files.write(playerFile.toPath(), lines, StandardCharsets.UTF_8);
        } else if (!playerFile.exists() && Bukkit.getPlayer(uuid) == null) {
            return null;
        }
        PlayerStats playerStats = gson.fromJson(Files.newBufferedReader(playerFile.toPath()), PlayerStats.class);
        if (!PlayTime.instance.playerData.containsKey(uuid)) PlayTime.instance.playerData.put(uuid, playerStats);
        return playerStats;
    }

    public static void updatePlayerFile(UUID uuid) throws IOException {
        File folder = new File(Paths.get(PlayTime.instance.getDataFolder() + "/players").toString());
        if (!folder.exists()) folder.mkdirs();
        File playerFile = new File(folder, uuid.toString() + ".json");
        if (!playerFile.exists()) throw new IOException("Player doesn't currently have a file");
        String data = PlayTime.instance.playerData.get(uuid).getJson();
        Files.write(playerFile.toPath(), Arrays.asList(data.split("\n")), StandardCharsets.UTF_8);
    }

}
