package com.example.teleport;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class TeleportPlugin extends JavaPlugin {

    private final Map<UUID, TeleportRequest> requests = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("TeleportPlugin enabled!");
    }

    @Override
    public void onDisable() {
        requests.clear();
        getLogger().info("TeleportPlugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by a player!");
            return true;
        }

        String name = command.getName().toLowerCase();
        switch (name) {
            case "tpi":
            case "tpc":
                if (args.length < 1) {
                    player.sendMessage("§cPlease specify a player name!");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    player.sendMessage("§cPlayer not found!");
                    return true;
                }
                TeleportRequest.Type type = name.equals("tpi") ? TeleportRequest.Type.TPI : TeleportRequest.Type.TPC;
                TeleportRequest req = new TeleportRequest(player.getUniqueId(), target.getUniqueId(), type);
                requests.put(target.getUniqueId(), req);

                target.sendMessage("§e" + player.getName() +
                        (type == TeleportRequest.Type.TPI ? " wants to teleport to you." : " has invited you to their location."));
                target.sendMessage("§aAccept with /tpa, §cDecline with /tpd");

                player.sendMessage("§aRequest sent to " + target.getName() + ".");
                return true;

            case "tpa":
                TeleportRequest r = requests.get(player.getUniqueId());
                if (r == null) {
                    player.sendMessage("§cNo pending requests to accept!");
                    return true;
                }
                if (System.currentTimeMillis() - r.getTimestamp() > 5 * 60 * 1000) {
                    player.sendMessage("§cThis request has expired!");
                    requests.remove(player.getUniqueId());
                    return true;
                }
                Player senderPlayer = Bukkit.getPlayer(r.getSender());
                if (senderPlayer == null) {
                    player.sendMessage("§cSender is offline!");
                    requests.remove(player.getUniqueId());
                    return true;
                }

                if (r.getType() == TeleportRequest.Type.TPI) {
                    senderPlayer.teleport(player.getLocation());
                    senderPlayer.sendMessage("§aTeleported to " + player.getName() + "!");
                    player.sendMessage("§aYou accepted the request.");
                } else {
                    player.teleport(senderPlayer.getLocation());
                    player.sendMessage("§aYou accepted " + senderPlayer.getName() + "'s invitation!");
                    senderPlayer.sendMessage("§a" + player.getName() + " accepted your invitation!");
                }

                requests.remove(player.getUniqueId());
                return true;

            case "tpd":
                TeleportRequest r2 = requests.get(player.getUniqueId());
                if (r2 == null) {
                    player.sendMessage("§cNo requests to decline!");
                    return true;
                }
                Player senderP = Bukkit.getPlayer(r2.getSender());
                if (senderP != null) {
                    senderP.sendMessage("§c" + player.getName() + " declined the request.");
                }
                player.sendMessage("§cYou declined the request.");
                requests.remove(player.getUniqueId());
                return true;
        }

        return false;
    }
}
