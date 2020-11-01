package net.cpprograms.minecraft.TravelPortals.storage;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum StorageType {
    YAML;

    public static String stringValues() {
        return Arrays.stream(values()).map(t -> t.name().toLowerCase()).collect(Collectors.joining("|"));
    }
}
