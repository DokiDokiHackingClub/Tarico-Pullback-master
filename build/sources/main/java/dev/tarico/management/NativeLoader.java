package dev.tarico.management;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class NativeLoader {
    public static void load() {
        try {
            InputStream is = NativeLoader.class.getResourceAsStream("/runtime.dll");
            File f = new File("Tarico-Native.dll");
            if (!f.exists()) {
                f.createNewFile();
            }
            OutputStream os = Files.newOutputStream(f.toPath());
            int index = 0;
            byte[] bytes = new byte[1024];
            while ((index = is.read(bytes)) != -1) {
                os.write(bytes, 0, index);
            }
            os.flush();
            os.close();
            is.close();
            System.load("Tarico-Native.dll");
        } catch (Exception | UnsatisfiedLinkError e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to Load native!\n" + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
