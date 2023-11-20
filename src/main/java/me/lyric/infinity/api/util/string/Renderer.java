package me.lyric.infinity.api.util.string;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Renderer extends CFont {
    private static final String COLOR_CODES = "0123456789abcdefklmnorzy+-";
    private static final Random CHAR_RANDOM = new Random();
    final boolean SHADOW = true;
    private static final List<Character> RANDOM_CHARS = new ArrayList<>("ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000".chars().mapToObj(c -> Character.valueOf((char)c)).collect(Collectors.toList()));
    protected CFont.CharData[] boldChars = new CFont.CharData[256];
    protected CFont.CharData[] italicChars = new CFont.CharData[256];
    protected CFont.CharData[] boldItalicChars = new CFont.CharData[256];
    protected DynamicTexture texBold;
    protected DynamicTexture texItalic;
    protected DynamicTexture texBoth;
    private final int[] colorCode = new int[32];

    public Renderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        this.setupMinecraftColorcodes();
        this.setupBoldItalicIDs();
    }

    public float drawStringWithShadow(String text, double x, double y, int color) {
        float shadowWidth = this.drawString(text, x + 1.0, y + 1.0, color, true);
        return Math.max(shadowWidth, this.drawString(text, x, y, color, false));
    }

    public float drawString(String text, float x, float y, int color) {
        return this.drawString(text, x, y, color, false);
    }

    public float drawCenteredString(String text, float x, float y, int color) {
        return this.drawString(text, x - (float)this.getStringWidth(text) / 2.0f, y, color);
    }

    public float drawCenteredStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, (double)(x - (float)this.getStringWidth(text) / 2.0f) + 1.0, (double)y + 1.0, color, true);
        return this.drawString(text, x - (float)this.getStringWidth(text) / 2.0f, y, color);
    }

    public float drawString(String text, double x, double y, int color, boolean shadow) {
        x -= 1.0;
        if (text == null) {
            return 0.0f;
        }
        if (shadow) {
            x -= 0.4;
            y -= 0.4;
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }
        CFont.CharData[] currentData = this.charData;
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        boolean random = false;
        boolean bold = false;
        boolean italic = false;
        boolean strike = false;
        boolean underline = false;
        boolean rainbowP = false;
        boolean rainbowM = false;
        x *= 2.0;
        y = (y - 3.0) * 2.0;
        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color((float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, alpha);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(this.tex.getGlTextureId());
        GL11.glBindTexture(3553, this.tex.getGlTextureId());
        for (int i = 0; i < text.length(); ++i) {
            char character = text.charAt(i);
            if (character == '§' && i + 1 < text.length()) {
                int colorIndex = COLOR_CODES.indexOf(text.charAt(i + 1));
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    random = false;
                    underline = false;
                    strike = false;
                    rainbowP = false;
                    rainbowM = false;
                    GlStateManager.bindTexture(this.tex.getGlTextureId());
                    currentData = this.charData;
                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }
                    if (shadow) {
                        colorIndex += 16;
                    }
                    int colorcode = this.colorCode[colorIndex];
                    GlStateManager.color((float)(colorcode >> 16 & 0xFF) / 255.0f, (float)(colorcode >> 8 & 0xFF) / 255.0f, (float)(colorcode & 0xFF) / 255.0f, alpha);
                } else if (colorIndex == 16) {
                    random = true;
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        GlStateManager.bindTexture(this.texBoth.getGlTextureId());
                        currentData = this.boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(this.texBold.getGlTextureId());
                        currentData = this.boldChars;
                    }
                } else if (colorIndex == 18) {
                    strike = true;
                } else if (colorIndex == 19) {
                    underline = true;
                } else if (colorIndex == 20) {
                    italic = true;
                    if (bold) {
                        GlStateManager.bindTexture(this.texBoth.getGlTextureId());
                        currentData = this.boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(this.texItalic.getGlTextureId());
                        currentData = this.italicChars;
                    }
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    random = false;
                    underline = false;
                    strike = false;
                    rainbowP = false;
                    rainbowM = false;
                    GlStateManager.color((float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, alpha);
                    GlStateManager.bindTexture(this.tex.getGlTextureId());
                    currentData = this.charData;
                } else {
                    if (colorIndex == 22) {
                        int colorcode;
                        bold = false;
                        italic = false;
                        random = false;
                        underline = false;
                        strike = false;
                        rainbowP = false;
                        rainbowM = false;
                        GlStateManager.bindTexture(this.tex.getGlTextureId());
                        currentData = this.charData;
                        char[] h = new char[8];
                        if (i + 9 < text.length()) {
                            for (int j = 0; j < 8; ++j) {
                                h[j] = text.charAt(i + j + 2);
                            }
                        } else {
                            ++i;
                            continue;
                        }
                        try {
                            colorcode = (int)Long.parseLong(new String(h), 16);
                        }
                        catch (Exception e) {
                            continue;
                        }
                        GlStateManager.color((float)(colorcode >> 16 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1), (float)(colorcode >> 8 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1), (float)(colorcode & 0xFF) / 255.0f / (float)(shadow ? 4 : 1), (float)(colorcode >> 24 & 0xFF) / 255.0f);
                        i += 9;
                        continue;
                    }
                    if (colorIndex == 23) {
                        bold = false;
                        italic = false;
                        random = false;
                        underline = false;
                        strike = false;
                        rainbowP = false;
                        rainbowM = false;
                        GlStateManager.bindTexture(this.tex.getGlTextureId());
                        currentData = this.charData;
                        int rainbow = Color.HSBtoRGB(1.0f, 1.0f, 1.0f);
                        GlStateManager.color((float)(rainbow >> 16 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1), (float)(rainbow >> 8 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1), (float)(rainbow & 0xFF) / 255.0f / (float)(shadow ? 4 : 1), alpha);
                    } else if (colorIndex == 24) {
                        bold = false;
                        italic = false;
                        random = false;
                        underline = false;
                        strike = false;
                        rainbowP = true;
                        rainbowM = false;
                        GlStateManager.bindTexture(this.tex.getGlTextureId());
                        currentData = this.charData;
                    } else {
                        bold = false;
                        italic = false;
                        random = false;
                        underline = false;
                        strike = false;
                        rainbowP = false;
                        rainbowM = true;
                        GlStateManager.bindTexture(this.tex.getGlTextureId());
                        currentData = this.charData;
                    }
                }
                ++i;
                continue;
            }
            if (character >= currentData.length) continue;
            if (random) {
                int w = currentData[character].width;
                CFont.CharData[] finalCurrentData = currentData;
                List<Character> randoms = RANDOM_CHARS.stream().filter(c -> {
                    if (c.charValue() < finalCurrentData.length) {
                        return finalCurrentData[c.charValue()].width == w;
                    }
                    return false;
                }).collect(Collectors.toList());
                if (randoms.size() != 0) {
                    character = randoms.get(CHAR_RANDOM.nextInt(randoms.size()));
                }
            }
            if (rainbowP || rainbowM) {
                int rainbow = Color.HSBtoRGB(1.0f, 1.0f, 1.0f);
                GlStateManager.color((float)(rainbow >> 16 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1), (float)(rainbow >> 8 & 0xFF) / 255.0f / (float)(shadow ? 4 : 1), (float)(rainbow & 0xFF) / 255.0f / (float)(shadow ? 4 : 1), alpha);
            }
            GL11.glBegin(4);
            this.drawChar(currentData, character, (float)x, (float)y);
            GL11.glEnd();
            if (strike) {
                this.drawLine(x, y + (double)currentData[character].height / 2.0, x + (double)currentData[character].width - 8.0, y + (double)currentData[character].height / 2.0);
            }
            if (underline) {
                this.drawLine(x, y + (double)currentData[character].height - 2.0, x + (double)currentData[character].width - 8.0, y + (double)currentData[character].height - 2.0);
            }
            x += (double)(currentData[character].width - 8 + this.charOffset);
        }
        GL11.glHint(3155, 4352);
        GL11.glPopMatrix();
        return (float)(x / 2.0);
    }

    @Override
    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }
        CFont.CharData[] currentData = this.charData;
        int width = 0;
        boolean bold = false;
        boolean italic = false;
        for (int i = 0; i < text.length(); ++i) {
            char character = text.charAt(i);
            if (character == '§' && i + 1 < text.length()) {
                int colorIndex = COLOR_CODES.indexOf(text.charAt(i + 1));
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                } else if (colorIndex == 17) {
                    bold = true;
                    currentData = italic ? this.boldItalicChars : this.boldChars;
                } else if (colorIndex == 20) {
                    italic = true;
                    currentData = bold ? this.boldItalicChars : this.italicChars;
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    currentData = this.charData;
                } else {
                    if (colorIndex == 22) {
                        bold = false;
                        italic = false;
                        currentData = this.charData;
                        i += 9;
                        continue;
                    }
                    if (colorIndex == 23) {
                        bold = false;
                        italic = false;
                    } else if (colorIndex == 24) {
                        bold = false;
                        italic = false;
                    } else {
                        bold = false;
                        italic = false;
                    }
                }
                ++i;
                continue;
            }
            if (character >= currentData.length) continue;
            width += currentData[character].width - 8 + this.charOffset;
        }
        return width / 2;
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.setupBoldItalicIDs();
    }

    @Override
    public void setAntiAlias(boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        this.setupBoldItalicIDs();
    }

    @Override
    public void setFractionalMetrics(boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        this.setupBoldItalicIDs();
    }

    private void setupBoldItalicIDs() {
        this.texBold = this.setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
        this.texItalic = this.setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
        this.texBoth = this.setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
    }

    private void drawLine(double x, double y, double x1, double y1) {
        GL11.glDisable(3553);
        GL11.glLineWidth(1.0f);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GL11.glEnable(3553);
    }

    public List<String> wrapWords(String text, double width) {
        ArrayList<String> result = new ArrayList<String>();
        if ((double)this.getStringWidth(text) > width) {
            String[] words = text.split(" ");
            StringBuilder current = new StringBuilder();
            char lastColorCode = '￿';
            for (String word : words) {
                char[] array = word.toCharArray();
                for (int i = 0; i < array.length; ++i) {
                    char c = array[i];
                    if (c != '§' || i + 1 >= array.length) continue;
                    lastColorCode = array[i + 1];
                }
                StringBuilder stringBuilder = new StringBuilder();
                if ((double)this.getStringWidth(stringBuilder.append((Object)current).append(word).append(" ").toString()) < width) {
                    current.append(word).append(" ");
                    continue;
                }
                result.add(current.toString());
                current = new StringBuilder("§").append(lastColorCode).append(word).append(" ");
            }
            if (current.length() > 0) {
                if ((double)this.getStringWidth(current.toString()) < width) {
                    result.add("§" + lastColorCode + current + " ");
                } else {
                    result.addAll(this.formatString(current.toString(), width));
                }
            }
        } else {
            result.add(text);
        }
        return result;
    }

    public List<String> formatString(String string, double width) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        char lastColorCode = '￿';
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (c == '§' && i < chars.length - 1) {
                lastColorCode = chars[i + 1];
            }
            StringBuilder stringBuilder = new StringBuilder();
            if ((double)this.getStringWidth(stringBuilder.append(current.toString()).append(c).toString()) < width) {
                current.append(c);
                continue;
            }
            result.add(current.toString());
            current = new StringBuilder("§").append(lastColorCode).append(c);
        }
        if (current.length() > 0) {
            result.add(current.toString());
        }
        return result;
    }

    private void setupMinecraftColorcodes() {
        for (int i = 0; i < 32; ++i) {
            int o = (i >> 3 & 1) * 85;
            int r = (i >> 2 & 1) * 170 + o;
            int g = (i >> 1 & 1) * 170 + o;
            int b = (i & 1) * 170 + o;
            if (i == 6) {
                r += 85;
            }
            if (i >= 16) {
                r /= 4;
                g /= 4;
                b /= 4;
            }
            this.colorCode[i] = (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
        }
    }
}


