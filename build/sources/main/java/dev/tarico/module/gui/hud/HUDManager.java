package dev.tarico.module.gui.hud;

import dev.tarico.management.FileManager;
import dev.tarico.module.gui.hud.implement.*;

import java.util.ArrayList;
import java.util.List;

public class HUDManager {
    public static ArrayList<HUDObject> hudObjects = new ArrayList<>();

    public static ScoreboardHUD scoreboardHUD = new ScoreboardHUD();


    public static void init() {
        try {
            hudObjects.add(new InventoryHUD());
            hudObjects.add(new RadarHUD());
            hudObjects.add(new KeybindsHUD());
            hudObjects.add(scoreboardHUD);
            hudObjects.add(new KeyHUD());
            hudObjects.add(new SessionHUD());
            hudObjects.add(new TargetHUD());
            List<String> winpos = FileManager.read("HUD.txt");
            for (String v : winpos) {
                String name = v.split(":")[0];
                HUDObject w = null;
                for (HUDObject win : hudObjects) {
                    if (win.getName().equals(name))
                        w = win;
                }
                if (w == null) continue;
                w.setPosX(Integer.parseInt(v.split(":")[1]));
                w.setPosY(Integer.parseInt(v.split(":")[2]));
//                w.m.setState(Boolean.parseBoolean(v.split(":")[3]));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void savePos() {
        StringBuilder windowss = new StringBuilder();
        for (HUDObject w : hudObjects) {
            windowss.append(String.format("%s:%s:%s%s", w.getName(), w.getPosX(), w.getPosY(), System.lineSeparator()));
        }
        FileManager.save("HUD.txt", windowss.toString(), false);
    }
}
