package com.zeshanaslam.fgen.config;

import com.zeshanaslam.fgen.utils.RandomCollection;
import org.bukkit.Material;


public class GroupData {

    public String group;
    public RandomCollection<Material> blocks;

    public GroupData(String group) {
        this.group = group;
        this.blocks = new RandomCollection<>();
    }
}
