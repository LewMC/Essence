package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TagUtil {
    private final Essence plugin;

    public TagUtil(Essence plugin) {
        this.plugin = plugin;
    }

    public String doReplacement(String text) {
        String mcVersion = this.plugin.getServer().getBukkitVersion();
        String[] mcVersionArray = mcVersion.split("-");

        text = text.replace("{{essence-version}}", this.plugin.getDescription().getVersion());
        text = text.replace("{{minecraft-version}}", mcVersionArray[0]);
        text = text.replace("{{time}}", new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        text = text.replace("{{date}}", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
        text = text.replace("{{datetime}}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));

        return text;
    }
}
