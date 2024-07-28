package github.andredimaz.sentinel.autoclick;

import github.andredimaz.sentinel.autoclick.commands.cmdAutoClicker;
import github.andredimaz.sentinel.autoclick.groups.CooldownGroup;
import github.andredimaz.sentinel.autoclick.listeners.AutoClickerListener;
import github.andredimaz.sentinel.autoclick.tasks.AutoClickerTask;
import github.andredimaz.sentinel.autoclick.utils.MenuUtils;
import github.andredimaz.sentinel.autoclick.utils.colorUtils;
import github.andredimaz.sentinel.autoclick.utils.materialUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

    private Set<EntityType> blacklistedMobs;
    private List<CooldownGroup> cooldownGroups;
    public MenuUtils menuUtils;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadBlacklistedMobs();
        loadCooldownGroups();
        menuUtils = new MenuUtils(this);
        getServer().getPluginManager().registerEvents(new AutoClickerListener(this), this);
        this.getCommand("autoclick").setExecutor(new cmdAutoClicker(this));
        Bukkit.getConsoleSender().sendMessage( "\n" +
                "§a #####   #######  ##   ##   # #####  ######  ##   ##  #######  ####     \n" +
                "§a##   ##   ##   #  ###  ##  ## ## ##    ##    ###  ##   ##   #   ##      \n" +
                "§a##        ##      #### ##     ##       ##    #### ##   ##       ##      \n" +
                "§a #####    ####    #######     ##       ##    #######   ####     ##      \n" +
                "§a     ##   ##      ## ####     ##       ##    ## ####   ##       ##      \n" +
                "§a##   ##   ##   #  ##  ###     ##       ##    ##  ###   ##   #   ##  ##  \n" +
                "§a #####   #######  ##   ##    ####    ######  ##   ##  #######  #######  \n" +
                "                                                                        \n" +
                " §aStatus: §2Ativado\n" +
                " §aDesenvolvedor:§f dimaz\n" +
                " §aContato:§f @notdimaz\n" +
                " §aVersão:§f 1.0\n");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage( "\n" +
                "§a #####   #######  ##   ##   # #####  ######  ##   ##  #######  ####     \n" +
                "§a##   ##   ##   #  ###  ##  ## ## ##    ##    ###  ##   ##   #   ##      \n" +
                "§a##        ##      #### ##     ##       ##    #### ##   ##       ##      \n" +
                "§a #####    ####    #######     ##       ##    #######   ####     ##      \n" +
                "§a     ##   ##      ## ####     ##       ##    ## ####   ##       ##      \n" +
                "§a##   ##   ##   #  ##  ###     ##       ##    ##  ###   ##   #   ##  ##  \n" +
                "§a #####   #######  ##   ##    ####    ######  ##   ##  #######  #######  \n" +
                "                                                                        \n" +
                " §cStatus: §4Desativado\n" +
                " §cDesenvolvedor:§f dimaz\n" +
                " §cContato:§f @notdimaz\n" +
                " §cVersão:§f 1.0\n");
    }


    private void loadBlacklistedMobs() {
        blacklistedMobs = new HashSet<>();
        for (String mob : getConfig().getStringList("mobs-blacklist")) {
            try {
                blacklistedMobs.add(EntityType.valueOf(mob));
            } catch (IllegalArgumentException e) {
                getLogger().log(Level.WARNING, "Mob desconhecido na blacklist: " + mob);
            }
        }
    }

    private void loadCooldownGroups() {
        cooldownGroups = new ArrayList<>();
        for (String group : getConfig().getConfigurationSection("cooldown").getKeys(false)) {
            int ordem = getConfig().getInt("cooldown." + group + ".ordem", Integer.MAX_VALUE);
            double cooldown = getConfig().getDouble("cooldown." + group + ".cooldown");
            String permission = getConfig().getString("cooldown." + group + ".permissao");
            cooldownGroups.add(new CooldownGroup(ordem, cooldown, permission));
        }
        cooldownGroups.sort(Comparator.comparingInt(CooldownGroup::getOrder));
    }

    public boolean isBlacklisted(EntityType type) {
        return blacklistedMobs.contains(type);
    }

    public void startAutoClicker(Player player) {
        CooldownGroup group = getPlayerCooldownGroup(player);
        BukkitTask task = new AutoClickerTask(this, player, group.getCooldown()).runTaskTimer(this, 0L, (long) (group.getCooldown() * 20));
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
        }
    }

    public CooldownGroup getPlayerCooldownGroup(Player player) {
        for (CooldownGroup group : cooldownGroups) {
            if (group.hasPermission(player)) {
                return group;
            }
        }
        return cooldownGroups.get(cooldownGroups.size() - 1); // Retorna o grupo de menor prioridade se nenhum outro corresponder
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("autoclick")) {
                menuUtils.openMenu(player);
                return true;
            }
        }
        return false;
    }
}