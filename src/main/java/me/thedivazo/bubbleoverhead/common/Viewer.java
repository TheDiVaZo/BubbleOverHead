package me.thedivazo.bubbleoverhead.common;

import me.thedivazo.bubbleoverhead.common.math.Location;

import java.util.UUID;

public interface Viewer {
    UUID uuid();
    Location location();
}
