package me.lyric.infinity.mixin.mixins.entity;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.impl.modules.player.Delays;
import me.lyric.infinity.impl.modules.render.Swing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value={EntityLivingBase.class})
public class MixinEntityLivingBase {

    @Final
    private final Minecraft mc = Minecraft.getMinecraft();

    @Shadow
    public int activeItemStackUseCount;
    @Shadow
    public ItemStack activeItemStack;

    @Inject(method={"getArmSwingAnimationEnd"}, at={@At(value="HEAD")}, cancellable=true)
    private void getArmSwingAnimationEnd(CallbackInfoReturnable<Integer> info) {

        Swing mod = Swing.INSTANCE;

        if (mod.isEnabled() && mod.slowSwing.getValue()) {
            info.setReturnValue(15);

        }
    }
    @Redirect(method = "onItemUseFinish", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;resetActiveHand()V"))
    public void resetActiveHandHook(EntityLivingBase base) {
        if (mc.world.isRemote && Infinity.INSTANCE.moduleManager.getModuleByClass(Delays.class).eat.getValue() && base instanceof EntityPlayerSP && !mc.isSingleplayer() && this.activeItemStack.getItem() instanceof ItemFood) {
            this.activeItemStackUseCount = 0;
            ((EntityPlayerSP) base).connection.sendPacket(new CPacketPlayerTryUseItem(base.getActiveHand()));
        } else {
            base.resetActiveHand();
        }
    }
}
