package github.andredimaz.sentinel.autoclick.tasks;

import github.andredimaz.sentinel.autoclick.Main;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoClickerTask extends BukkitRunnable {

    private final Main plugin;
    private final Player player;
    private final double cooldown;

    public AutoClickerTask(Main plugin, Player player, double cooldown) {
        this.plugin = plugin;
        this.player = player;
        this.cooldown = cooldown;
    }

    @Override
    public void run() {
        if (!player.isOnline() || player.isDead()) {
            plugin.stopAutoClicker(player);
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInHand();
        double damage = getItemDamage(itemInHand.getType());

        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                LivingEntity target = (LivingEntity) entity;
                if (!plugin.isBlacklisted(target.getType())) {
                    target.damage(damage, player);
                }
            }
        }
    }

    private double getItemDamage(Material material) {
        switch (material) {
            case WOOD_SWORD:
                return 4;
            case STONE_SWORD:
                return 5;
            case IRON_SWORD:
                return 6;
            case DIAMOND_SWORD:
                return 7;
            case GOLD_SWORD:
                return 4;
            default:
                return 1; // Dano padrão para outros itens
        }
    }
}
