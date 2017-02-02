package net.cpprograms.minecraft.TravelPortals.storage;

import com.google.common.io.Files;
import net.cpprograms.minecraft.TravelPortals.TravelPortals;
import net.cpprograms.minecraft.TravelPortals.WarpLocation;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlStorage extends PortalStorage {

    private final TravelPortals plugin;
    private final File worldsFolder;

    public YamlStorage(TravelPortals plugin) {
        this.plugin = plugin;
        worldsFolder = new File(plugin.getDataFolder(), "worlds");
    }

    @Override
    public boolean load() {
        try {
            if ((!worldsFolder.exists() || !worldsFolder.isDirectory()) && !worldsFolder.mkdirs()) {
                plugin.logSevere("Could not create worlds folder!");
                return false;
            }
        } catch (SecurityException e) {
            plugin.logSevere("Could not read/write TravelPortals data folder!");
            return false;
        }

        for (File portalFile : worldsFolder.listFiles(file -> file.isFile())) {
            String worldName = Files.getNameWithoutExtension(portalFile.getName());

            if (plugin.getServer().getWorld(worldName) == null) {
                plugin.logWarning("A world with the name " + worldName + " does not exist? Loading the portals anyways!");
            }

            for (Map<?, ?> portalMap : YamlConfiguration.loadConfiguration(portalFile).getMapList("portals")) {
                addPortal(WarpLocation.deserialize(portalMap));
            }
        }

        return true;
    }

    @Override
    public boolean save(WarpLocation portal) {
        return save(portal.getWorld());
    }

    @Override
    public boolean save() {
        boolean success = true;
        for (World world : plugin.getServer().getWorlds()) {
            success = save(world.getName()) && success;
        }
        return success;
    }

    @Override
    public StorageType getType() {
        return StorageType.YAML;
    }

    @Override
    public boolean save(String worldName) {
        List<Map<String, Object>> portalList = new ArrayList<>();
        for (WarpLocation portal : getPortals().values()) {
            if (worldName.equalsIgnoreCase(portal.getWorld())) {
                portalList.add(portal.serialize());
            }
        }

        if (portalList.isEmpty()) {
            return deleteWorldFile(worldName);
        }

        YamlConfiguration worldConfig = new YamlConfiguration();
        worldConfig.set("portals", portalList);
        try {
            worldConfig.save(new File(worldsFolder, worldName + ".yml"));
        } catch (IOException e) {
            plugin.logWarning("Could not save the portals of world " + worldName + " to the config file! " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean renameWorld(String oldWorld, String newWorld) {
        super.renameWorld(oldWorld, newWorld);
        deleteWorldFile(oldWorld);
        return false;
    }

    private boolean deleteWorldFile(String world) {
        File oldConfigFile = getIgnoreCaseFile(worldsFolder, world + ".yml");
        if (oldConfigFile == null) {
            plugin.logWarning("Could not remove " + world + ".yml as it doesn't seem to exist? ");
            return false;
        }
        return oldConfigFile.delete();
    }

    private File getIgnoreCaseFile(File path, String fileName) {
        File correctCase = new File(path, fileName);
        if (correctCase.exists()) {
            return correctCase;
        }

        for (File file : worldsFolder.listFiles(pathName -> pathName.isFile())) {
            if (file.getName().equalsIgnoreCase(fileName)) {
                return file;
            }
        }
        return null;
    }
}
