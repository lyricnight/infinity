package me.lyric.infinity.impl.modules.render;

import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.render.crystal.RenderCrystalPreEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;

/**
 * @author lyricccc
 */
public class CrystalModifier extends Module {

    public CrystalModifier(){
        super("CrystalModifier", "Changes things about crystal rendering.", Category.RENDER);
    }
    public Setting<Float> spinSpeed = register(new Setting<>("SpinSpeed", "Changes spin speed of crystal.", 1.0f, 0f, 100f));
    public final Setting<Float> bounceFactor = register(new Setting<>("BounceFactor","Factor for bounce of crystal.", 1.0f, 0.0f, 100f));

    @EventListener
    public void onRenderCrystalPre(RenderCrystalPreEvent e)
    {
        e.cancel();
        e.getModelBase().render(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount() * spinSpeed.getValue(), e.getAgeInTicks() * bounceFactor.getValue(), e.getNetHeadYaw(), e.getHeadPitch(), e.getScaleFactor());
    }
}
