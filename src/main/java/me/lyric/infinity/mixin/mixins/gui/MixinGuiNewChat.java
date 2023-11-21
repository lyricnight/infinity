package me.lyric.infinity.mixin.mixins.gui;

import com.google.common.collect.Lists;
import jdk.nashorn.internal.ir.IfNode;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.util.gl.RenderUtils;
import me.lyric.infinity.api.util.gl.Stencil;
import me.lyric.infinity.api.util.metadata.MathUtils;
import me.lyric.infinity.impl.modules.client.Internals;
import me.lyric.infinity.impl.modules.misc.BetterChat;
import me.lyric.infinity.mixin.transformer.IMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Timer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = GuiNewChat.class)
public abstract class MixinGuiNewChat extends MixinGui {

    //these vars may not be public, but private, check them first if this doesn't work.

    @Final
    @Shadow
    public final Minecraft mc = Minecraft.getMinecraft();

    @Shadow
    @Final
    public List<ChatLine> drawnChatLines;

    @Shadow
    public int scrollPos;

    @Shadow
    public boolean isScrolled;

    @Shadow
    public abstract int getLineCount();

    @Shadow
    public abstract boolean getChatOpen();

    @Shadow
    public abstract float getChatScale();

    @Shadow
    public abstract int getChatWidth();

    @Redirect(method = {"drawChat"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    public void drawChatHook1(int left, int top, int right, int bottom, int color) {
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(BetterChat.class).isEnabled() && Infinity.INSTANCE.moduleManager.getModuleByClass(BetterChat.class).rect.getValue()) {
            Gui.drawRect(left, top, right, bottom, 0);
        } else {
            Gui.drawRect(left, top, right, bottom, color);
        }

    }

    @Redirect(method = {"setChatLine"}, at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0, remap = false))
    public int drawnChatLinesSize(List<ChatLine> list) {
        return Infinity.INSTANCE.moduleManager.getModuleByClass(BetterChat.class).isEnabled() && Infinity.INSTANCE.moduleManager.getModuleByClass(BetterChat.class).inf.getValue() ? -2147483647 : list.size();
    }

