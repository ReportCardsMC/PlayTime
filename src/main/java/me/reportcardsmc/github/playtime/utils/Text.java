package me.reportcardsmc.github.playtime.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text {

    public static Component color(String text) {
        return Component.text(ChatColor.translateAlternateColorCodes('&', text));
    }

    public static String formatComma(long number) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(number);
    }

    public static String dateToString(long mi) {
        Date date = new Date(mi);
        return new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a").format(date);
    }

    public static String msToFormat(long ms) {
        Date date = new Date(ms);
        String format = new SimpleDateFormat("DD HH mm ss").format(date);
        String[] split = format.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if (!Objects.equals(s, "00")) {
                final String regex = "^0";
                final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                final Matcher matcher = pattern.matcher(s);
                final String replaced = matcher.replaceAll("");
                if (i != 0) builder.append(replaced);

                switch (i) {
                    case 0:
                        long days = Long.parseLong(replaced) - 1;
                        if (days >= 1) {
                            builder.append(days);
                            builder.append("d ");
                        }
                        break;
                    case 1:
                        builder.append("h ");
                        break;
                    case 2:
                        builder.append("m ");
                        break;
                    case 3:
                        builder.append("s ");
                        break;
                }
            }
        }
        // The substituted value will be contained in the result variable
        final String regex = "\\s$";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(builder.toString());
        return matcher.replaceAll("");
    }

}
