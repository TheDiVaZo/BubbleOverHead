package me.thedivazo.bubbleoverhead.common.adapter;

import me.thedivazo.bubbleoverhead.common.MinecraftVersion;
import me.thedivazo.bubbleoverhead.common.World;
import me.thedivazo.bubbleoverhead.common.math.Location;

public interface PlatformAdapter {
    MinecraftVersion getCurrentVersion();

    Object asPlatformWorld(World world);
    Object fromLocation(Location location);

}