    @Redirect(method = {"setChatLine"}, at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 2, remap = false))
    public int chatLinesSize(List<ChatLine> list) {
        return Infinity.INSTANCE.moduleManager.getModuleByClass(BetterChat.class).isEnabled() && Infinity.INSTANCE.moduleManager.getModuleByClass(BetterChat.class).inf.getValue() ? -2147483647 : list.size();
    }


    @Inject(method = { "drawChat" }, at =  @At("HEAD"), cancellable = true)
    private void drawChat(final int updateCounter, final CallbackInfo ci) {
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(BetterChat.class).isEnabled() && Infinity.INSTANCE.moduleManager.getModuleByClass(Internals.class).cfont.getValue()) {
            if (mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
                ScaledResolution scaledresolution = new ScaledResolution(mc);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GlStateManager.translate(0.0f, (float)(scaledresolution.getScaledHeight() - 60), 0.0f);
                int maxLineCount = getLineCount();
                boolean isChatOpen = false;
                int j = 0;
                int lineCount = this.drawnChatLines.size();
                int fontHeight = Infinity.INSTANCE.fontManager.getHeight("exampletext");
                if (lineCount > 0) {
                    if (getChatOpen()) {
                        isChatOpen = true;
                    }
                    float scale = getChatScale();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(2.0f, 20.0f, 0.0f);
                    GlStateManager.scale(scale, scale, 1.0f);
                    int scaledWidth = MathHelper.ceil(this.getChatWidth() / scale);
                    float x = 0.0f;
                    float y = 0.0f;
                    boolean render = false;
                    for (int i = 0; i + this.scrollPos < this.drawnChatLines.size() && i < maxLineCount; ++i) {
                        final ChatLine chatline = this.drawnChatLines.get(i + this.scrollPos);
                        if (chatline != null && (updateCounter - chatline.getUpdatedCounter() < 200 || isChatOpen)) {
                            render = true;
                            if (!isChatOpen && updateCounter - chatline.getUpdatedCounter() > 195) {
                                float percent = 1.0f - (updateCounter - chatline.getUpdatedCounter() + ((IMinecraft)mc).getTimer().renderPartialTicks - 195.0f) / 5.0f;
                                percent = MathUtils.clamp(percent, 0.0f, 1.0f);
                                y -= fontHeight * percent;
                            }
                            else {
                                y -= fontHeight;
                            }
                        }
                    }
                    if (render) {
                        Stencil.initStencil();
                        Stencil.bindWriteStencilBuffer();
                        if (!Infinity.INSTANCE.moduleManager.getModuleByClass(BetterChat.class).rect.getValue())
                        {
                            RenderUtils.drawRoundedRect(x - 2.0f, y, x + scaledWidth + 4.0f, 1.0, 5.0, Color.white.getRGB());
                        }
                        GL11.glPopMatrix();
                        GL11.glPopMatrix();
                        Stencil.bindReadStencilBuffer(1);
                        GL11.glPushMatrix();
                        GlStateManager.translate(0.0f, (float)(scaledresolution.getScaledHeight() - 60), 0.0f);
                        GL11.glPushMatrix();
                        GlStateManager.translate(2.0f, 20.0f, 0.0f);
                        GlStateManager.scale(scale, scale, 1.0f);
                        if (!Infinity.INSTANCE.moduleManager.getModuleByClass(BetterChat.class).rect.getValue())
                        {
                            RenderUtils.drawRoundedRect(x - 2.0f, y, x + scaledWidth + 4.0f, 1.0, 5.0, new Color(20, 20, 20, 60).getRGB());

                        }
                    }
                    for (int i = 0; i + this.scrollPos < this.drawnChatLines.size() && i < maxLineCount; ++i) {
                        final ChatLine chatline = this.drawnChatLines.get(i + this.scrollPos);
                        if (chatline != null) {
                            int j2 = updateCounter - chatline.getUpdatedCounter();
                            if (j2 < 200 || isChatOpen) {
                                ++j;
                                final int left = 0;
                                final int top = -i * fontHeight;
                                final String text = chatline.getChatComponent().getFormattedText();
                                GlStateManager.enableBlend();
                                Infinity.INSTANCE.fontManager.drawString(text, (float)left, (float)(top - (fontHeight - 2.3)), Color.white.getRGB(), true);
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                    if (render) {
                        Stencil.uninitStencil();
                    }
                    if (isChatOpen) {
                        GlStateManager.translate(-3.0f, 0.0f, 0.0f);
                        fontHeight = Infinity.INSTANCE.fontManager.getHeight("exampletext");
                        int l2 = lineCount * fontHeight + lineCount;
                        int i2 = j * fontHeight + j;
                        int j3 = this.scrollPos * i2 / lineCount;
                        int k2 = i2 * i2 / l2;
                        if (l2 != i2) {
                            int opacity = (j3 > 0) ? 170 : 96;
                            int l3 = this.isScrolled ? 13382451 : 3355562;
                            MixinGuiNewChat.drawRect(0, -j3, 2, -j3 - k2, l3 + (opacity << 24));
                            MixinGuiNewChat.drawRect(2, -j3, 1, -j3 - k2, 13421772 + (opacity << 24));
                        }
                    }
                    GlStateManager.popMatrix();
                }
            }
            ci.cancel();
        }
    }

    /**
     * @author lyric
     * @reason to use customchat
     */

    @Nullable
    @Overwrite
    public ITextComponent getChatComponent(int p_146236_1_, int p_146236_2_) {
        if (!getChatOpen()) {
            return null;
        }
        ScaledResolution scaledresolution = new ScaledResolution(mc);
        int i = scaledresolution.getScaleFactor();
        float f = this.getChatScale();
        int j = p_146236_1_ / i - 3;
        int k = p_146236_2_ / i - 27;
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(Internals.class).cfont.getValue() && Infinity.INSTANCE.moduleManager.getModuleByClass(BetterChat.class).isEnabled()) {
            k -= 12;
        }
        j = MathHelper.floor(j / f);
        k = MathHelper.floor(k / f);
        if (j < 0 || k < 0) {
            return null;
        }
        int l = Math.min(this.getLineCount(), this.drawnChatLines.size());
        if (j <= MathHelper.floor(this.getChatWidth() / this.getChatScale()) && k < Infinity.INSTANCE.fontManager.getHeight("Exampletext") * l + l) {
            int i2 = k / Infinity.INSTANCE.fontManager.getHeight("Exampletext") + this.scrollPos;
            if (i2 >= 0 && i2 < this.drawnChatLines.size()) {
                ChatLine chatline = this.drawnChatLines.get(i2);
                int j2 = 0;
                for (ITextComponent ichatcomponent : chatline.getChatComponent()) {
                    if (ichatcomponent instanceof TextComponentString) {
                        j2 += (int)(Infinity.INSTANCE.fontManager.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(((TextComponentString)ichatcomponent).getText(), false)));
                        if (j2 > j) {
                            return ichatcomponent;
                        }
                        continue;
                    }
                }
            }
            return null;
        }
        return null;
    }
    @Redirect(method = { "setChatLine" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiUtilRenderComponents;splitText(Lnet/minecraft/util/text/ITextComponent;ILnet/minecraft/client/gui/FontRenderer;ZZ)Ljava/util/List;"))
    private List<ITextComponent> onFunc(ITextComponent k, int s1, FontRenderer chatcomponenttext, boolean l, boolean chatcomponenttext2) {
        return (Infinity.INSTANCE.moduleManager.getModuleByClass(Internals.class).cfont.getValue() && Infinity.INSTANCE.moduleManager.getModuleByClass(BetterChat.class).isEnabled()) ? this.wrapToLen(k, s1, chatcomponenttext) : GuiUtilRenderComponents.splitText(k, s1, chatcomponenttext, l, chatcomponenttext2);
    }

    private List<ITextComponent> wrapToLen(ITextComponent p_178908_0_, int p_178908_1_, FontRenderer p_178908_2_) {
        int i = 0;
        ITextComponent ichatcomponent = new TextComponentString("");
        List<ITextComponent> list = new ArrayList<>();
        List<ITextComponent> list2 = (List<ITextComponent>) Lists.newArrayList((Iterable)p_178908_0_);
        for (int j = 0; j < list2.size(); ++j) {
            ITextComponent ichatcomponent2 = list2.get(j);
            String s = ichatcomponent2.getUnformattedComponentText();
            boolean flag = false;
            if (s.contains("\n")) {
                int k = s.indexOf(10);
                String s2 = s.substring(k + 1);
                s = s.substring(0, k + 1);
                TextComponentString chatcomponenttext = new TextComponentString(s2);
                chatcomponenttext.setStyle(ichatcomponent2.getStyle().createShallowCopy());
                list2.add(j + 1, chatcomponenttext);
                flag = true;
            }
            String s3 = GuiUtilRenderComponents.removeTextColorsIfConfigured(ichatcomponent2.getStyle().getFormattingCode() + s, false);
            String s4 = s3.endsWith("\n") ? s3.substring(0, s3.length() - 1) : s3;
            double i2 = Infinity.INSTANCE.fontManager.getStringWidth(s4);
            TextComponentString chatcomponenttext2 = new TextComponentString(s4);
            chatcomponenttext2.setStyle(ichatcomponent2.getStyle().createShallowCopy());
            if (i + i2 > p_178908_1_) {
                String s5 = Infinity.INSTANCE.fontManager.renderer.wrapWords(s3, p_178908_1_ - i).toString();
                String s6 = (s5.length() < s3.length()) ? s3.substring(s5.length()) : null;
                if (s6 != null && s6.length() > 0) {
                    int l = s5.lastIndexOf(" ");
                    if (l >= 0 && Infinity.INSTANCE.fontManager.getStringWidth(s3.substring(0, l)) > 0.0) {
                        s5 = s3.substring(0, l);
                        s6 = s3.substring(l);
                    }
                    else if (i > 0 && !s3.contains(" ")) {
                        s5 = "";
                        s6 = s3;
                    }
                    s6 = FontRenderer.getFormatFromString(s5) + s6;
                    TextComponentString chatcomponenttext3 = new TextComponentString(s6);
                    chatcomponenttext3.setStyle(ichatcomponent2.getStyle().createShallowCopy());
                    list2.add(j + 1, chatcomponenttext3);
                }
                i2 = Infinity.INSTANCE.fontManager.getStringWidth(s5);
                chatcomponenttext2 = new TextComponentString(s5);
                chatcomponenttext2.setStyle(ichatcomponent2.getStyle().createShallowCopy());
                flag = true;
            }
            if (i + i2 <= p_178908_1_) {
                i += (int)i2;
                ichatcomponent.appendSibling(chatcomponenttext2);
            }
            else {
                flag = true;
            }
            if (flag) {
                list.add(ichatcomponent);
                i = 0;
                ichatcomponent = new TextComponentString("");
            }
        }
        list.add(ichatcomponent);
        return list;
    }



}