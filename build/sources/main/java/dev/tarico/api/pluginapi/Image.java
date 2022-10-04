package dev.tarico.api.pluginapi;

import dev.tarico.utils.Base64ImageLocation;

import java.util.HashMap;
import java.util.Map;


/**
 * 图片数据
 */
public class Image {
    public static Map<String, Base64ImageLocation> maps = new HashMap<>();

    public final Base64ImageLocation location;


    /**
     * 生成图片
     * @param base64 图片的base64编码
     */
    public Image(String base64) {
        if (maps.containsKey(base64)) {
            location = maps.get(base64);
        } else {
            location = new Base64ImageLocation(base64);
            maps.put(base64, location);
        }
    }
}
