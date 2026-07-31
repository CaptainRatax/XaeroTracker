package info.infinf.xaeroTracker.util;

import info.infinf.xaeroTracker.XaeroTracker;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;

public final class MessageUtil {
    private static final byte LEVEL_ID_PACKET = 0;
    private static final byte HANDSHAKE_PACKET = 1;
    private static final byte TRACKED_PLAYER_PACKET = 2;
    private static final byte RESET_PACKET = 3;

    private MessageUtil() {
    }

    public static byte[] getLevelIdMessage(int levelId) {
        return ByteBuffer.allocate(5)
                .put(LEVEL_ID_PACKET)
                .putInt(levelId)
                .array();
    }

    public static byte[] getHandshakeMessage() {
        return ByteBuffer.allocate(5)
                .put(HANDSHAKE_PACKET)
                .putInt(XaeroTracker.SUPPORTED_NETWORK_VERSION)
                .array();
    }

    public static byte[] getTrackPlayerMessage(Player pl) {
        // Packet type followed by an unnamed NBT compound.
        var msg = new ByteArrayOutputStream(93);

        try (var wrappedMsg = new DataOutputStream(msg)) {
            wrappedMsg.writeByte(TRACKED_PLAYER_PACKET);
            var uuidArray = UUIDUtil.uuidToIntArray(pl.getUniqueId());
            wrappedMsg.writeByte(10);
            wrappedMsg.writeByte(1);
            wrappedMsg.writeUTF("r");
            wrappedMsg.writeByte(0);
            wrappedMsg.writeByte(11);
            wrappedMsg.writeUTF("i");
            wrappedMsg.writeInt(uuidArray.length);
            for (var i: uuidArray) {
                wrappedMsg.writeInt(i);
            }
            wrappedMsg.writeByte(6);
            wrappedMsg.writeUTF("x");
            wrappedMsg.writeDouble(pl.getX());
            wrappedMsg.writeByte(6);
            wrappedMsg.writeUTF("y");
            wrappedMsg.writeDouble(pl.getY());
            wrappedMsg.writeByte(6);
            wrappedMsg.writeUTF("z");
            wrappedMsg.writeDouble(pl.getZ());
            wrappedMsg.writeByte(8);
            wrappedMsg.writeUTF("d");
            wrappedMsg.writeUTF(pl.getWorld().getKey().toString());
            wrappedMsg.writeByte(0);
        } catch (IOException ex) {
            throw new UncheckedIOException("Could not encode a tracked-player packet", ex);
        }

        return msg.toByteArray();
    }

    public static byte[] getUntrackPlayerMessage(Player pl) {
        // The same NBT packet with the remove flag and the player's UUID.
        var msg = new ByteArrayOutputStream(32);

        try (var wrappedMsg = new DataOutputStream(msg)) {
            wrappedMsg.writeByte(TRACKED_PLAYER_PACKET);
            var uuidArray = UUIDUtil.uuidToIntArray(pl.getUniqueId());
            wrappedMsg.writeByte(10);
            wrappedMsg.writeByte(1);
            wrappedMsg.writeUTF("r");
            wrappedMsg.writeByte(1);
            wrappedMsg.writeByte(11);
            wrappedMsg.writeUTF("i");
            wrappedMsg.writeInt(uuidArray.length);
            for (var i: uuidArray) {
                wrappedMsg.writeInt(i);
            }
            wrappedMsg.writeByte(0);
        } catch (IOException ex) {
            throw new UncheckedIOException("Could not encode an untracked-player packet", ex);
        }

        return msg.toByteArray();
    }

    public static byte[] getTrackResetMessage() {
        return new byte[] {RESET_PACKET};
    }
}
