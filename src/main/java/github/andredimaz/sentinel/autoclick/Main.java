package github.andredimaz.sentinel.autoclick;

import github.andredimaz.sentinel.autoclick.commands.cmdAutoClicker;
import github.andredimaz.sentinel.autoclick.listeners.AutoClickerListener;
import github.andredimaz.sentinel.autoclick.tasks.AutoClickerTask;
import github.andredimaz.sentinel.autoclick.utils.GroupManager;
import github.andredimaz.sentinel.autoclick.utils.MenuUtils;
import github.andredimaz.sentinel.autoclick.utils.colorUtils;
import net.milkbowl.vault.economy.Economy;
import com.ystoreplugins.ypoints.api.yPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

    private Set<EntityType> blacklistedMobs;
    private List<GroupManager> cooldownGroups;
    public MenuUtils menuUtils;
    private Economy econ;
    private yPointsAPI yPointsAPI;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupEconomy();
        setupYPoints();
        loadBlacklistedMobs();
        loadCooldownGroups();
        menuUtils = new MenuUtils(this);
        getServer().getPluginManager().registerEvents(new AutoClickerListener(this), this);
        getServer().getPluginManager().registerEvents(menuUtils, this);
        this.getCommand("autoclick").setExecutor(new cmdAutoClicker(this));
        Bukkit.getConsoleSender().sendMessage( "\n" +
                " §aStatus: §2Ativado\n" +
                " §aDesenvolvedor:§f dimaz\n" +
                " §aContato:§f @notdimaz\n" +
                " §aVersão:§f 1.0\n");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage( "\n" +
                " §cStatus: §4Desativado\n" +
                " §cDesenvolvedor:§f dimaz\n" +
                " §cContato:§f @notdimaz\n" +
                " §cVersão:§f 1.0\n");
    }

    public void loadBlacklistedMobs() {
        blacklistedMobs = new HashSet<>();
        for (String mob : getConfig().getStringList("mobs-blacklist")) {
            try {
                blacklistedMobs.add(EntityType.valueOf(mob));
            } catch (IllegalArgumentException e) {
                getLogger().log(Level.WARNING, "Mob desconhecido na blacklist: " + mob);
            }
        }
    }

    public void loadCooldownGroups() {
        cooldownGroups = new ArrayList<>();
        ConfigurationSection groupSection = getConfig().getConfigurationSection("grupos");
        if (groupSection == null) {
            getLogger().log(Level.SEVERE, "Seção 'grupos' não encontrada na configuração.");
            return;
        }

        for (String group : groupSection.getKeys(false)) {
            ConfigurationSection groupConfig = groupSection.getConfigurationSection(group);
            if (groupConfig == null) {
                getLogger().log(Level.WARNING, "Seção 'grupos." + group + "' não encontrada na configuração.");
                continue;
            }

            int ordem = groupConfig.getInt("ordem", Integer.MAX_VALUE);
            String permission = groupConfig.getString("permissao");

            Map<Integer, Double> cooldownLevels = new HashMap<>();
            Map<Integer, Map<String, Object>> cooldownCosts = new HashMap<>();
            ConfigurationSection cooldownSection = groupConfig.getConfigurationSection("melhorias.cooldown");
            if (cooldownSection != null) {
                for (String level : cooldownSection.getKeys(false)) {
                    ConfigurationSection levelConfig = cooldownSection.getConfigurationSection(level);
                    cooldownLevels.put(Integer.parseInt(level), levelConfig.getDouble("cooldown"));
                    Map<String, Object> costs = new HashMap<>();
                    for (String cost : levelConfig.getConfigurationSection("custos").getKeys(false)) {
                        costs.put(cost, levelConfig.getConfigurationSection("custos").get(cost));
                    }
                    cooldownCosts.put(Integer.parseInt(level), costs);
                }
            }

            Map<Integer, Integer> rangeLevels = new HashMap<>();
            Map<Integer, Map<String, Object>> rangeCosts = new HashMap<>();
            ConfigurationSection rangeSection = groupConfig.getConfigurationSection("melhorias.range");
            if (rangeSection != null) {
                for (String level : rangeSection.getKeys(false)) {
                    ConfigurationSection levelConfig = rangeSection.getConfigurationSection(level);
                    rangeLevels.put(Integer.parseInt(level), levelConfig.getInt("range"));
                    Map<String, Object> costs = new HashMap<>();
                    for (String cost : levelConfig.getConfigurationSection("custos").getKeys(false)) {
                        costs.put(cost, levelConfig.getConfigurationSection("custos").get(cost));
                    }
                    rangeCosts.put(Integer.parseInt(level), costs);
                }
            }

            cooldownGroups.add(new GroupManager(ordem, permission, cooldownLevels, rangeLevels, cooldownCosts, rangeCosts));
        }
        cooldownGroups.sort(Comparator.comparingInt(GroupManager::getOrder));
    }

    public boolean isBlacklisted(EntityType type) {
        return blacklistedMobs.contains(type);
    }

    public void startAutoClicker(Player player) {
        GroupManager group = getPlayerGroup(player);
        int nivelMelhoria = getNivelMelhoria(player);
        BukkitTask task = new AutoClickerTask(this, player, group.getCooldown(nivelMelhoria), group.getRange(nivelMelhoria)).runTaskTimer(this, 0L, (long) (group.getCooldown(nivelMelhoria) * 20));
        player.setMetadata("autoclicker_task", new FixedMetadataValue(this, task));
        String message = getConfig().getString("mensagens.ativado");
        if (message != null && !message.isEmpty()) {
            player.sendMessage(colorUtils.colorize(message));
        }
    }

    public void stopAutoClicker(Player player) {
        if (player.hasMetadata("autoclicker_task")) {
            BukkitTask task = (BukkitTask) player.getMetadata("autoclicker_task").get(0).value();
            task.cancel();
            player.removeMetadata("autoclicker_task", this);
            String message = getConfig().getString("mensagens.desativado");
            if (message != null && !message.isEmpty()) {
                player.sendMessage(colorUtils.colorize(message));
            }
        } else {
            player.sendMessage(colorUtils.colorize(getConfig().getString("mensagens.ja-desativado")));
        }
    }

    public GroupManager getPlayerGroup(Player player) {
        for (GroupManager group : cooldownGroups) {
            if (group.hasPermission(player)) {
                return group;
            }
        }
        return cooldownGroups.get(cooldownGroups.size() - 1);
    }

    public int getNivelMelhoria(Player player) {
        if (!player.hasMetadata("nivelMelhoria")) {
            return 1;
        }
        return player.getMetadata("nivelMelhoria").stream()
                .mapToInt(meta -> meta.asInt())
                .findFirst()
                .orElse(1);
    }

    public void setNivelMelhoria(Player player, int nivel) {
        player.setMetadata("nivelMelhoria", new FixedMetadataValue(this, nivel));
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                econ = rsp.getProvider();
            }
        }
    }

    private void setupYPoints() {
        yPointsAPI = new yPointsAPI();
    }

    public yPointsAPI getYPointsAPI() {
        return yPointsAPI;
    }

    public Economy getEconomy() {
        return econ;
    }
}
