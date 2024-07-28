package github.andredimaz.sentinel.autoclick.tasks;

import github.andredimaz.sentinel.autoclick.Main;
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

        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                LivingEntity target = (LivingEntity) entity;
                if (!plugin.isBlacklisted(target.getType())) {
                    target.damage(itemInHand.getType().getMaxDurability(), player);
                }
            }
        }
    }
}
