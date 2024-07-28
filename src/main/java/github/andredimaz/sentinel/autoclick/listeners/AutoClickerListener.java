package github.andredimaz.sentinel.autoclick.listeners;

import github.andredimaz.sentinel.autoclick.Main;
import github.andredimaz.sentinel.autoclick.utils.colorUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class AutoClickerListener implements Listener {

    private final Main plugin;

    public AutoClickerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        String menuTitle = plugin.getConfig().getString("menu.titulo");

        if (!inventory.getTitle().equals(menuTitle)) {
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getItemMeta() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ConfigurationSection menuConfig = plugin.getConfig().getConfigurationSection("menu.alternar");

        if (menuConfig != null && clickedItem.isSimilar(plugin.menuUtils.createItemFromConfig(menuConfig, player))) {
            boolean isAutoClickerActive = player.hasMetadata("autoclicker_task");
            if (isAutoClickerActive) {
                plugin.stopAutoClicker(player);
            } else {
                plugin.startAutoClicker(player);
            }
            updateMenuStatus(inventory, player);
        }
    }

    private void updateMenuStatus(Inventory inventory, Player player) {
        ConfigurationSection menuConfig = plugin.getConfig().getConfigurationSection("menu");
        if (menuConfig == null) {
            return;
        }

        Set<String> itemKeys = menuConfig.getKeys(false);
        for (String itemKey : itemKeys) {
            if (!itemKey.equals("linhas") && !itemKey.equals("titulo")) {
                ConfigurationSection itemConfig = menuConfig.getConfigurationSection(itemKey);
                if (itemConfig != null) {
                    ItemStack item = plugin.menuUtils.createItemFromConfig(itemConfig, player);
                    int slot = itemConfig.getInt("slot", 0);
                    if (slot >= 0 && slot < inventory.getSize()) {
                        inventory.setItem(slot, item);
                    }
                }
            }
        }
    }
}