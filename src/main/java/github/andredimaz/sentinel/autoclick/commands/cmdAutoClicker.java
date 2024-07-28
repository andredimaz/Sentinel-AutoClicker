package github.andredimaz.sentinel.autoclick.commands;

import github.andredimaz.sentinel.autoclick.Main;
import github.andredimaz.sentinel.autoclick.utils.colorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cmdAutoClicker implements CommandExecutor {

    private final Main plugin;

    public cmdAutoClicker(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorUtils.colorize("&cApenas jogadores podem usar este comando."));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            plugin.menuUtils.openMenu(player);
            return true;
        }


        String message = plugin.getConfig().getString("mensagens.comando-invalido");

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                plugin.startAutoClicker(player);
                return true;
            } else if (args[0].equalsIgnoreCase("off")) {
                plugin.stopAutoClicker(player);
                return true;
            } else {
                player.sendMessage(colorUtils.colorize(message));
                return true;
            }
        }

        player.sendMessage(colorUtils.colorize(message));
        return true;
    }
}