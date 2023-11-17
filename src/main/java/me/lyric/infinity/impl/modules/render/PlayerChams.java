package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInformation(name = "PlayerChams", description = "See people thru walls.", category = Category.Render)
public class PlayerChams extends Module {
    public BooleanSetting self = createSetting("Self", false);

    public boolean isValid(Entity entity)
    {
        Entity renderEntity = getEntity();
        if (entity == null)
        {
            return false;
        }
        else if (!self.getValue() && entity.equals(renderEntity))
        {
            return false;
        }
        else if (entity instanceof EntityPlayer)
        {
            return true;
        }
        else
        {
            return true;
        }
    }
    public static Entity getEntity()
    {
        return mc.getRenderViewEntity() == null ? mc.player : mc.getRenderViewEntity();
    }

}
