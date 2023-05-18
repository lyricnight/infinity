package me.lyric.infinity.impl.modules.misc;

import com.mojang.authlib.GameProfile;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.Map;

/**
 * @author lyric
 * this is weird
 */
public class FakePlayer extends Module {
    public Setting<String> username = register(new Setting<Object>("Name","The name of the FakePlayer.", "lyric"));
    public FakePlayer() {
        super("FakePlayer","Creates FakePlayer for testing.", Category.MISC);
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

        mc.world.addEntityToWorld(-4201337, (Entity)fake);
    }

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        mc.world.removeEntityFromWorld(-4201337);
    }
}
