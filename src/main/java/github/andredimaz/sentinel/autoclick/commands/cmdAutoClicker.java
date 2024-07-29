package github.andredimaz.sentinel.autoclick.commands;

import github.andredimaz.sentinel.autoclick.Main;
import github.andredimaz.sentinel.autoclick.utils.colorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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
            plugin.menuUtils.openMenu(player, "principal");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                plugin.startAutoClicker(player);
                return true;
            } else if (args[0].equalsIgnoreCase("off")) {
                plugin.stopAutoClicker(player);
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!player.hasPermission("autoclicker.admin")) {
                    player.sendMessage(colorUtils.colorize("&cVocê não tem permissão para usar este comando."));
                    return true;
                }

                plugin.reloadConfig();
                plugin.loadBlacklistedMobs();
                plugin.loadCooldownGroups();
                player.sendMessage(colorUtils.colorize("&aConfiguração recarregada com sucesso."));
                return true;
            } else if (args[0].equalsIgnoreCase("resetar") || args[0].equalsIgnoreCase("reset")) {
                sender.sendMessage(colorUtils.colorize("&cUso inválido. Use /ac reset <jogador>"));
                return true;
            } else if (args[0].equalsIgnoreCase("ajuda") || args[0].equalsIgnoreCase("help")) {
                mostrarAjuda(player);
                return true;
            } else {
                player.sendMessage(colorUtils.colorize(plugin.getConfig().getString("mensagens.comando-invalido")));
                return true;
            }
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("resetar") || args[0].equalsIgnoreCase("reset"))) {
            if (!player.hasPermission("autoclicker.admin")) {
                player.sendMessage(colorUtils.colorize("&cVocê não tem permissão para usar este comando."));
                return true;
            }

            String targetName = args[1];
            if (targetName.equals("*")) {
                for (Player target : Bukkit.getOnlinePlayers()) {
                    plugin.setNivelMelhoria(target, 1);
                }
                player.sendMessage(colorUtils.colorize("&aNíveis de melhoria resetados para todos os jogadores."));
            } else {
                Player target = Bukkit.getPlayer(targetName);
                if (target != null) {
                    plugin.setNivelMelhoria(target, 1);
                    player.sendMessage(colorUtils.colorize("&aNível de melhoria resetado para " + target.getName() + "."));
                } else {
                    player.sendMessage(colorUtils.colorize("&cJogador não encontrado: " + targetName));
                }
            }
            return true;
        }

        player.sendMessage(colorUtils.colorize(plugin.getConfig().getString("mensagens.comando-invalido")));
        return true;
    }

    private void mostrarAjuda(Player player) {
        List<String> ajudaMensagens = plugin.getConfig().getStringList("mensagens.ajuda");
        for (String linha : ajudaMensagens) {
            player.sendMessage(colorUtils.colorize(linha));
        }
    }
}
