package dev.tarico.module.config;

import dev.tarico.management.FileManager;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConfigManager {
    public static CopyOnWriteArrayList<String> configs = new CopyOnWriteArrayList<>();

    public static void reload() {
        configs.clear();
        File[] files = FileManager.dir.listFiles();
        if (files == null)
            return;
        for (File f : files) {
            if (f.getName().endsWith(".json") && !f.getName().equals("filter.json")) {
                configs.add(f.getName().replace(".json", ""));
            }
        }
    }
}

