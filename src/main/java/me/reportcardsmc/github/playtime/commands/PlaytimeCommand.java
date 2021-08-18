package me.reportcardsmc.github.playtime.commands;

import me.reportcardsmc.github.playtime.PlayTime;
import me.reportcardsmc.github.playtime.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class PlaytimeCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Text.color("&cYou input no arguments, this is a player only feature... Try /playtime help"));
                return true;
            }
            Player player = ((Player) sender).getPlayer();
            assert player != null;
            UUID uuid = player.getUniqueId();
            if (!PlayTime.instance.playerData.containsKey(uuid)) {
                sender.sendMessage(Text.color("&cThere was an error running the command..."));
                return true;
            }
            sendStats(player);
            return true;
        }
        if (args.length == 1) {
            Player online = Bukkit.getPlayer(args[0]);
            OfflinePlayer viewing = Bukkit.getOfflinePlayerIfCached(args[0]);
            if (viewing == null) viewing = Bukkit.getOfflinePlayer(args[0]);
            UUID uuid = viewing.getUniqueId();
            if (online != null && !PlayTime.instance.playerData.containsKey(uuid)) {
                sender.sendMessage(Text.color("&cThere was an error running the command..."));
                return true;
            }
            if (online != null) sendStats(sender, online);
            if (online == null) sendStats(sender, viewing);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    private void sendStats(Player player) {
        sendStats(player, player);
    }

    private void sendStats(CommandSender viewer, OfflinePlayer player) {
        if (player.isOnline()) {
            Player p = Bukkit.getPlayer(player.getUniqueId());
            sendStats(viewer, p);
        }
        UUID uuid = player.getUniqueId();
        PlayerStats data = null;
        try {
            data = Data.getPlayerData(uuid);
        } catch (IOException ignored) {
        }
        if (data == null) viewer.sendMessage(Text.color("&cThis player has no stats"));
        else {

            long secondsPlayed = data.getTimePlayed();
            long firstJoin = player.getFirstPlayed();
            long lastSession = data.getLastSession();
            long averageSession = data.averageSessionOffline();

            viewer.sendMessage(Text.color("&6&l » &ePlaytime Stats &7(" + player.getName() + "&7)"));
            viewer.sendMessage(Text.color("&6"));
            viewer.sendMessage(Text.color("&6&lGENERAL"));
            viewer.sendMessage(Text.color("&6 »&e First Join: &f" + Text.dateToString(firstJoin) + " &7(UTC)"));
            viewer.sendMessage(Text.color("&6 »&e Time Played: &f" + Text.msToFormat(secondsPlayed)));
            viewer.sendMessage(Text.color("&6"));
            viewer.sendMessage(Text.color("&6&lSESSION"));
            viewer.sendMessage(Text.color("&6 »&e Last Session: &f" + Text.msToFormat(lastSession)));
            viewer.sendMessage(Text.color("&6 »&e Average Session: &f" + Text.msToFormat(averageSession)));
            viewer.sendMessage(Text.color("&6"));
        }

    }

    private void sendStats(CommandSender viewer, Player player) {
        long secondsPlayed = PTUtil.updatePlayTime(player);
        long firstJoin = player.getFirstPlayed();
        long curSession = Session.getCurrentSession(player);
        long lastSession = PlayTime.instance.playerData.get(player.getUniqueId()).getLastSession();
        long averageSession = PlayTime.instance.playerData.get(player.getUniqueId()).averageSession();

        viewer.sendMessage(Text.color("&6&l » &ePlaytime Stats &7(" + player.getName() + "&7)"));
        viewer.sendMessage(Text.color("&6"));
        viewer.sendMessage(Text.color("&6&lGENERAL"));
        viewer.sendMessage(Text.color("&6 »&e First Join: &f" + Text.dateToString(firstJoin) + " &7(UTC)"));
        viewer.sendMessage(Text.color("&6 »&e Time Played: &f" + Text.msToFormat(secondsPlayed)));
        viewer.sendMessage(Text.color("&6"));
        viewer.sendMessage(Text.color("&6&lSESSION"));
        viewer.sendMessage(Text.color("&6 »&e Current Session: &f" + Text.msToFormat(curSession)));
        viewer.sendMessage(Text.color("&6 »&e Last Session: &f" + Text.msToFormat(lastSession)));
        viewer.sendMessage(Text.color("&6 »&e Average Session: &f" + Text.msToFormat(averageSession)));
    }
}
