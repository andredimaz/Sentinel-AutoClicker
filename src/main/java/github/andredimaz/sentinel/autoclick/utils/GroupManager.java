package github.andredimaz.sentinel.autoclick.utils;

import org.bukkit.entity.Player;

import java.util.Map;

public class GroupManager {
    private final int order;
    private final String permission;
    private final Map<Integer, Double> cooldownLevels;
    private final Map<Integer, Integer> rangeLevels;
    private final Map<Integer, Map<String, Object>> cooldownCosts;
    private final Map<Integer, Map<String, Object>> rangeCosts;

    public GroupManager(int order, String permission,
                        Map<Integer, Double> cooldownLevels, Map<Integer, Integer> rangeLevels,
                        Map<Integer, Map<String, Object>> cooldownCosts, Map<Integer, Map<String, Object>> rangeCosts) {
        this.order = order;
        this.permission = permission;
        this.cooldownLevels = cooldownLevels;
        this.rangeLevels = rangeLevels;
        this.cooldownCosts = cooldownCosts;
        this.rangeCosts = rangeCosts;
    }

    public int getOrder() {
        return order;
    }

    public boolean hasPermission(Player player) {
        return permission.isEmpty() || player.hasPermission(permission);
    }

    public double getCooldown(int level) {
        return cooldownLevels.getOrDefault(level, cooldownLevels.get(1)); // Pega o nível 1 se o nível solicitado não existir
    }

    public int getRange(int level) {
        return rangeLevels.getOrDefault(level, rangeLevels.get(1)); // Pega o nível 1 se o nível solicitado não existir
    }

    public Map<String, Object> getCooldownCost(int level) {
        return cooldownCosts.get(level);
    }

    public Map<String, Object> getRangeCost(int level) {
        return rangeCosts.get(level);
    }


    public boolean hasNextCooldownLevel(int level) {
        return cooldownLevels.containsKey(level + 1);
    }

    public boolean hasNextRangeLevel(int level) {
        return rangeLevels.containsKey(level + 1);
    }

    public Map<Integer, Double> getCooldownLevels() {
        return cooldownLevels;
    }

    public Map<Integer, Integer> getRangeLevels() {
        return rangeLevels;
    }


}
