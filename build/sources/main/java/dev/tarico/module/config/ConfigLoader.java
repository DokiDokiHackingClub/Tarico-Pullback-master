package dev.tarico.module.config;

import com.google.gson.*;
import dev.tarico.management.FileManager;
import dev.tarico.management.ModuleManager;
import dev.tarico.module.modules.Module;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.module.value.ModeValue;
import dev.tarico.module.value.NumberValue;
import dev.tarico.module.value.Value;

import java.io.*;
import java.util.Map;

public class ConfigLoader {
    String name;
    private static final Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
    private static JsonParser jsonParser = new JsonParser();

    public ConfigLoader(String name) {
        this.name = name;
    }

    public boolean isExist() {
        return new File(FileManager.dir, name + ".json").exists();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void saveSetting() {
        saveSetting(false);
    }

    public void delete() {
        new File(FileManager.dir, name + ".json").delete();
    }

    public void saveSetting(boolean nobind) {
        JsonObject json = new JsonObject();

        for (Module hack : ModuleManager.instance.getModules()) {
            JsonObject jsonHack = new JsonObject();
            jsonHack.addProperty("toggled", hack.getState());
            jsonHack.addProperty("key", nobind ? "no" : String.valueOf(hack.getKey()));

            if (!hack.getValues().isEmpty()) {
                for (Value value : hack.getValues()) {
                    jsonHack.addProperty(value.getName(), String.valueOf(value.getValue()));
                }
            }
            json.add(hack.getName(), jsonHack);
        }

        PrintWriter saveJson = null;
        try {
            saveJson = new PrintWriter(new FileWriter(new File(FileManager.dir, name + ".json")));
            saveJson.println(gsonPretty.toJson(json));
            saveJson.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConfigManager.reload();
    }

    @SuppressWarnings("all")
    public void readConfig() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(FileManager.dir, name + ".json")));
            JsonObject jsonObject = (JsonObject) jsonParser.parse(bufferedReader);
            bufferedReader.close();

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                try {
                    Module module = ModuleManager.instance.getModule(entry.getKey());
                    if (module != null) {
                        JsonObject jsonObjectHack = (JsonObject) entry.getValue();
                        if (jsonObjectHack.get("key").getAsString() != "no") {
                            module.setKey(jsonObjectHack.get("key").getAsInt());
                        }
                        module.setState(jsonObjectHack.get("toggled").getAsBoolean());
                        if (module.getValues().isEmpty()) continue;

                        for (Value value : module.getValues()) {
                            if (jsonObjectHack.get(value.getName()).getAsString() == null)
                                continue;
                            if (!value.getName().equalsIgnoreCase(value.getName()))
                                continue;
                            if (value instanceof BooleanValue) {
                                value.setValue(Boolean.parseBoolean(jsonObjectHack.get(value.getName()).getAsString()));
                                continue;
                            }
                            if (value instanceof NumberValue) {
                                value.setValue(Double.parseDouble(jsonObjectHack.get(value.getName()).getAsString()));
                                continue;
                            }
                            ((ModeValue<Enum<?>>) value).setMode(jsonObjectHack.get(value.getName()).getAsString());
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readConfigNoBind() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(FileManager.dir, name + ".json")));
            JsonObject jsonObject = (JsonObject) jsonParser.parse(bufferedReader);
            bufferedReader.close();

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                Module module = ModuleManager.instance.getModule(entry.getKey());

                if (module != null) {
                    try {
                        JsonObject jsonObjectHack = (JsonObject) entry.getValue();
                        module.setState(jsonObjectHack.get("toggled").getAsBoolean());
                        if (module.getValues().isEmpty()) continue;

                        for (Value value : module.getValues()) {
                            if (jsonObjectHack.get(value.getName()).getAsString() == null)
                                continue;
                            if (!value.getName().equalsIgnoreCase(value.getName()))
                                continue;
                            if (value instanceof BooleanValue) {
                                value.setValue(Boolean.parseBoolean(jsonObjectHack.get(value.getName()).getAsString()));
                                continue;
                            }
                            if (value instanceof NumberValue) {
                                value.setValue(Double.parseDouble(jsonObjectHack.get(value.getName()).getAsString()));
                                continue;
                            }
                            ((ModeValue<Enum<?>>) value).setMode(jsonObjectHack.get(value.getName()).getAsString());
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
