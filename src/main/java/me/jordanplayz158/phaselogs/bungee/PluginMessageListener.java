package me.jordanplayz158.phaselogs.bungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PluginMessageListener implements Listener {
    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase("logmessage:sent")) {
            return;
        }

        /* args[0] = uuid
         * args[1] = prefix
         * args[2] = user (with ":")
         * args[3] = message
         */
        String[] args = ByteStreams.newDataInput(event.getData()).readUTF().split("\\s+");
        ProxiedPlayer player = PhaseLogs.getInstance().getProxy().getPlayer(args[2].substring(0, args[2].length() - 1));

        sendCustomData(player, args[0]);

        StringBuilder format = new StringBuilder(PhaseLogs.getInstance().getConfiguration().getString("botOutput"));
        StringBuilder message = new StringBuilder();

        for(int i = 3; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("{SERVER}", player.getServer().getInfo().getName());
            put("{PREFIX}", args[1]);
            put("{USERNAME}", player.getName());
            put("{MESSAGE}", message.toString());
        }};


        for(Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            int index = format.indexOf(placeholder.getKey());

            while(index != -1) {
                format.delete(index, index + placeholder.getKey().length());
                format.insert(index, placeholder.getValue());

                index = format.indexOf(placeholder.getKey());
            }
        }

        char[] chatChars = "0123456789abcdefklmnor".toCharArray();

        for(char chatChar : chatChars) {
            String chatColor = "&" + chatChar;
            int index = format.indexOf(chatColor);

            while(index != -1) {
                format.delete(index, index + chatColor.length());

                index = format.indexOf(chatColor);
            }
        }

        PhaseLogs.getInstance().addMessage(format.toString());
    }

    public void sendCustomData(ProxiedPlayer player, String id) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        // perform a check to see if globally are no players
        if (networkPlayers == null || networkPlayers.isEmpty()) {
            return;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(id); // this data could be whatever you want

        // we send the data to the server
        // using ServerInfo the packet is being queued if there are no players in the server
        // using only the server to send data the packet will be lost if no players are in it
        player.getServer().getInfo().sendData( "logmessage:received", out.toByteArray() );
    }
}
