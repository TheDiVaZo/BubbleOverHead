package me.thedivazo.bubbleoverhead.core.bukkit;

import me.thedivazo.bubbleoverhead.common.MinecraftVersion;
import me.thedivazo.bubbleoverhead.common.World;
import me.thedivazo.bubbleoverhead.common.adapter.PlatformAdapter;
import me.thedivazo.bubbleoverhead.common.math.Location;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BukkitAdapter implements PlatformAdapter {

    private static final MinecraftVersion CURRENT_VERSION = MinecraftVersion.of(Bukkit.getVersion());

    @Override
    public MinecraftVersion currentVersion() {
        return CURRENT_VERSION;
    }

    @Override
    public org.bukkit.World asPlatformWorld(World world) {
        return Bukkit.getWorld(world.uuid());
    }

    @Override
    public org.bukkit.Location fromLocation(Location location) {
        return new org.bukkit.Location(asPlatformWorld(location.world()), location.x(), location.y(), location.z());
    }

    @Override
    @Nullable
    public Player getPlayer(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }
}
