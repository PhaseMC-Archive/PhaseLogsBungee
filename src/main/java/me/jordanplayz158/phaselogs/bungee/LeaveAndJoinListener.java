package me.jordanplayz158.phaselogs.bungee;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LeaveAndJoinListener implements Listener {
    @EventHandler
    public void onJoin(PostLoginEvent event) {
        PhaseLogs.getInstance().addMessage(
                event.getPlayer().getName()
                        + " has joined the network. ("
                        + event.getPlayer().getServer().getInfo().getName()
                        +
                        ")"
        );
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        PhaseLogs.getInstance().addMessage(event.getPlayer().getName() + " has left the network. (" + event.getPlayer().getServer().getInfo().getName() + ")");
    }

    @EventHandler
    public void onMove(ServerSwitchEvent event) {
        PhaseLogs.getInstance().addMessage(
                event.getPlayer().getName()
                        + " has moved from "
                        + event.getFrom().getName()
                        + " to "
                        + event.getPlayer().getServer().getInfo().getName());
    }
}