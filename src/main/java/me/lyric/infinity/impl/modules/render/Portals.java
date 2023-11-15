package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.mixin.mixins.accessors.IEntity;
import net.minecraftforge.client.GuiIngameForge;

/**
 * @author lyric
 * top ten useful modules
 */

public class Portals extends Module {

    public BooleanSetting gui = createSetting("GUI", "Allows opening guis in portal.", true));

    public BooleanSetting noRender = createSetting("Render", "Cancels portal rendering.", true));

    private boolean renderPortal = false;
    public Portals()
    {
        super("Portals", "Handles things about portals.", Category.RENDER);
    }

    @Override
    public void onEnable() {
        renderPortal = GuiIngameForge.renderPortal;
    }

    @Override
    public void onDisable() {
        GuiIngameForge.renderPortal = renderPortal;
    }

    @Override
    public void onUpdate() {
        if(mc.player == null || mc.world == null) return;
        if (gui.getValue()) {
            ((IEntity) mc.player).setInPortal(false);
        }
        if (noRender.getValue()) {
            GuiIngameForge.renderPortal = false;
        }
    }
}
