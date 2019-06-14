package com.zeshanaslam.fgen;

import com.zeshanaslam.fgen.utils.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class AFKTask extends BukkitRunnable {

    private final Main main;

    public AFKTask(Main main) {
        this.main = main;
    }

    @Override
    public void run() {
        for (Player player: Bukkit.getOnlinePlayers()) {
            if (main.configStore.afkDisabledWorlds.contains(player.getWorld().getName()) || player.hasPermission("FGen.AfkBypass"))
                continue;

            long lastMoved = main.lastMoved.get(player.getUniqueId());

            LocalDateTime afkStart =  LocalDateTime.ofInstant(Instant.ofEpochMilli(lastMoved), TimeZone.getDefault().toZoneId());
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(afkStart, now);

            long hours = duration.toHours();
            int minutes = (int) ((duration.getSeconds() % 3600) / 60);
            int seconds = (int) (duration.getSeconds() % 60);

            if (seconds > main.configStore.threshold) {
                String text = main.configStore.actionBarText.replace("%hours%", String.valueOf(hours))
                        .replace("%mins%", String.valueOf(minutes))
                        .replace("%seconds%", String.valueOf(seconds));

                ActionBar.sendActionbar(player, text);
            }
        }
    }
}
