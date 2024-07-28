package github.andredimaz.sentinel.autoclick.utils;

import org.bukkit.entity.Player;

public class GroupManager {
    private final int order;
    private final double cooldown;
    private final int range;
    private final String permission;

    public GroupManager(int order, double cooldown, int range, String permission) {
        this.order = order;
        this.cooldown = cooldown;
        this.range = range;
        this.permission = permission;
    }

    public int getOrder() {
        return order;
    }

    public double getCooldown() {
        return cooldown;
    }

    public int getRange() {
        return range;
    }

    public boolean hasPermission(Player player) {
        return permission.isEmpty() || player.hasPermission(permission);
    }
}