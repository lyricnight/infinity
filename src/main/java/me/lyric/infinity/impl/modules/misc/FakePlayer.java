package me.lyric.infinity.impl.modules.misc;

import com.mojang.authlib.GameProfile;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.StringSetting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.Map;

/**
 * @author lyric
 */
@ModuleInformation(name = "FakePlayer", description = "testing things", category = Category.Misc)
public class FakePlayer extends Module {
    public StringSetting username = createSetting("Name","real");
    @Override
    public void onLogout()
    {
        disable();
    }
    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            this.disable();
            return;
        }
        EntityOtherPlayerMP fake = new EntityOtherPlayerMP((World)mc.world, new GameProfile(mc.session.getProfile().getId(), username.getValue()));
        fake.copyLocationAndAnglesFrom(mc.player);
        fake.inventory.copyInventory(mc.player.inventory);
        for (Map.Entry<Potion,PotionEffect> entry : mc.player.activePotionsMap.entrySet()) {
            fake.addPotionEffect(entry.getValue());
        }

        mc.world.addEntityToWorld(-4201337, (Entity) fake);
    }

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        mc.world.removeEntityFromWorld(-4201337);
    }
    @Override
    public String getDisplayInfo()
    {
        if(!nullSafe()) return "";
        return username.getValue().toLowerCase();
    }
}
