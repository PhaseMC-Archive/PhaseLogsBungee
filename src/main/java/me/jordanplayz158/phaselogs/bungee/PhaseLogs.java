package me.jordanplayz158.phaselogs.bungee;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PhaseLogs extends Plugin implements Listener {
    private static PhaseLogs instance;
    private List<String> messages = new ArrayList<>();
    private Configuration configuration;
    private DiscordBot discordBot;

    @Override
    public void onEnable() {
        instance = this;

        getProxy().registerChannel("logmessage:sent");
        getProxy().registerChannel("logmessage:received");
        getProxy().getPluginManager().registerListener(this, new PluginMessageListener());
        getProxy().getPluginManager().registerListener(this, new LeaveAndJoinListener());

        ConfigurationProvider configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }
            File configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                    try (InputStream is = getResourceAsStream("config.yml");
                         OutputStream os = new FileOutputStream(configFile)) {
                        ByteStreams.copy(is, os);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Unable to create configuration file", e);
                }
            }

            configuration = configurationProvider.load(configFile);

            discordBot = new DiscordBot();

            String delay = configuration.getString("messageBufferRate");
            int delayInt = Integer.parseInt(delay.substring(0, delay.length() - 1));

            getProxy().getScheduler().schedule(this, discordBot::sendMessages, delayInt, delayInt, getTimeUnit(delay));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TimeUnit getTimeUnit(String time) {
        switch(time.substring(time.length() - 1).toLowerCase()) {
            case "s":
                return TimeUnit.SECONDS;
            case "m":
                return TimeUnit.MINUTES;
            case "h":
                return TimeUnit.HOURS;
            case "d":
                return TimeUnit.DAYS;
            default:
                return null;
        }
    }

    public static PhaseLogs getInstance() {
        return instance;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void addMessage(String message) {
        messages.add(message);
    }
}
