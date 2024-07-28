package github.andredimaz.sentinel.autoclick.utils;

import org.bukkit.ChatColor;

public class colorUtils {
    public static String colorize(String string) {
        if (string == null) {
            return ""; // Retorna uma string vazia se a string for nula
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
