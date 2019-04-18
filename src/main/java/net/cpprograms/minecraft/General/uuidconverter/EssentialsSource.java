package net.cpprograms.minecraft.General.uuidconverter;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.cpprograms.minecraft.General.PluginBase;

import java.util.UUID;

/*
 * @author Max Lee aka Phoenix616 (mail@moep.tv)
 */

public class EssentialsSource implements UuidConverter.Source {
    private final Essentials api;

    public EssentialsSource(PluginBase plugin) {
        api = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
    }

    @Override
    public UUID getUUID(String name) {
        User user = api.getUser(name);
        return user != null ? user.getConfigUUID() : null;
    }
}
