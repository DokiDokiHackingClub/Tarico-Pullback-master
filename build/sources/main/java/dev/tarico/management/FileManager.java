package dev.tarico.management;

import com.google.gson.*;
import me.cubk.plugin.PickupFilterManager;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileManager {
    public static File dir;

    static {
        final File mcDataDir = Minecraft.getMinecraft().mcDataDir;
        FileManager.dir = new File(mcDataDir, "Tarico");
    }

    public FileManager() {
        super();
    }

    public static void init() {
        if (!FileManager.dir.exists()) {
            if (!FileManager.dir.mkdir())
                System.err.println("Failed init config files");
        }
    }

    public static List<String> read(final String file) {
        final List<String> out = new ArrayList<>();
        try {
            if (!FileManager.dir.exists()) {
                if (!FileManager.dir.mkdir())
                    System.err.println("Failed init config files");
            }
            final File f = new File(FileManager.dir, file);
            if (!f.exists()) {
                if (f.createNewFile())
                    System.err.println("Failed init config files");
            }
            try {
                try (FileInputStream fis = new FileInputStream(f)) {
                    try (InputStreamReader isr = new InputStreamReader(fis)) {
                        try (BufferedReader br = new BufferedReader(isr)) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                out.add(line);
                            }
                        }
                    }
                    fis.close();
                    return out;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    private static JsonParser jsonParser = new JsonParser();
    private static Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();


    public static void save(final String file, final String content, final boolean append) {
        try {
            final File f = new File(FileManager.dir, file);
            if (!f.exists()) {
                if (!f.createNewFile())
                    System.err.println("Failed init config files");
            }
            try (FileWriter writer = new FileWriter(f, append)) {
                writer.write(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadPickupFilter() {
        try {
            BufferedReader loadJson = new BufferedReader(new FileReader(new File(dir, "filter.json")));
            JsonObject json = (JsonObject) jsonParser.parse(loadJson);
            loadJson.close();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                JsonObject jsonData = (JsonObject) entry.getValue();
                int id = Integer.parseInt(entry.getKey());
                PickupFilterManager.addItem(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void savePickupFilter() {
        try {
            JsonObject json = new JsonObject();

            for (int id : PickupFilterManager.items) {
                JsonObject jsonData = new JsonObject();

                jsonData.addProperty("name", Item.getItemById(id).getUnlocalizedName());

                json.add("" + id, jsonData);
            }
            PrintWriter saveJson = new PrintWriter(new FileWriter(new File(dir, "filter.json")));
            saveJson.println(gsonPretty.toJson(json));
            saveJson.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

