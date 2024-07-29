package github.andredimaz.sentinel.autoclick.utils;

import github.andredimaz.sentinel.autoclick.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MenuUtils implements Listener {

    private final Main plugin;

    public MenuUtils(Main plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player, String menuName) {
        ConfigurationSection menuConfig = plugin.getConfig().getConfigurationSection("menus." + menuName);
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
                    ItemStack item = createItemFromConfig(itemConfig, player, menuName, itemKey);
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

    public ItemStack createItemFromConfig(ConfigurationSection itemConfig, Player player, String menuName, String itemKey) {
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
        int nivelMelhoria = plugin.getNivelMelhoria(player);
        GroupManager groupManager = plugin.getPlayerGroup(player);
        double cooldown = groupManager.getCooldown(nivelMelhoria);
        int range = groupManager.getRange(nivelMelhoria);
        String formattedCooldown = formatCooldown(cooldown);

        boolean nextCooldownExists = groupManager.hasNextCooldownLevel(nivelMelhoria);
        boolean nextRangeExists = groupManager.hasNextRangeLevel(nivelMelhoria);

        double nextCooldown = nextCooldownExists ? groupManager.getCooldown(nivelMelhoria + 1) : cooldown;
        int nextRange = nextRangeExists ? groupManager.getRange(nivelMelhoria + 1) : range;

        String cooldownNivel;
        String rangeNivel;

        ConfigurationSection loresConfig = plugin.getConfig().getConfigurationSection("lores");
        if (loresConfig == null) {
            plugin.getLogger().warning("Lores configuration section not found");
            return item;
        }

        if (!nextCooldownExists) {
            cooldownNivel = loresConfig.getString("nivel.cooldown.maximo", "&7{cooldown}s")
                    .replace("{cooldown}", formattedCooldown);
        } else {
            cooldownNivel = loresConfig.getString("nivel.cooldown.melhorar", "&7{cooldown}s &f-> &7{cooldown_next}s")
                    .replace("{cooldown}", formattedCooldown)
                    .replace("{cooldown_next}", formatCooldown(nextCooldown));
        }

        if (!nextRangeExists) {
            rangeNivel = loresConfig.getString("nivel.range.maximo", "&7{range}")
                    .replace("{range}", String.valueOf(range));
        } else {
            rangeNivel = loresConfig.getString("nivel.range.melhorar", "&7{range} &f-> &7{range_next}")
                    .replace("{range}", String.valueOf(range))
                    .replace("{range_next}", String.valueOf(nextRange));
        }

        String cooldownStatusLore = loresConfig.getString("status-lore.pode-evoluir", "&fClique para melhorar.");
        String rangeStatusLore = loresConfig.getString("status-lore.pode-evoluir", "&fClique para melhorar.");

        double playerBalance = plugin.getEconomy().getBalance(player);
        double upgradeCostMoney = 0;
        int upgradeCostXP = 0;

        Map<String, Object> costs = itemKey.equals("cooldown") ? groupManager.getCooldownCost(nivelMelhoria + 1) : groupManager.getRangeCost(nivelMelhoria + 1);
        if (costs != null) {
            for (Map.Entry<String, Object> cost : costs.entrySet()) {
                if (cost.getValue() instanceof ConfigurationSection) {
                    ConfigurationSection costConfig = (ConfigurationSection) cost.getValue();
                    String costType = costConfig.getString("tipo");
                    double price = costConfig.getDouble("preco");

                    if (costType.equals("money")) {
                        upgradeCostMoney = price;
                    } else if (costType.equals("xp")) {
                        upgradeCostXP = (int) price;
                    }
                }
            }
        }

        if (!nextCooldownExists && itemKey.equals("cooldown")) {
            cooldownStatusLore = loresConfig.getString("status-lore.nivel-maximo", "&cNivel máximo atingido");
        } else if (!nextRangeExists && itemKey.equals("range")) {
            rangeStatusLore = loresConfig.getString("status-lore.nivel-maximo", "&cNivel máximo atingido");
        } else if ((playerBalance < upgradeCostMoney || player.getLevel() < upgradeCostXP) && itemKey.equals("cooldown")) {
            cooldownStatusLore = loresConfig.getString("status-lore.sem-saldo", "&cSaldo insuficiente");
        } else if ((playerBalance < upgradeCostMoney || player.getLevel() < upgradeCostXP) && itemKey.equals("range")) {
            rangeStatusLore = loresConfig.getString("status-lore.sem-saldo", "&cSaldo insuficiente");
        }

        List<String> costsLore = generateCostsLore(upgradeCostMoney, upgradeCostXP);

        String displayName = itemConfig.getString("nome", "")
                .replace("{status}", status)
                .replace("{cooldown}", formattedCooldown)
                .replace("{range}", String.valueOf(range))
                .replace("{cooldown_nivel}", cooldownNivel)
                .replace("{range_nivel}", rangeNivel);
        meta.setDisplayName(colorUtils.colorize(displayName));

        final String finalCooldownStatusLore = cooldownStatusLore;
        final String finalRangeStatusLore = rangeStatusLore;

        List<String> lore = itemConfig.getStringList("lore").stream()
                .flatMap(line -> {
                    if (line.equals("{custos}")) {
                        return costsLore.stream();
                    } else {
                        return Stream.of(line
                                .replace("{status}", status)
                                .replace("{cooldown}", formattedCooldown)
                                .replace("{range}", String.valueOf(range))
                                .replace("{cooldown_nivel}", cooldownNivel)
                                .replace("{range_nivel}", rangeNivel)
                                .replace("{status-lore-cooldown}", finalCooldownStatusLore)
                                .replace("{status-lore-range}", finalRangeStatusLore));
                    }
                })
                .map(colorUtils::colorize)
                .collect(Collectors.toList());
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }

    private String formatValue(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        } else {
            return String.format("%s", value);
        }
    }

    private List<String> generateCostsLore(double money, int xp) {
        ConfigurationSection custosLoreConfig = plugin.getConfig().getConfigurationSection("custos-lore");
        if (custosLoreConfig == null) {
            plugin.getLogger().warning("Custos lore configuration section not found");
            return Collections.emptyList();
        }

        List<String> costsLore = new ArrayList<>();
        if (money > 0 || xp > 0) {
            costsLore.add(custosLoreConfig.getString("nome", "&fCusto:"));
            if (money > 0) {
                costsLore.add(custosLoreConfig.getString("money", "&a  {money} coins").replace("{money}", formatValue(money)));
            }
            if (xp > 0) {
                costsLore.add(custosLoreConfig.getString("xp", "&b  {xp} Niveis").replace("{xp}", formatValue(xp)));
            }
        }

        return costsLore;
    }

    private void upgrade(Player player, String tipo) {
        int nivelMelhoria = plugin.getNivelMelhoria(player);
        GroupManager group = plugin.getPlayerGroup(player);
        Map<String, Object> custos = tipo.equals("cooldown") ? group.getCooldownCost(nivelMelhoria + 1) : group.getRangeCost(nivelMelhoria + 1);

        if (custos == null) {
            player.sendMessage(colorUtils.colorize("&cVocê já atingiu o nível máximo de melhoria."));
            return;
        }

        double totalMoneyCost = 0;
        int totalXpCost = 0;

        for (Map.Entry<String, Object> custo : custos.entrySet()) {
            ConfigurationSection custoConfig = (ConfigurationSection) custo.getValue();
            String tipoCusto = custoConfig.getString("tipo");
            double preco = custoConfig.getDouble("preco");

            if (tipoCusto.equals("money")) {
                totalMoneyCost += preco;
            } else if (tipoCusto.equals("xp")) {
                totalXpCost += (int) preco;
            }
        }

        if ((totalMoneyCost > 0 && (plugin.getEconomy() == null || !plugin.getEconomy().has(player, totalMoneyCost))) ||
                (totalXpCost > 0 && player.getLevel() < totalXpCost)) {
            player.sendMessage(colorUtils.colorize("&cVocê não tem saldo suficiente."));
            return;
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

    private String formatCooldown(double cooldown) {
        if (cooldown == (long) cooldown) {
            return String.format("%d", (long) cooldown);
        } else {
            return new DecimalFormat("#.##").format(cooldown);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(colorUtils.colorize(plugin.getConfig().getString("menus.melhorias.titulo")))) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

                if (displayName.equals(ChatColor.stripColor(colorUtils.colorize(plugin.getConfig().getString("menus.melhorias.voltar.nome"))))) {
                    openMenu(player, "principal");
                }
            }
        }
    }
}
