package me.thedivazo.bubbleoverhead.core.armorstand;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import me.thedivazo.bubbleoverhead.common.*;
import me.thedivazo.bubbleoverhead.common.math.Vector;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BubbleArmorStand implements Bubble {
    private final ProtocolManager pm = ProtocolLibrary.getProtocolManager();
    private static final int CUSTOM_NAME_INDEX = 2;
    private static final int CUSTOM_NAME_VISIBLE_INDEX = 3;
    private static final int PARAM_ARMOR_STAND_INDEX;
    static {
        if (Platform.get().currentVersion().compareTo(MinecraftVersion.of("1.17")) >= 0) {
            PARAM_ARMOR_STAND_INDEX = 15;
        } else if (Platform.get().currentVersion().compareTo(MinecraftVersion.of("1.14")) > 0) {
            PARAM_ARMOR_STAND_INDEX = 14;
        } else if (Platform.get().currentVersion().compareTo(MinecraftVersion.of("1.14")) == 0) {
            PARAM_ARMOR_STAND_INDEX = 13;
        } else {
            PARAM_ARMOR_STAND_INDEX = 11;
        }
    }
    private final WrappedDataWatcher.Serializer serBoolean = WrappedDataWatcher.Registry.get(Boolean.class);
    private final WrappedDataWatcher.Serializer serByte = WrappedDataWatcher.Registry.get(Byte.class);

    private final WrappedDataWatcher metadata = new WrappedDataWatcher();

    private Message prevMessage = null;

    /* Business data */
    private final int id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    private final UUID uuid = UUID.randomUUID();
    private final Set<UUID> viewers = new HashSet<>();
    private Message message;
    private Vector vector = Vector.ZERO;

    public BubbleArmorStand() {
        updateSettingsMetadata();
    }

    /* Packet methods */

    private void updateSettingsMetadata() {
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(CUSTOM_NAME_VISIBLE_INDEX, serBoolean), true);
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(PARAM_ARMOR_STAND_INDEX, serByte), (byte)
                ((0x10 /*isMarker*/) | (0x01 /*isSmall*/) | (0x08 /*noBasePlate*/))
        );
        metadata.setObject(0, serByte, (byte) (0x20 /*invisible*/));
    }

    private void updateMetadataMessage() {
        Optional<?> opt;
        if (Platform.get().currentVersion().compareTo("1.13") <= 0) {
            opt = Optional.of(WrappedChatComponent.fromText(message.text()).getHandle());
        } else {
            opt = Optional.of(WrappedChatComponent.fromChatMessage(message.text())[0].getHandle());
        }
        if(Platform.get().currentVersion().compareTo("1.12") > 0) {
            WrappedDataWatcher.Serializer serChatComponent = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(CUSTOM_NAME_INDEX, serChatComponent), opt);
        } else {
            WrappedDataWatcher.Serializer serString = WrappedDataWatcher.Registry.get(String.class);
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(CUSTOM_NAME_INDEX, serString), message.text());
        }
    }

    private PacketContainer getFakeStandPacket() {
        return new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY) {{
            getModifier().writeDefaults();
            getIntegers().write(0, id);

            if(Platform.get().currentVersion().compareTo("1.13") <= 0) {
                getIntegers().write(6, 78);
            } else {
                getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
            }
            // Set location
            getDoubles().write(0, vector.x());
            getDoubles().write(1, vector.y());
            getDoubles().write(2, vector.z());
            getUUIDs().write(0, uuid);
        }};
    }

    private PacketContainer getMetaPacket() {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getIntegers().write(0, id);
        try {
            final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();

            for (final WrappedWatchableObject entry : metadata.getWatchableObjects()) {
                if (entry == null) continue;

                final WrappedDataWatcher.WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
                wrappedDataValueList.add(
                        new WrappedDataValue(
                                watcherObject.getIndex(),
                                watcherObject.getSerializer(),
                                entry.getRawValue()
                        )
                );
            }

            metaPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        }
        catch (Throwable e) {
            metaPacket.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());
        }
        return metaPacket;
    }

    private void updatePosition() {
        PacketContainer teleportPacket = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT) {{
            getModifier().writeDefaults();
            getIntegers().write(0, id);
            getDoubles().write(0, vector.x());
            getDoubles().write(1, vector.y());
            getDoubles().write(2, vector.z());
            getBooleans().write(0, false);
        }};

        for (UUID viewer : viewers) {
            sendPacket(viewer, teleportPacket);
        }
    }

    private void updateMessage() {
        if (prevMessage != null) {
            prevMessage = null;
            updateMetadataMessage();
            for (UUID viewer : viewers) {
                remove(viewer);
                spawn(viewer);
            }
        }
    }

    private void spawn(UUID player) {
        PacketContainer metaPacket = getMetaPacket();
        PacketContainer fakeStandPacket = getFakeStandPacket();

        try {
            sendPacket(player, metaPacket);
            sendPacket(player, fakeStandPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void remove(UUID viewer) {
        PacketContainer removeStandPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);

        IntArrayList list = new IntArrayList(new int[]{id});

        if(Platform.get().currentVersion().compareTo("1.18") >= 0) {
            removeStandPacket.getIntLists().write(0, new ArrayList<>(list));
        } else if(Platform.get().currentVersion().compareTo("1.17") == 0) {
            try {
                removeStandPacket.getModifier().write(0, list);
            } catch (Throwable e) {
                removeStandPacket.getModifier().write(0, id);
            }
        } else {
            removeStandPacket.getModifier().write(0, list.toArray(new int[0]));
        }

        sendPacket(viewer, removeStandPacket);
    }

    private void sendPacket(UUID playerId, PacketContainer packetContainer) {
        Object player = Platform.get().getPlayer(playerId);
        if (player == null) return;
        if (!(player instanceof Player)) return;

        pm.sendServerPacket((Player) player, packetContainer);
    }

    /* Business methods */

    @Override
    public Message message() {
        return message;
    }

    @Override
    public void setMessage(Message message) {
        this.prevMessage = this.message;
        this.message = message;
    }

    @Override
    public void show(Viewer viewer) {
        viewers.add(viewer.uuid());
        spawn(viewer.uuid());
    }

    @Override
    public void update() {
        updateMessage();
        updatePosition();
    }

    @Override
    public void hide(Viewer viewer) {
        viewers.remove(viewer.uuid());
        remove(viewer.uuid());
    }

    @Override
    public void hideAll() {
        for (UUID viewer : viewers) {
            remove(viewer);
        }
        viewers.clear();
    }

    @Override
    public Vector location() {
        return vector;
    }

    @Override
    public void setLocation(Vector vector) {
        this.vector = vector;
    }
}
