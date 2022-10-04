package dev.tarico.module.gui.hud.implement;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import dev.tarico.module.command.commands.CommandScoreboard;
import dev.tarico.module.gui.hud.HUDObject;
import dev.tarico.module.modules.render.HUD;
import dev.tarico.module.value.BooleanValue;
import dev.tarico.utils.client.Mapper;
import dev.tarico.utils.client.RenderUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class ScoreboardHUD extends HUDObject {
    public String title;
    public static BooleanValue<Boolean> nobar = new BooleanValue<>("No Top Bar",false);

    public ScoreboardHUD() {
        super(60, "Scoreboard");
    }

    @Override
    public boolean doDraw() {
        return mc.theWorld.getScoreboard() != null;
    }



    @Override
    public void drawHUD(int x, int y, float p) {
        setHide(nobar.getValue());
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective scoreobjective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(mc.thePlayer.getName());

        if (scoreplayerteam != null) {
            int i1 = scoreplayerteam.getChatFormat().getColorIndex();

            if (i1 >= 0) {
                scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + i1);
            }
        }

        ScoreObjective scoreobjective1 = scoreobjective != null ? scoreobjective
                : scoreboard.getObjectiveInDisplaySlot(1);

        if (scoreobjective1 != null) {
            Collection<Score> collection = scoreboard.getSortedScores(scoreobjective1);
            ArrayList arraylist = collection.stream().filter(p_apply_1_ -> p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#")).collect(Collectors.toCollection(Lists::newArrayList));

            ArrayList<Score> arraylist1;

            if (arraylist.size() > 15) {
                arraylist1 = Lists.newArrayList(Iterables.skip(arraylist, collection.size() - 15));
            } else {
                arraylist1 = arraylist;
            }

            int height = 8;
            title = "Scoreboard";
            float width = mc.fontRendererObj.getStringWidth(title) + 10;
            int height2 = 0;
            for (int i = -2; i < arraylist1.size(); i++) {
                height2 += mc.fontRendererObj.FONT_HEIGHT + 1;
            }
            RenderUtil.drawShadow(x, y - (nobar.getValue() ? 0 : 10), getWidth(), height2 + 2 + (nobar.getValue() ? 0 : 10));

            RenderUtil.drawBlurRect(x, y, getWidth(), height2 + 2, new Color(0, 0, 0, HUD.hudalpha.getValue().intValue()).getRGB());
            String s3 = scoreobjective1.getDisplayName();
            this.drawCenteredString(mc.fontRendererObj, s3, x + (getWidth() / 2), y + 3, 0xffffffff);


            for (int i = 0; i < arraylist1.size(); i++) {
                Score score1 = arraylist1.get(arraylist1.size() - i - 1);
                ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
                String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());

                for (Mapper m : CommandScoreboard.mappers) {
                    s1 = s1.replaceAll(m.getKey(), m.getValue());
                }

                width = Math.max(mc.fontRendererObj.getStringWidth(s1), width);
                mc.fontRendererObj.drawString(s1, x + 4, y + (height += 10), 0xffffffff);
            }

            setWidth((int) Math.max(mc.fontRendererObj.getStringWidth(s3), width) + 10);
        }
    }
}
