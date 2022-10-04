package dev.tarico.module.auth;

import by.radioegor146.nativeobfuscator.Native;

public class Wrapper {
    @Native
    public static boolean isLogin() {

        // Package Access
        return GuiLogin.login;
    }

    public static void setlogin(boolean is) {
        GuiLogin.login = is;
    }
}
