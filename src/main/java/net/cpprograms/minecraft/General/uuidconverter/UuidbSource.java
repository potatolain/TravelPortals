package net.cpprograms.minecraft.General.uuidconverter;

import net.cpprograms.minecraft.General.PluginBase;
import net.zaiyers.UUIDDB.bukkit.UUIDDB;

import java.util.UUID;

/*
 * @author Max Lee aka Phoenix616 (mail@moep.tv)
 */

public class UuidbSource implements UuidConverter.Source {
    private final UUIDDB api;

    public UuidbSource(PluginBase plugin) {
        api = (UUIDDB) plugin.getServer().getPluginManager().getPlugin("UUIDDB");
    }

    @Override
    public UUID getUUID(String name) {
        try {
            String uuidStr = api.getStorage().getUUIDByName(name);
            if (uuidStr != null) {
                return UUID.fromString(uuidStr);
            }
        } catch (IllegalArgumentException ignored) {}
        return null;
    }
}
