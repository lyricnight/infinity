package me.lyric.infinity.impl.modules.combat;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.minecraft.CombatUtil;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.manager.client.InteractionManager;

/**
 * @author lyric (o_o)
 */

public class AntiCev extends Module {
    public AntiCev() {
        super("AntiCev", "Prevents cevbreaker.", Category.COMBAT);
    }

    public Setting<Boolean> attack = register(new Setting<>("Attack","Attacks crystals to place.", true));
    public Setting<Boolean> rot = register(new Setting<>("Rotate", "Rotations.", false));
    public Setting<Boolean> packet = register(new Setting<>("Packet Rotations", "Uses packet rotations.", true).withParent(rot));

    @Override
    public void onUpdate()
    {
        if (mc.player == null)
        {
            return;
        }
        if (CombatUtil.isBlockAbovePlayerHead())
        {
            ChatUtils.sendMessage("Reached block place func.");
            InteractionManager.placeBlock(CombatUtil.getAntiCevPlacement(), rot.getValue(), packet.getValue(), attack.getValue(), true);
        }
    }
}
