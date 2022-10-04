package dev.tarico.management;

import java.util.HashMap;
import java.util.List;

public class FriendManager {
    private static HashMap<String, String> friends;

    public static boolean isFriend(String name) {
        return friends.containsKey(name);
    }

    public static HashMap<String, String> maps() {
        return friends;
    }

    public void init() {
        friends = new HashMap<>();
        List<String> frriends = FileManager.read("Friends.txt");

        for (String v : frriends) {
            if (v.contains(":")) {
                String name = v.split(":")[0];
                String alias = v.split(":")[1];
                friends.put(name, alias);
            } else {
                friends.put(v, v);
            }
        }
    }
}
