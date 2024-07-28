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
    private final int range;

    public AutoClickerTask(Main plugin, Player player, double cooldown, int range) {
        this.plugin = plugin;
        this.player = player;
        this.cooldown = cooldown;
        this.range = range;
    }

    @Override
    public void run() {
        if (!player.isOnline() || player.isDead()) {
            plugin.stopAutoClicker(player);
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInHand();

        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                LivingEntity target = (LivingEntity) entity;
                if (!plugin.isBlacklisted(target.getType())) {
                    target.damage(itemInHand.getType().getMaxDurability(), player);
                }
            }
        }
    }
}
