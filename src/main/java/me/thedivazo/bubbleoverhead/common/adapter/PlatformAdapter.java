package me.thedivazo.bubbleoverhead.common.adapter;

import me.thedivazo.bubbleoverhead.common.MinecraftVersion;
import me.thedivazo.bubbleoverhead.common.Viewer;
import me.thedivazo.bubbleoverhead.common.World;
import me.thedivazo.bubbleoverhead.common.math.Location;

import java.util.UUID;

public interface PlatformAdapter {
    MinecraftVersion currentVersion();

    Object asPlatformWorld(World world);
    Object fromLocation(Location location);
    Object getPlayer(UUID uuid);

}
