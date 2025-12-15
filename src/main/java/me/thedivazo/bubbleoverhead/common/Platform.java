package me.thedivazo.bubbleoverhead.common;

import me.thedivazo.bubbleoverhead.common.adapter.PlatformAdapter;

public class Platform {
    private static volatile PlatformAdapter ADAPTER;

    public static void init(PlatformAdapter adapter) {
        if (ADAPTER != null) throw new IllegalStateException("PlatformAdapter already initialized");
        ADAPTER = adapter;
    }

    public static PlatformAdapter get() {
        PlatformAdapter a = ADAPTER;
        if (a == null) throw new IllegalStateException("PlatformAdapter not initialized yet");
        return a;
    }
}
