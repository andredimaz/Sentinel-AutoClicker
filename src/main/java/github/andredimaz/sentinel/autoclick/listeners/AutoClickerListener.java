package github.andredimaz.sentinel.autoclick.listeners;

import github.andredimaz.sentinel.autoclick.Main;
import github.andredimaz.sentinel.autoclick.utils.GroupManager;
import github.andredimaz.sentinel.autoclick.utils.colorUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class AutoClickerListener implements Listener {

    private final Main plugin;

    public AutoClickerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        String menuTitle = plugin.getConfig().getString("menus.principal.titulo");

        if (inventory.getTitle().equals(menuTitle)) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getItemMeta() == null) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            ConfigurationSection menuConfig = plugin.getConfig().getConfigurationSection("menus.principal");

            if (menuConfig != null) {
                ConfigurationSection alternarConfig = menuConfig.getConfigurationSection("alternar");
                ConfigurationSection melhorarConfig = menuConfig.getConfigurationSection("melhorar");

                if (alternarConfig != null && clickedItem.isSimilar(plugin.menuUtils.createItemFromConfig(alternarConfig, player, "principal", "alternar"))) {
                    boolean isAutoClickerActive = player.hasMetadata("autoclicker_task");
                    if (isAutoClickerActive) {
                        plugin.stopAutoClicker(player);
                    } else {
                        plugin.startAutoClicker(player);
                    }
                    updateMenuStatus(inventory, player);
                } else if (melhorarConfig != null && clickedItem.isSimilar(plugin.menuUtils.createItemFromConfig(melhorarConfig, player, "principal", "melhorar"))) {
                    plugin.menuUtils.openMenu(player, "melhorias");
                }
            }
        }

        menuTitle = plugin.getConfig().getString("menus.melhorias.titulo");
        if (inventory.getTitle().equals(menuTitle)) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getItemMeta() == null) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            ConfigurationSection menuConfig = plugin.getConfig().getConfigurationSection("menus.melhorias");

            if (menuConfig != null) {
                ConfigurationSection cooldownConfig = menuConfig.getConfigurationSection("cooldown");
                ConfigurationSection rangeConfig = menuConfig.getConfigurationSection("range");

                if (cooldownConfig != null && clickedItem.isSimilar(plugin.menuUtils.createItemFromConfig(cooldownConfig, player, "melhorias", "cooldown"))) {
                    upgrade(player, "cooldown");
                } else if (rangeConfig != null && clickedItem.isSimilar(plugin.menuUtils.createItemFromConfig(rangeConfig, player, "melhorias", "range"))) {
                    upgrade(player, "range");
                }
            }
        }
    }

    private void upgrade(Player player, String tipo) {
        int nivelMelhoria = plugin.getNivelMelhoria(player);
        GroupManager group = plugin.getPlayerGroup(player);
        Map<String, Object> custos = tipo.equals("cooldown") ? group.getCooldownCost(nivelMelhoria + 1) : group.getRangeCost(nivelMelhoria + 1);

        if (custos == null) {
            player.sendMessage(colorUtils.colorize("&cVocê já atingiu o nível máximo de melhoria."));
            return;
        }

        for (Map.Entry<String, Object> custo : custos.entrySet()) {
            ConfigurationSection custoConfig = (ConfigurationSection) custo.getValue();
            String tipoCusto = custoConfig.getString("tipo");
            double preco = custoConfig.getDouble("preco");

            if (tipoCusto.equals("money")) {
                if (plugin.getEconomy() == null || !plugin.getEconomy().has(player, preco)) {
                    player.sendMessage(colorUtils.colorize("&cVocê não tem dinheiro suficiente."));
                    return;
                }
            } else if (tipoCusto.equals("xp")) {
                if (player.getLevel() < preco) {
                    player.sendMessage(colorUtils.colorize("&cVocê não tem XP suficiente."));
                    return;
                }
            }
        }

        for (Map.Entry<String, Object> custo : custos.entrySet()) {
            ConfigurationSection custoConfig = (ConfigurationSection) custo.getValue();
            String tipoCusto = custoConfig.getString("tipo");
            double preco = custoConfig.getDouble("preco");

            if (tipoCusto.equals("money")) {
                plugin.getEconomy().withdrawPlayer(player, preco);
            } else if (tipoCusto.equals("xp")) {
                player.setLevel(player.getLevel() - (int) preco);
            }
        }

        plugin.setNivelMelhoria(player, nivelMelhoria + 1);

        player.sendMessage(colorUtils.colorize("&aMelhoria realizada com sucesso!"));
        plugin.menuUtils.openMenu(player, "melhorias");
    }

    private void updateMenuStatus(Inventory inventory, Player player) {
        ConfigurationSection menuConfig = plugin.getConfig().getConfigurationSection("menus.principal");
        if (menuConfig == null) {
            return;
        }

        Set<String> itemKeys = menuConfig.getKeys(false);
        for (String itemKey : itemKeys) {
            if (!itemKey.equals("linhas") && !itemKey.equals("titulo")) {
                ConfigurationSection itemConfig = menuConfig.getConfigurationSection(itemKey);
                if (itemConfig != null) {
                    ItemStack item = plugin.menuUtils.createItemFromConfig(itemConfig, player, "principal", itemKey);
                    int slot = itemConfig.getInt("slot", 0);
                    if (slot >= 0 && slot < inventory.getSize()) {
                        inventory.setItem(slot, item);
                    }
                }
            }
        }
    }
}
