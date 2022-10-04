package dev.tarico.api.pluginapi;

import dev.tarico.Client;
import dev.tarico.utils.Mapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Keyboard;

/**
 * 游戏状态与设置获取
 */
public class PluginGameSetting {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static PluginGameSetting getGameSettings() {
        return new PluginGameSetting();
    }

    /**
     * 获取玩家视角状态
     *
     * @return 视角状态ID
     */

    public int getThirdPersonViewState() {
        return mc.gameSettings.thirdPersonView;
    }

    /**
     * 设定玩家视角状态
     *
     * @param personView 视角状态ID 1=第一人称视角 2=第二人称视角 3=第三人称视角
     */

    public void setThirdPersonView(int personView) {
        mc.gameSettings.thirdPersonView = personView;
    }

    /**
     * 判断按钮按下
     *
     * @param key 按键类型
     * @return boolean类型判断结果
     */

    public boolean isPressed(Keybind key) {
        GameSettings gameSettings = mc.gameSettings;
        if (key == Keybind.ATTACK) {
            return Mapping.getPressed(gameSettings.keyBindAttack);
        } else if (key == Keybind.USE_ITEM) {
            return Mapping.getPressed(gameSettings.keyBindUseItem);
        } else if (key == Keybind.FORWARD) {
            return Mapping.getPressed(gameSettings.keyBindForward);
        } else if (key == Keybind.BACK) {
            return Mapping.getPressed(gameSettings.keyBindBack);
        } else if (key == Keybind.LEFT) {
            return Mapping.getPressed(gameSettings.keyBindLeft);
        } else if (key == Keybind.RIGHT) {
            return Mapping.getPressed(gameSettings.keyBindRight);
        } else if (key == Keybind.PLAYER_LIST) {
            return Mapping.getPressed(gameSettings.keyBindPlayerList);
        } else if (key == Keybind.SPRINT) {
            return Mapping.getPressed(gameSettings.keyBindSprint);
        } else if (key == Keybind.JUMP) {
            return Mapping.getPressed(gameSettings.keyBindJump);
        } else if (key == Keybind.SNEAK) {
            return Mapping.getPressed(gameSettings.keyBindSneak);
        } else {
            return false;
        }
    }

    /**
     * 判断某个键盘码是否按下
     *
     * @param key 按键码
     * @return boolean类型判断结果
     */
    public boolean isKeyDown(int key){
        return Keyboard.isKeyDown(key);
    }


    /**
     * 获取鼠标灵敏度
     *
     * @return float类型灵敏度值
     */

    public float getMouseSensitivity() {
        return mc.gameSettings.mouseSensitivity;
    }

    /**
     * 设置鼠标灵敏度
     *
     * @param sensitivity float类型灵敏度值
     */

    public void setMouseSensitivity(float sensitivity) {
        mc.gameSettings.mouseSensitivity = sensitivity;
    }

    /**
     * 获取是否开启视角摇晃
     *
     * @return boolean类型的判断值
     */

    public boolean isViewBobbing() {
        return mc.gameSettings.viewBobbing;
    }

    /**
     * 设置是否开启视角摇晃
     *
     * @param stage boolean类型的值
     */

    public void setViewBobbing(boolean stage) {
        mc.gameSettings.viewBobbing = stage;
    }

    /**
     * 获取当前TimerSpeed
     *
     * @return 当前TimerSpeed
     */

    public float getTimerSpeed() {
        return Client.getTimer().timerSpeed;
    }

    /**
     * 设置当前TimerSpeed
     *
     * @param speed 要设置的TimerSpeed
     */

    public void setTimerSpeed(float speed) {
        Client.getTimer().timerSpeed = speed;
    }
}
