package net.cpprograms.minecraft.TravelPortals.storage;

import com.google.common.io.Files;
import net.cpprograms.minecraft.TravelPortals.TravelPortals;
import net.cpprograms.minecraft.TravelPortals.WarpLocation;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    public boolean save() {
        Map<String, List<WarpLocation>> worlds = new HashMap<>();
        for (WarpLocation portal : getPortals().values()) {
            if (!worlds.containsKey(portal.getWorld())) {
                worlds.put(portal.getWorld(), new ArrayList<>());
            }
            worlds.get(portal.getWorld()).add(portal);
        }

        for (Map.Entry<String, List<WarpLocation>> worldPortals : worlds.entrySet()) {
            YamlConfiguration worldConfig = new YamlConfiguration();
            List<Map> portalList = new ArrayList<>();
            for (WarpLocation portal : worldPortals.getValue()) {
                portalList.add(portal.serialize());
            }
            worldConfig.set("portals", portalList);
            try {
                worldConfig.save(new File(worldsFolder, worldPortals.getKey() + ".yml"));
            } catch (IOException e) {
                plugin.logWarning("Could not save the portals of world " + worldPortals.getKey() + " to the config file! " + e.getMessage());
            }
        }
        return true;
    }

    @Override
    public void renameWorld(String oldWorld, String newWorld) {
        super.renameWorld(oldWorld, newWorld);
        File oldConfigFile = getIgnoreCaseFile(worldsFolder, oldWorld + ".yml");
        if (oldConfigFile == null) {
            return;
        }

        File newConfigFile = new File(worldsFolder, newWorld + ".yml");
        try {
            Files.move(oldConfigFile, newConfigFile);
        } catch (IOException e) {
            plugin.logWarning("Error while moving " + oldConfigFile.getName() + " to " + newConfigFile.getName() + "! " + e.getMessage());
        }
    }

    @Override
    public void deleteWorld(String oldWorld) {
        super.deleteWorld(oldWorld);
        File oldConfigFile = getIgnoreCaseFile(worldsFolder, oldWorld + ".yml");
        if (oldConfigFile != null) {
            oldConfigFile.delete();
        }
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
