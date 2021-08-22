package me.reportcardsmc.github.playtime.commands;

import me.reportcardsmc.github.playtime.PlayTime;
import me.reportcardsmc.github.playtime.utils.Text;
import me.reportcardsmc.github.playtime.utils.players.PlayTimeUtilities;
import me.reportcardsmc.github.playtime.utils.players.PlayerData;
import me.reportcardsmc.github.playtime.utils.players.PlayerSession;
import me.reportcardsmc.github.playtime.utils.players.PlayerStats;
import me.reportcardsmc.github.playtime.utils.server.ServerStats;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
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
            switch (args[0]) {
                case "help":
                    sender.sendMessage("&6 &l|&e /playtime Usage:");
                    sender.sendMessage("&e/playtime &6- &eView your own playtime stats");
                    sender.sendMessage("&e/playtime <player> &6- &eView someone elses playtime stats");
                    if (sender.hasPermission("playtime.server")) sender.sendMessage("&e/playtime server &6- &eView overall server playtime stats");
                    break;
                case "server":
                    if (!sender.hasPermission("playtime.server")) {
                        sender.sendMessage("&cInvalid Permissions.");
                        break;
                    }
                    sendServerStats(sender);
                    break;
                default:
                    Player online = Bukkit.getPlayer(args[0]);
                    if (sender instanceof  Player) {
                        Player player = (Player) (Player) sender;
                        if (online != null && online == player && !player.hasPermission("playtime.others")) {
                            player.sendMessage("&cInvalid Permissions!");
                            break;
                        }
                    }

                    OfflinePlayer viewing = Bukkit.getOfflinePlayerIfCached(args[0]);
                    if (viewing == null) viewing = Bukkit.getOfflinePlayer(args[0]);
                    UUID uuid = viewing.getUniqueId();
                    if (online != null && !PlayTime.instance.playerData.containsKey(uuid)) {
                        sender.sendMessage(Text.color("&cThere was an error running the command..."));
                        return true;
                    }
                    if (online != null) sendStats(sender, online);
                    if (online == null) sendStats(sender, viewing);
                    break;
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    private void sendServerStats(CommandSender sender) {
        ServerStats serverStats = PlayTime.instance.serverStats;
        long totalTime = serverStats.getTotalPlayTime();
        long averageSession = serverStats.getAverageSession();
        long totalJoins = serverStats.getTotalJoins();
        long uniqueJoins = serverStats.getUniqueJoins();

        sender.sendMessage(Text.color("&6&l » &eServer Playtime Stats"));
        sender.sendMessage(Text.color("&6&lGENERAL"));
        sender.sendMessage(Text.color("&6 »&e Total Play Time: &f" + Text.msToFormat(totalTime)));
        sender.sendMessage(Text.color("&6 »&e Average Session: &f" + Text.msToFormat(averageSession)));
        sender.sendMessage(Text.color("&6"));
        sender.sendMessage(Text.color("&6&lJOINS"));
        sender.sendMessage(Text.color("&6 »&e Total Joins: &f" + Text.formatComma(totalJoins)));
        sender.sendMessage(Text.color("&6 »&e Unique Joins: &f" + Text.formatComma(uniqueJoins)));
        sender.sendMessage(Text.color("&6 »&e Avg. Joins a Player: &f" + Text.formatComma(Math.floorDiv(totalJoins, uniqueJoins))));
        sender.sendMessage(Text.color("&6"));
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
            data = PlayerData.getPlayerData(uuid);
        } catch (IOException ignored) {
        }
        if (data == null) viewer.sendMessage(Text.color("&cThis player has no stats"));
        else {

            long secondsPlayed = data.getTimePlayed();
            long firstJoin = player.getFirstPlayed();
            long lastSession = data.getLastSession();
            long averageSession = data.averageSessionOffline();
            long sessions = data.getSessions();

            viewer.sendMessage(Text.color("&6&l » &ePlaytime Stats &7(" + player.getName() + "&7)"));
            viewer.sendMessage(Text.color("&6&lGENERAL"));
            viewer.sendMessage(Text.color("&6 »&e First Join: &f" + Text.dateToString(firstJoin) + " &7(UTC)"));
            viewer.sendMessage(Text.color("&6 »&e Total Joins: &f" + Text.formatComma(sessions)));
            viewer.sendMessage(Text.color("&6 »&e Time Played: &f" + Text.msToFormat(secondsPlayed)));
            viewer.sendMessage(Text.color("&6"));
            viewer.sendMessage(Text.color("&6&lSESSION"));
            viewer.sendMessage(Text.color("&6 »&e Last Session: &f" + Text.msToFormat(lastSession)));
            viewer.sendMessage(Text.color("&6 »&e Average Session: &f" + Text.msToFormat(averageSession)));
            viewer.sendMessage(Text.color("&6"));
        }

    }

    private void sendStats(CommandSender viewer, Player player) {
        long secondsPlayed = PlayTimeUtilities.updatePlayTime(player);
        long firstJoin = player.getFirstPlayed();
        long curSession = PlayerSession.getCurrentSession(player);
        long lastSession = PlayTime.instance.playerData.get(player.getUniqueId()).getLastSession();
        long averageSession = PlayTime.instance.playerData.get(player.getUniqueId()).averageSession();
        long sessions = PlayTime.instance.playerData.get(player.getUniqueId()).getSessions();

        viewer.sendMessage(Text.color("&6&l » &ePlaytime Stats &7(" + player.getName() + "&7)"));
        viewer.sendMessage(Text.color("&6&lGENERAL"));
        viewer.sendMessage(Text.color("&6 »&e First Join: &f" + Text.dateToString(firstJoin) + " &7(UTC)"));
        viewer.sendMessage(Text.color("&6 »&e Total Joins: &f" + Text.formatComma(sessions)));
        viewer.sendMessage(Text.color("&6 »&e Time Played: &f" + Text.msToFormat(secondsPlayed)));
        viewer.sendMessage(Text.color("&6"));
        viewer.sendMessage(Text.color("&6&lSESSION"));
        viewer.sendMessage(Text.color("&6 »&e Current Session: &f" + Text.msToFormat(curSession)));
        viewer.sendMessage(Text.color("&6 »&e Last Session: &f" + Text.msToFormat(lastSession)));
        viewer.sendMessage(Text.color("&6 »&e Average Session: &f" + Text.msToFormat(averageSession)));
    }
}
