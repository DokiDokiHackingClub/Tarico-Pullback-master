package dev.tarico.module.command.commands;

import dev.tarico.module.command.Command;
import dev.tarico.module.modules.render.HUD;
import dev.tarico.utils.client.Helper;

public class CommandWatermark extends Command {
    public CommandWatermark() {
        super("watermark", new String[]{"wm"}, "Set Watermark");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 1) {
            HUD.wm = args[0];
            Helper.sendMessage("Set watermark to " + HUD.wm);
        } else {
            Helper.sendMessageWithoutPrefix("\u00a7bCorrect usage:\u00a77 .watermark <new>");
        }
        return null;
    }
}

