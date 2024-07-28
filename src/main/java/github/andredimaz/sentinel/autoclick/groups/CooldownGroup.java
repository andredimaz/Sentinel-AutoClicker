package github.andredimaz.sentinel.autoclick.groups;

import org.bukkit.entity.Player;

public class CooldownGroup {
    private final int order;
    private final double cooldown;
    private final String permission;

    public CooldownGroup(int order, double cooldown, String permission) {
        this.order = order;
        this.cooldown = cooldown;
        this.permission = permission;
    }

    public int getOrder() {
        return order;
    }

    public double getCooldown() {
        return cooldown;
    }

    public boolean hasPermission(Player player) {
        return permission.isEmpty() || player.hasPermission(permission);
    }
}