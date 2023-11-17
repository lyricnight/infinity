package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.mixin.mixins.accessors.IEntity;
import net.minecraftforge.client.GuiIngameForge;

/**
 * @author lyric
 * top ten useful modules
 */
@ModuleInformation(name = "Portals", description = "Changes portal stuff.", category = Category.Render)
public class Portals extends Module {

    public BooleanSetting gui = createSetting("GUI", true);

    public BooleanSetting noRender = createSetting("Render", true);

    private boolean renderPortal = false;

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
