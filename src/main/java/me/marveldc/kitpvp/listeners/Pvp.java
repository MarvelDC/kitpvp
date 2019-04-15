package me.marveldc.kitpvp.listeners;

import me.marveldc.kitpvp.Players;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import static me.marveldc.kitpvp.Kitpvp.instance;
import static me.marveldc.kitpvp.Util.randomNum;


public class Pvp implements org.bukkit.event.Listener {

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player player = e.getEntity();
        if (player.getKiller() instanceof Player) {
            if (player.getKiller() != null) {
                new Players(player.getKiller().getUniqueId(), 1, 0, 0, "get update", player.getName());
                new Players(player.getUniqueId(), 0, 1, randomNum(-2, -8), "get update", "");
            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
            @Override
            public void run() {
                player.spigot().respawn();
            }
        }, 4L);
        //player.spigot().respawn();
    }
}
