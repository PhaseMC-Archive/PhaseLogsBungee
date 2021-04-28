package me.jordanplayz158.phaselogs.bungee;

import me.jordanplayz158.utils.Initiate;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.md_5.bungee.config.Configuration;
import org.apache.log4j.Level;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Objects;

public class DiscordBot {
    private final Configuration config;
    private JDA jda;

    public DiscordBot() {
        config = PhaseLogs.getInstance().getConfiguration();

        Initiate.log(Level.toLevel(config.getString("logLevel")));

        JDABuilder jdaBuilder = JDABuilder.createLight(config.getString("token"));

        try {
            jda = jdaBuilder
                    .setActivity(Activity.of(Activity.ActivityType.valueOf(config.getString("activity.type").toUpperCase()), config.getString("activity.name")))
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public void sendMessages() {
        Guild guild = jda.getGuilds().get(0);

        long guildId = config.getLong("guild");

        if(guildId != 0L) {
            guild = jda.getGuildById(guildId);
        }

        StringBuilder log = new StringBuilder();

        List<String> messages = PhaseLogs.getInstance().getMessages();

        for(int i = 0; i < messages.size();) {
            if(log.length() + messages.get(i).length() > 2000) {
                break;
            }

            log.append(messages.get(i)).append("\n");

            PhaseLogs.getInstance().getMessages().remove(i);
        }

        if(log.toString().isEmpty()) {
            return;
        }

        assert guild != null;
        Objects.requireNonNull(guild.getTextChannelById(config.getLong("logChannel"))).sendMessage(log.toString()).queue();
    }
}
