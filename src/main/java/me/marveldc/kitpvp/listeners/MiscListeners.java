package me.marveldc.kitpvp.listeners;

import me.marveldc.kitpvp.Players;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class MiscListeners implements org.bukkit.event.Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        //e.getPlayer().setScoreboard(Util.setScoreboard());
        new Players(e.getPlayer().getUniqueId(), 0, 0, 0, "get", "");
    }
}
