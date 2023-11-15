package me.lyric.infinity.impl.modules.misc;

import com.mojang.authlib.GameProfile;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.Map;

/**
 * @author lyric
 */
@ModuleInformation(getName = "FakePlayer", getDescription = "testing things", category = Category.Misc)
public class FakePlayer extends Module {
    public StringSetting username = register(new Setting<Object>("Name","The name of the FakePlayer.", "real"));
    @Override
    public void onLogout()
    {
        toggle();
    }
    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            this.toggle();
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
