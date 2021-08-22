package me.reportcardsmc.github.playtime.utils.server;

import com.google.gson.Gson;
import me.reportcardsmc.github.playtime.PlayTime;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ServerData {

    public static ServerStats getServerStats() throws IOException {
        Gson gson = new Gson();
        File dataFolder = PlayTime.instance.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();
        File serverData = new File(Paths.get(dataFolder + "/server.json").toString());
        if (!serverData.exists()) {
            if (!serverData.createNewFile()) throw new IOException("Couldn't create server file");
            List<String> lines = Arrays.asList(
                    "// CHANGING ANY OF THESE VALUES CAN BREAK SOMETHING, KNOW WHAT YOU'RE DOING BEFORE CHANGING ANYTHING.",
                    "{",
                    "\"totalPlayTime\": 0,",
                    "\"totalJoins\": 0,",
                    "\"uniqueJoins\": 0",
                    "}"
            );
            Files.write(serverData.toPath(), lines, StandardCharsets.UTF_8);
        }
        return gson.fromJson(Files.newBufferedReader(serverData.toPath()), ServerStats.class);
    }

    public static void updateServerFile() throws IOException {
        File dataFolder = PlayTime.instance.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();
        File serverData = new File(Paths.get(dataFolder + "/server.json").toString());
        if (!serverData.exists()) throw new IOException("Server Data File Doesn't Exist");
        Files.write(serverData.toPath(), Arrays.asList(PlayTime.instance.serverStats.getJson().split("\n")), StandardCharsets.UTF_8);
    }

}
