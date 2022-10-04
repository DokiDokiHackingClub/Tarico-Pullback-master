package dev.tarico.utils.client;

import java.io.File;

public class FileUtil {
    public static final String SEPARATOR = File.separator;

    public static final String SYS_DIR = System.getenv("WINDIR") + SEPARATOR + "system32";
}
