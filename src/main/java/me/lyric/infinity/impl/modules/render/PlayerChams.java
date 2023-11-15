package me.lyric.infinity.impl.modules.render;

import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerChams extends Module {
    public PlayerChams()
    {
        super("PlayerChams", "Normal chams for players", Category.RENDER);
    }
    public BooleanSetting self = createSetting("Self", "Renders yourself", false));

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
