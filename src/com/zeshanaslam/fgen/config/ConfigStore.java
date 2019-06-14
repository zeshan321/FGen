package com.zeshanaslam.fgen.config;

import com.zeshanaslam.fgen.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ConfigStore {

    private final Main main;

    public HashMap<String, GroupData> groupDataHashMap;
    public TreeMap<Integer, Double> miningReductions;
    public int threshold;
    public String actionBarText;
    public List<String> afkDisabledWorlds;
    public Material burnItem;
    public List<EntityDamageEvent.DamageCause> damageCauses;
    public List<String> damageCauseWorlds;
    public int x;
    public int y;
    public int z;

    public ConfigStore(Main main) {
        this.main = main;
        this.groupDataHashMap = new HashMap<>();

        for (String key: main.getConfig().getStringList("Groups")) {
            if (!main.getConfig().contains(key)) {
                System.err.println("FGen error: " + key + " does not exist in config!");
                continue;
            }

            GroupData groupData = new GroupData(key);

            double totalChance = 0;
            for (String materialString: main.getConfig().getConfigurationSection(key).getKeys(false)) {
                Material material = Material.matchMaterial(materialString);
                double chance = main.getConfig().getDouble(key + "." + materialString);
                totalChance = totalChance + chance;

                groupData.blocks.add(chance, material);
            }

            // Add cobblestone if less then 100
            if (totalChance < 100) {
                double cobbleChance = 100.0 - totalChance;
                System.out.println("FGen warning: " + key + " total does not equal 100. Cobble is generate at " + cobbleChance + "%.");
                groupData.blocks.add(cobbleChance, Material.COBBLESTONE);
            }

            groupDataHashMap.put(key, groupData);
        }

        this.miningReductions = new TreeMap<>(Collections.reverseOrder());
        for (String key: main.getConfig().getConfigurationSection("AFK.MiningReductions").getKeys(false)) {
            int second = Integer.parseInt(key);
            double percent = main.getConfig().getDouble("AFK.MiningReductions." + key);

            miningReductions.put(second, percent);
        }

        this.threshold = main.getConfig().getInt("AFK.Display.Threshold");
        this.actionBarText = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("AFK.Display.ActionBar"));
        this.afkDisabledWorlds = main.getConfig().getStringList("AFK.DisabledWorlds");
        this.burnItem = Material.valueOf(main.getConfig().getString("Burn.Item"));
        this.damageCauses = new ArrayList<>();
        for (String damage: main.getConfig().getStringList("Burn.DamageTypes")) {
            damageCauses.add(EntityDamageEvent.DamageCause.valueOf(damage));
        }

        this.damageCauseWorlds = main.getConfig().getStringList("Burn.DisabledWorlds");
        this.x = main.getConfig().getInt("Burn.x");
        this.y = main.getConfig().getInt("Burn.y");
        this.z = main.getConfig().getInt("Burn.z");
    }

    public GroupData getGroupData(String world, OfflinePlayer offlinePlayer) {
        List<String> keys = new ArrayList<>(groupDataHashMap.keySet());
        Collections.reverse(keys);

        for (String key: keys) {
            if (main.hookHandler.vaultPermissions.playerHas(world, offlinePlayer, "FGen.groups." + key))
                return groupDataHashMap.get(key);
        }


        return null;
    }

    public double getMiningReduction(Player player) {
        if (main.lastMoved.containsKey(player.getUniqueId())) {
            long lastMoved = main.lastMoved.get(player.getUniqueId());
            long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastMoved);

            for (int key: miningReductions.keySet()) {
                double reduction = miningReductions.get(key);

                if (seconds > key)
                    return reduction;
            }
        }

        return -1;
    }
}
