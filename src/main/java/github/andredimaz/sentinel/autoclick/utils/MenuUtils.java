package github.andredimaz.sentinel.autoclick.utils;


import github.andredimaz.sentinel.autoclick.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MenuUtils {

    private final Main plugin;

    public MenuUtils(Main plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player) {
        ConfigurationSection menuConfig = plugin.getConfig().getConfigurationSection("menu");
        if (menuConfig == null) {
            player.sendMessage(ChatColor.RED + "Erro: Configuração do menu não encontrada.");
            return;
        }

        int slots = menuConfig.getInt("linhas") * 9;
        String title = colorUtils.colorize(menuConfig.getString("titulo", "Menu da Espada"));
        Inventory menu = Bukkit.createInventory(null, slots, title);

        Set<String> itemKeys = menuConfig.getKeys(false);
        for (String itemKey : itemKeys) {
            if (!itemKey.equals("linhas") && !itemKey.equals("titulo")) {
                ConfigurationSection itemConfig = menuConfig.getConfigurationSection(itemKey);
                if (itemConfig != null) {
                    ItemStack item = createItemFromConfig(itemConfig, player);
                    int slot = itemConfig.getInt("slot", 0);
                    if (slot >= 0 && slot < slots) {
                        menu.setItem(slot, item);
                    } else {
                        plugin.getLogger().warning("Slot inválido para item " + itemKey + ": " + slot);
                    }
                }
            }
        }

        player.openInventory(menu);
    }

    public ItemStack createItemFromConfig(ConfigurationSection itemConfig, Player player) {
        ItemStack item;
        String materialString = itemConfig.getString("material");

        if (materialString.equals("{player}")) {
            item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            skullMeta.setOwner(player.getName());
            item.setItemMeta(skullMeta);
        } else {
            item = materialUtils.parseMaterial(materialString);
        }

        ItemMeta meta = item.getItemMeta();
        boolean isAutoClickerActive = player.hasMetadata("autoclicker_task");
        String status = isAutoClickerActive
                ? plugin.getConfig().getString("status.desativar", "&cDesativar")
                : plugin.getConfig().getString("status.ativar", "&aAtivar");
        double cooldown = plugin.getPlayerCooldownGroup(player).getCooldown();
        String formattedCooldown = formatCooldown(cooldown);

        String displayName = itemConfig.getString("nome", "").replace("{status}", status).replace("{cooldown}", formattedCooldown);
        meta.setDisplayName(colorUtils.colorize(displayName));

        List<String> lore = itemConfig.getStringList("lore").stream()
                .map(line -> line.replace("{status}", status).replace("{cooldown}", formattedCooldown))
                .map(colorUtils::colorize)
                .collect(Collectors.toList());
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); // Esconder atributos
        item.setItemMeta(meta);

        return item;
    }

    private String formatCooldown(double cooldown) {
        if (cooldown == (long) cooldown) {
            return String.format("%d", (long) cooldown);
        } else {
            return new DecimalFormat("#.##").format(cooldown);
        }
    }
}