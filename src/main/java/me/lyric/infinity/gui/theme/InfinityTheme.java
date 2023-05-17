package me.lyric.infinity.gui.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntSupplier;



public class InfinityTheme extends ThemeBase {

    /**
     * Alias map.
     */
    protected final Map<String, Integer> icons = new HashMap<String, Integer>();
    /**
     * The font size.
     */
    protected int height;
    /**
     * The text padding
     */
    protected int padding;
    /**
     * The component border.
     */
    protected int border;
    /**
     * Scroll bar width.
     */
    protected int scroll;
    /**
     * The rainbow gradient.
     */
    protected IntSupplier gradient;
    /**
     * Images
     */
    protected int background, combat, exploits, misc, movement, player, render;

    /**
     * Constructor.
     *
     * @param scheme   the color scheme to be used
     * @param height   the font size
     * @param padding  the text padding
     * @param border   the component border
     * @param scroll   the scroll bar width
     * @param gradient the rainbow gradient
     */
    public InfinityTheme(IColorScheme scheme, int height, int padding, int border, int scroll, IntSupplier gradient) {
        super(scheme);
        this.height = height;
        this.padding = padding;
        this.border = border;
        this.scroll = scroll;
        this.gradient = gradient;
        scheme.createSetting(this, "Title Color", "The color for panel titles.", true, true, new Color(255, 128, 0), true);
        scheme.createSetting(this, "Enabled Color", "The color for enabled modules.", false, true, new Color(136, 180, 52), false);
        scheme.createSetting(this, "Disabled Color", "The color for disabled modules.", false, true, new Color(180, 52, 52), false);
        scheme.createSetting(this, "Inactive Color", "The primary color for modules.", false, true, new Color(72, 72, 72), false);
        scheme.createSetting(this, "Background Color", "The panel background color.", false, true, new Color(16, 16, 16), false);
        scheme.createSetting(this, "Primary Outline Color", "The main color for outlines.", false, true, new Color(10, 10, 10), false);
        scheme.createSetting(this, "Secondary Outline Color", "The auxiliary color for outlines.", false, true, new Color(40, 40, 40), false);
        scheme.createSetting(this, "Tertiary Outline Color", "The color for resize borders.", false, true, new Color(60, 60, 60), false);
        scheme.createSetting(this, "Active Font Color", "The color for active text.", false, true, new Color(192, 192, 192), false);
        scheme.createSetting(this, "Inactive Font Color", "The color for inactive text.", false, true, new Color(96, 96, 96), false);
        scheme.createSetting(this, "Scroll Bar Color", "The color for scroll bars.", false, true, new Color(64, 64, 64), false);
        scheme.createSetting(this, "Text Field Color", "The color for text fields.", false, true, new Color(32, 32, 32), false);
        scheme.createSetting(this, "Highlight Color", "The color for highlighted text.", false, true, new Color(0, 0, 255), false);
        scheme.createSetting(this, "Tooltip Color", "The color for description tooltips.", false, true, new Color(0, 0, 0, 128), false);
    }

    /**
     * Makes a color darker.
     *
     * @param a the original input color
     * @return the darker output color
     */
    protected static Color darker(Color a) {
        return new Color(a.getRed() / 2, a.getGreen() / 2, a.getBlue() / 2);
    }

    @Override
    public void loadAssets(IInterface inter) {
        background = inter.loadImage("background.png");
        combat = inter.loadImage("combat.png");
        icons.put("COMBAT", combat);
        exploits = inter.loadImage("exploits.png");
        icons.put("CLIENT", exploits);
        misc = inter.loadImage("misc.png");
        icons.put("MISC", misc);
        movement = inter.loadImage("movement.png");
        icons.put("MOVEMENT", movement);
        player = inter.loadImage("player.png");
        icons.put("PLAYER", player);
        render = inter.loadImage("render.png");
        icons.put("RENDER", render);
    }

    /**
     * Function to render background.
     *
     * @param context the context to be used
     * @param focus   the focus state
     */
    protected void renderBackground(Context context, boolean focus) {
        Rectangle rect = context.getRect();
        int x = (int) Math.ceil(rect.width / 256.0);
        int y = (int) Math.ceil(rect.height / 256.0);
        context.getInterface().window(rect);
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                context.getInterface().drawImage(new Rectangle(rect.x + i * 256, rect.y + j * 256, 256, 256), 0, false, background);
            }
        }
        context.getInterface().restore();
    }

    /**
     * Function to render small buttons.
     *
     * @param context the context to be used
     * @param title   the component title
     * @param symbol  the icon ID to be used
     * @param focus   the focus state
     */
    protected void renderSmallButton(Context context, String title, int symbol, boolean focus) {
        Point[] points = new Point[3];
        int padding = context.getSize().height <= 2 * this.padding ? 2 : this.padding;
        Rectangle rect = new Rectangle(context.getPos().x + padding / 2, context.getPos().y + padding / 2, context.getSize().height - 2 * (padding / 2), context.getSize().height - 2 * (padding / 2));
        if (title == null) rect.x += context.getSize().width / 2 - context.getSize().height / 2;
        Color color = getFontColor(focus);
        switch (symbol) {
            case ITheme.CLOSE:
                context.getInterface().drawLine(new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), color, color);
                context.getInterface().drawLine(new Point(rect.x, rect.y + rect.height), new Point(rect.x + rect.width, rect.y), color, color);
                break;
            case ITheme.MINIMIZE:
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height - 2, rect.width, 2), color, color, color, color);
                break;
            case ITheme.ADD:
                if (rect.width % 2 == 1) rect.width -= 1;
                if (rect.height % 2 == 1) rect.height -= 1;
                context.getInterface().fillRect(new Rectangle(rect.x + rect.width / 2 - 1, rect.y, 2, rect.height), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height / 2 - 1, rect.width, 2), color, color, color, color);
                break;
            case ITheme.LEFT:
                if (rect.height % 2 == 1) rect.height -= 1;
                points[2] = new Point(rect.x + rect.width, rect.y);
                points[1] = new Point(rect.x + rect.width, rect.y + rect.height);
                points[0] = new Point(rect.x, rect.y + rect.height / 2);
                break;
            case ITheme.RIGHT:
                if (rect.height % 2 == 1) rect.height -= 1;
                points[0] = new Point(rect.x, rect.y);
                points[1] = new Point(rect.x, rect.y + rect.height);
                points[2] = new Point(rect.x + rect.width, rect.y + rect.height / 2);
                break;
            case ITheme.UP:
                if (rect.width % 2 == 1) rect.width -= 1;
                points[0] = new Point(rect.x, rect.y + rect.height);
                points[1] = new Point(rect.x + rect.width, rect.y + rect.height);
                points[2] = new Point(rect.x + rect.width / 2, rect.y);
                break;
            case ITheme.DOWN:
                if (rect.width % 2 == 1) rect.width -= 1;
                points[2] = new Point(rect.x, rect.y);
                points[1] = new Point(rect.x + rect.width, rect.y);
                points[0] = new Point(rect.x + rect.width / 2, rect.y + rect.height);
                break;
        }
        if (symbol >= ITheme.LEFT && symbol <= ITheme.DOWN) {
            context.getInterface().fillTriangle(points[0], points[1], points[2], color, color, color);
        }
        if (title != null)
            context.getInterface().drawString(new Point(context.getPos().x + (symbol == ITheme.NONE ? padding : context.getSize().height), context.getPos().y + padding), height, title, getFontColor(focus));
    }

    @Override
    public IDescriptionRenderer getDescriptionRenderer() {
        return new IDescriptionRenderer() {
            @Override
            public void renderDescription(IInterface inter, Point pos, String text) {
                Rectangle rect = new Rectangle(pos, new Dimension(inter.getFontWidth(height, text) + 2 * border - 2, height + 2 * border - 2));
                Color color = scheme.getColor("Tooltip Color");
                inter.fillRect(rect, color, color, color, color);
                inter.drawString(new Point(pos.x + border - 1, pos.y + border - 1), height, text, getFontColor(true));
            }
        };
    }

    @Override
    public IContainerRenderer getContainerRenderer(int logicalLevel, int graphicalLevel, boolean horizontal) {
        return new IContainerRenderer() {
            @Override
            public void renderBackground(Context context, boolean focus) {
                if (graphicalLevel == 0) InfinityTheme.this.renderBackground(context, focus);
            }

            @Override
            public int getBorder() {
                if (graphicalLevel <= 0 && logicalLevel >= 2) return padding;
                else if (graphicalLevel <= 0 || logicalLevel < 0 || horizontal) return 0;
                else if (logicalLevel == 0) return 2 * border;
                else return border;
            }

            @Override
            public int getLeft() {
                if (graphicalLevel <= 0 && logicalLevel >= 2) return padding;
                else if (graphicalLevel <= 0 || logicalLevel < 0 || horizontal) return 0;
                else return 2 * border;
            }

            @Override
            public int getRight() {
                if (graphicalLevel <= 0 && logicalLevel >= 2) return padding;
                else if (graphicalLevel <= 0 || logicalLevel < 0 || horizontal) return 0;
                else if (logicalLevel == 0) return 2 * border;
                else return border;
            }

            @Override
            public int getTop() {
                if (graphicalLevel <= 0 && logicalLevel >= 2) return padding;
                else if (graphicalLevel <= 0 || logicalLevel < 0 || horizontal) return 0;
                else return 2 * border;
            }

            @Override
            public int getBottom() {
                if (graphicalLevel <= 0 && logicalLevel >= 2) return padding;
                else if (graphicalLevel <= 0 || logicalLevel < 0 || horizontal) return 0;
                else return 2 * border;
            }
        };
    }

    @Override
    public <T> IPanelRenderer<T> getPanelRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
        return new IPanelRenderer<T>() {
            @Override
            public void renderBackground(Context context, boolean focus) {
                Color color = getBackgroundColor(focus);
                if (logicalLevel == 1) context.getInterface().fillRect(context.getRect(), color, color, color, color);
                if (logicalLevel > 0) {
                    Color colorA = scheme.getColor("Primary Outline Color");
                    Color colorB = scheme.getColor("Secondary Outline Color");
                    Rectangle rect = context.getRect();
                    ITheme.drawRect(context.getInterface(), rect, colorA);
                    ITheme.drawRect(context.getInterface(), new Rectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2), colorB);
                }
            }

            @Override
            public int getBorder() {
                return 0;
            }

            @Override
            public int getLeft() {
                return logicalLevel > 0 ? 2 : 0;
            }

            @Override
            public int getRight() {
                return logicalLevel > 0 ? 2 : 0;
            }

            @Override
            public int getTop() {
                return 0;
            }

            @Override
            public int getBottom() {
                return logicalLevel > 0 ? 2 : 0;
            }

            @Override
            public void renderPanelOverlay(Context context, boolean focus, T state, boolean open) {
            }

            @Override
            public void renderTitleOverlay(Context context, boolean focus, T state, boolean open) {
            }
        };
    }

    @Override
    public <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
        return new IScrollBarRenderer<T>() {
            @Override
            public int renderScrollBar(Context context, boolean focus, T state, boolean horizontal, int height, int position) {
                Color activecolor = scheme.getColor("Scroll Bar Color");
                Color inactivecolor = scheme.getColor("Secondary Outline Color");
                context.getInterface().fillRect(context.getRect(), inactivecolor, inactivecolor, inactivecolor, inactivecolor);
                if (horizontal) {
                    int a = (int) (position / (double) height * context.getSize().width);
                    int b = (int) ((position + context.getSize().width) / (double) height * context.getSize().width);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + a + 1, context.getPos().y + 1, b - a - 2, context.getSize().height - 2), activecolor, activecolor, activecolor, activecolor);
                } else {
                    int a = (int) (position / (double) height * context.getSize().height);
                    int b = (int) ((position + context.getSize().height) / (double) height * context.getSize().height);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + 1, context.getPos().y + a + 1, context.getSize().width - 2, b - a - 2), activecolor, activecolor, activecolor, activecolor);
                }
                if (horizontal)
                    return (int) ((context.getInterface().getMouse().x - context.getPos().x) * height / (double) context.getSize().width - context.getSize().width / 2.0);
                else
                    return (int) ((context.getInterface().getMouse().y - context.getPos().y) * height / (double) context.getSize().height - context.getSize().height / 2.0);
            }

            @Override
            public int getThickness() {
                return scroll;
            }
        };
    }

    @Override
    public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
        return new IEmptySpaceRenderer<T>() {
            @Override
            public void renderSpace(Context context, boolean focus, T state) {
                if (graphicalLevel == 0) renderBackground(context, focus);
            }
        };
    }

    @Override
    public <T> IButtonRenderer<T> getButtonRenderer(Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
        return new IButtonRenderer<T>() {
            @Override
            public void renderButton(Context context, String title, boolean focus, T state) {
                boolean effFocus = container ? context.hasFocus() : focus;
                Rectangle rect = context.getRect();
                if (graphicalLevel <= 0) {
                    if (container) {
                        Color colorA = scheme.getColor("Primary Outline Color");
                        Color colorB = scheme.getColor("Title Color");
                        int gradient = InfinityTheme.this.gradient.getAsInt();
                        ITheme.drawRect(context.getInterface(), rect, colorA);
                        int current = rect.x + 1;
                        float[] hsb = Color.RGBtoHSB(colorB.getRed(), colorB.getGreen(), colorB.getBlue(), null);
                        while (current < rect.x + rect.width - 1) {
                            float hue = (float) (Math.floor(hsb[0] * 6) + 1) / 6;
                            int next = (int) (current + (hue - hsb[0]) * gradient);
                            if (next > rect.x + rect.width - 1) {
                                next = rect.x + rect.width - 1;
                                hue = hsb[0] + (next - current) / (float) gradient;
                            }
                            Color colorD = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
                            Color colorC = Color.getHSBColor(hue, hsb[1], hsb[2]);
                            context.getInterface().fillRect(new Rectangle(current, rect.y + 1, next - current, 1), colorD, colorC, colorC, colorD);
                            Color colorF = Color.getHSBColor(hsb[0], hsb[1], hsb[2] / 2);
                            Color colorE = Color.getHSBColor(hue, hsb[1], hsb[2] / 2);
                            context.getInterface().fillRect(new Rectangle(current, rect.y + 2, next - current, 1), colorF, colorE, colorE, colorF);
                            hsb[0] = hue;
                            current = next;
                        }
                    } else renderBackground(context, effFocus);
                } else if (container && logicalLevel == 1) {
                    Color color = getBackgroundColor(effFocus);
                    int start = context.getInterface().getFontWidth(height, title) + border;
                    context.getInterface().fillRect(new Rectangle(rect.x + border / 2, rect.y, start, 2), color, color, color, color);
                    context.getInterface().drawString(new Point(rect.x + border, rect.y - height / 2), height, title, getFontColor(effFocus));
                } else if (!container) {
                    context.getInterface().drawString(new Point(rect.x + getBaseHeight() + border, rect.y), height, title, getFontColor(effFocus));
                    if (type == Boolean.class) {
                        Color a = getMainColor(focus, (Boolean) state);
                        Color b = darker(a);
                        context.getInterface().fillRect(new Rectangle(rect.x + 1, rect.y + 1, rect.height - 2, rect.height - 2), a, a, b, b);
                        Color c = scheme.getColor("Primary Outline Color");
                        ITheme.drawRect(context.getInterface(), new Rectangle(rect.x, rect.y, rect.height, rect.height), c);
                    } else if (type == String.class) {
                        context.getInterface().drawString(new Point(rect.x + rect.width - context.getInterface().getFontWidth(height, (String) state), rect.y), height, (String) state, getFontColor(effFocus));
                    } else if (type == Color.class) {
                        Color a = (Color) state;
                        Color b = darker(a);
                        context.getInterface().fillRect(new Rectangle(rect.x + rect.width - 2 * rect.height + 1, rect.y + 1, 2 * rect.height - 2, rect.height - 2), a, a, b, b);
                        Color c = scheme.getColor("Primary Outline Color");
                        ITheme.drawRect(context.getInterface(), new Rectangle(rect.x + rect.width - 2 * rect.height, rect.y, 2 * rect.height, rect.height), c);
                    }
                }
            }

            @Override
            public int getDefaultHeight() {
                if (container && graphicalLevel <= 0) return 4;
                else if (container && logicalLevel == 1) return 2;
                else return getBaseHeight();
            }
        };
    }

    @Override
    public IButtonRenderer<Void> getSmallButtonRenderer(int symbol, int logicalLevel, int graphicalLevel, boolean container) {
        return new IButtonRenderer<Void>() {
            @Override
            public void renderButton(Context context, String title, boolean focus, Void state) {
                if (graphicalLevel <= 0) {
                    if (!container) renderBackground(context, focus);
                }
                renderSmallButton(context, title, symbol, focus);
            }

            @Override
            public int getDefaultHeight() {
                return getBaseHeight();
            }
        };
    }

    @Override
    public IButtonRenderer<String> getKeybindRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        return new IButtonRenderer<String>() {
            @Override
            public void renderButton(Context context, String title, boolean focus, String state) {
                getButtonRenderer(String.class, logicalLevel, graphicalLevel, container).renderButton(context, title, focus, focus ? "..." : state);
            }

            @Override
            public int getDefaultHeight() {
                if (container && graphicalLevel <= 0) return 4;
                else if (container && logicalLevel == 1) return 0;
                else return getBaseHeight();
            }
        };
    }

    @Override
    public ISliderRenderer getSliderRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        return new ISliderRenderer() {
            @Override
            public void renderSlider(Context context, String title, String state, boolean focus, double value) {
                context.getInterface().drawString(new Point(context.getPos().x + getBaseHeight() + border, context.getPos().y), height, title, getFontColor(focus));
                Rectangle rect = getSlideArea(context, title, state);
                Color a = getMainColor(focus, true);
                Color b = darker(a);
                Color c = getMainColor(focus, false);
                Color d = darker(c);
                int separator = (int) Math.round(rect.width * value);
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y, separator, rect.height), a, a, b, b);
                context.getInterface().fillRect(new Rectangle(rect.x + separator, rect.y, rect.width - separator, rect.height), d, d, c, c);
                ITheme.drawRect(context.getInterface(), rect, scheme.getColor("Primary Outline Color"));
                context.getInterface().drawString(new Point(rect.x + (rect.width - context.getInterface().getFontWidth(height, state)) / 2, rect.y + 1), height, state, getFontColor(focus));
            }

            @Override
            public int getDefaultHeight() {
                return 2 * getBaseHeight();
            }

            @Override
            public Rectangle getSlideArea(Context context, String title, String state) {
                return new Rectangle(context.getPos().x + getBaseHeight() + border, context.getPos().y + getBaseHeight(), context.getSize().width - 2 * getBaseHeight() - 2 * border, getBaseHeight());
            }
        };
    }

    @Override
    public IRadioRenderer getRadioRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        return new IRadioRenderer() {
            @Override
            public void renderItem(Context context, ILabeled[] items, boolean focus, int target, double state, boolean horizontal) {
                if (graphicalLevel <= 0) renderBackground(context, focus);
                for (int i = 0; i < items.length; i++) {
                    Rectangle rect = getItemRect(context, items, i, horizontal);
                    Context subContext = new Context(context.getInterface(), rect.width, rect.getLocation(), context.hasFocus(), context.onTop());
                    subContext.setHeight(rect.height);
                    Color color = i == target ? getFontColor(focus) : scheme.getColor("Inactive Font Color");
                    Color colorA = getBackgroundColor(focus);
                    Color colorB = scheme.getColor("Secondary Outline Color");
                    Color colorC = scheme.getColor("Primary Outline Color");
                    if (i == target) {
                        if (horizontal) {
                            context.getInterface().fillRect(new Rectangle(rect.x + 1, rect.y, 1, rect.height), colorC, colorC, colorC, colorC);
                            context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height - 1, 2, 1), colorC, colorC, colorC, colorC);
                            context.getInterface().fillRect(new Rectangle(rect.x, rect.y, 1, rect.height - 1), colorB, colorB, colorB, colorB);
                            context.getInterface().fillRect(new Rectangle(rect.x + rect.width - 2, rect.y, 1, rect.height), colorC, colorC, colorC, colorC);
                            context.getInterface().fillRect(new Rectangle(rect.x + rect.width - 2, rect.y + rect.height - 1, 2, 1), colorC, colorC, colorC, colorC);
                            context.getInterface().fillRect(new Rectangle(rect.x + rect.width - 1, rect.y, rect.height - 1, 1), colorB, colorB, colorB, colorB);
                        } else {
                            context.getInterface().fillRect(new Rectangle(rect.x, rect.y + 1, rect.width, 1), colorC, colorC, colorC, colorC);
                            context.getInterface().fillRect(new Rectangle(rect.x + rect.width - 1, rect.y, 1, 2), colorC, colorC, colorC, colorC);
                            context.getInterface().fillRect(new Rectangle(rect.x, rect.y, rect.width - 1, 1), colorB, colorB, colorB, colorB);
                            context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height - 2, rect.width, 1), colorC, colorC, colorC, colorC);
                            context.getInterface().fillRect(new Rectangle(rect.x + rect.width - 1, rect.y + rect.height - 2, 1, 2), colorC, colorC, colorC, colorC);
                            context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height - 1, rect.width - 1, 1), colorB, colorB, colorB, colorB);
                        }
                    } else {
                        context.getInterface().fillRect(rect, colorA, colorA, colorA, colorA);
                        if (horizontal) {
                            context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height - 2, rect.width, 1), colorB, colorB, colorB, colorB);
                            context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height - 1, rect.width, 1), colorC, colorC, colorC, colorC);
                        } else {
                            context.getInterface().fillRect(new Rectangle(rect.x + rect.width - 2, rect.y, 1, rect.height), colorB, colorB, colorB, colorB);
                            context.getInterface().fillRect(new Rectangle(rect.x + rect.width - 1, rect.y, 1, rect.height), colorC, colorC, colorC, colorC);
                        }
                    }
                    if (!horizontal && container && icons.containsKey(items[i].getDisplayName())) {
                        context.getInterface().drawImage(new Rectangle(rect.x + 16, rect.y + 16, rect.width - 32, rect.height - 32), 0, false, icons.get(items[i].getDisplayName()), color);
                    } else {
                        context.getInterface().drawString(new Point(rect.x + 3, rect.y + 3), height, items[i].getDisplayName(), color);
                    }
                }
            }

            @Override
            public int getDefaultHeight(ILabeled[] items, boolean horizontal) {
                if (horizontal || !container) return (horizontal ? 1 : items.length) * (getBaseHeight() + 6);
                else {
                    int height = 0;
                    for (ILabeled item : items) {
                        if (icons.containsKey(item.getDisplayName())) height += 64;
                        else height += getBaseHeight() + 6;
                    }
                    return height;
                }
            }

            @Override
            public Rectangle getItemRect(Context context, ILabeled[] items, int index, boolean horizontal) {
                Rectangle rect = context.getRect();
                if (horizontal) {
                    int start = (int) Math.round(rect.width / (double) items.length * index);
                    int end = (int) Math.round(rect.width / (double) items.length * (index + 1));
                    return new Rectangle(rect.x + start, rect.y, end - start, rect.height);
                } else if (!container) {
                    int start = (int) Math.round(rect.height / (double) items.length * index);
                    int end = (int) Math.round(rect.height / (double) items.length * (index + 1));
                    return new Rectangle(rect.x, rect.y + start, rect.width, end - start);
                } else {
                    int start = getRectBase(context, items, index);
                    int end = getRectBase(context, items, index + 1);
                    return new Rectangle(rect.x, rect.y + start, rect.width, end - start);
                }
            }

            private int getRectBase(Context context, ILabeled[] items, int index) {
                int count = 0;
                for (ILabeled item : items) {
                    if (icons.containsKey(item.getDisplayName())) count++;
                }
                double totalA = items.length - count + count * 64.0 / (getBaseHeight() + 6);
                double totalB = (items.length - count) * (getBaseHeight() + 6) / 64.0 + count;
                int textHeight = (int) Math.round(context.getSize().height / totalA);
                int iconHeight = (int) Math.round(context.getSize().height / totalB);
                int position = 0;
                for (int i = 0; i < index; i++) {
                    if (icons.containsKey(items[i].getDisplayName())) position += iconHeight;
                    else position += textHeight;
                }
                return position;
            }
        };
    }

    @Override
    public IResizeBorderRenderer getResizeRenderer() {
        return new IResizeBorderRenderer() {
            @Override
            public void drawBorder(Context context, boolean focus) {
                Color colorA = scheme.getColor("Primary Outline Color");
                Color colorB = scheme.getColor("Tertiary Outline Color");
                Color colorC = scheme.getColor("Secondary Outline Color");
                Rectangle rect = context.getRect();
                ITheme.drawRect(context.getInterface(), rect, colorA);
                ITheme.drawRect(context.getInterface(), new Rectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2), colorB);
                ITheme.drawRect(context.getInterface(), new Rectangle(rect.x + 2, rect.y + 2, rect.width - 4, rect.height - 4), colorC);
                ITheme.drawRect(context.getInterface(), new Rectangle(rect.x + 3, rect.y + 3, rect.width - 6, rect.height - 6), colorC);
                ITheme.drawRect(context.getInterface(), new Rectangle(rect.x + 4, rect.y + 4, rect.width - 8, rect.height - 8), colorB);
            }

            @Override
            public int getBorder() {
                return 5;
            }
        };
    }

    @Override
    public ITextFieldRenderer getTextRenderer(boolean embed, int logicalLevel, int graphicalLevel, boolean container) {
        return new ITextFieldRenderer() {
            @Override
            public int renderTextField(Context context, String title, boolean focus, String content, int position, int select, int boxPosition, boolean insertMode) {
                // Declare and assign variables
                boolean effFocus = container ? context.hasFocus() : focus;
                Color color = scheme.getColor("Primary Outline Color");
                Color textColor = getFontColor(effFocus);
                Color highlightColor = scheme.getColor("Highlight Color");
                Rectangle rect = getTextArea(context, title);
                int strlen = context.getInterface().getFontWidth(height, content.substring(0, position));
                // Deal with box render offset
                if (boxPosition < position) {
                    int minPosition = boxPosition;
                    while (minPosition < position) {
                        if (context.getInterface().getFontWidth(height, content.substring(0, minPosition)) + rect.width - padding >= strlen)
                            break;
                        minPosition++;
                    }
                    if (boxPosition < minPosition) boxPosition = minPosition;
                } else if (boxPosition > position) boxPosition = position - 1;
                int maxPosition = content.length();
                while (maxPosition > 0) {
                    if (context.getInterface().getFontWidth(height, content.substring(maxPosition)) >= rect.width - padding) {
                        maxPosition++;
                        break;
                    }
                    maxPosition--;
                }
                if (boxPosition > maxPosition) boxPosition = maxPosition;
                else if (boxPosition < 0) boxPosition = 0;
                int offset = context.getInterface().getFontWidth(height, content.substring(0, boxPosition));
                // Deal with highlighted text
                int x1 = rect.x + padding / 2 - offset + strlen;
                int x2 = rect.x + padding / 2 - offset;
                if (position < content.length())
                    x2 += context.getInterface().getFontWidth(height, content.substring(0, position + 1));
                else x2 += context.getInterface().getFontWidth(height, content + "X");
                // Draw stuff around the box
                if (graphicalLevel <= 0) renderBackground(context, focus);
                context.getInterface().fillRect(rect, scheme.getColor("Text Field Color"), scheme.getColor("Text Field Color"), scheme.getColor("Text Field Color"), scheme.getColor("Text Field Color"));
                context.getInterface().drawString(new Point(context.getPos().x + getBaseHeight() + border, context.getRect().y + (embed ? padding / 2 : 0)), height, title, textColor);
                // Draw the box
                context.getInterface().window(rect);
                if (select >= 0) {
                    int x3 = rect.x + padding / 2 - offset + context.getInterface().getFontWidth(height, content.substring(0, select));
                    context.getInterface().fillRect(new Rectangle(Math.min(x1, x3), rect.y + padding / 2, Math.abs(x3 - x1), height), highlightColor, highlightColor, highlightColor, highlightColor);
                }
                context.getInterface().drawString(new Point(rect.x + padding / 2 - offset, rect.y + padding / 2), height, content, textColor);
                if ((System.currentTimeMillis() / 500) % 2 == 0 && focus) {
                    if (insertMode)
                        context.getInterface().fillRect(new Rectangle(x1, rect.y + padding / 2 + height, x2 - x1, 1), textColor, textColor, textColor, textColor);
                    else
                        context.getInterface().fillRect(new Rectangle(x1, rect.y + padding / 2, 1, height), textColor, textColor, textColor, textColor);
                }
                ITheme.drawRect(context.getInterface(), rect, color);
                context.getInterface().restore();
                return boxPosition;
            }

            @Override
            public int getDefaultHeight() {
                if (embed) {
                    int height = getBaseHeight() + padding;
                    if (height % 2 == 1) height += 1;
                    return height;
                } else return 2 * getBaseHeight() + padding;
            }

            @Override
            public Rectangle getTextArea(Context context, String title) {
                Rectangle rect = context.getRect();
                if (embed) {
                    int length = border + getBaseHeight() + padding + context.getInterface().getFontWidth(height, title);
                    return new Rectangle(rect.x + length, rect.y, rect.width - length, rect.height);
                } else
                    return new Rectangle(rect.x + border + getBaseHeight(), rect.y + getBaseHeight(), rect.width - 2 * border - 2 * getBaseHeight(), rect.height - getBaseHeight());
            }

            @Override
            public int transformToCharPos(Context context, String title, String content, int boxPosition) {
                Rectangle rect = getTextArea(context, title);
                Point mouse = context.getInterface().getMouse();
                int offset = context.getInterface().getFontWidth(height, content.substring(0, boxPosition));
                if (rect.contains(mouse)) {
                    for (int i = 1; i <= content.length(); i++) {
                        if (rect.x + padding / 2 - offset + context.getInterface().getFontWidth(height, content.substring(0, i)) > mouse.x) {
                            return i - 1;
                        }
                    }
                    return content.length();
                }
                return -1;
            }
        };
    }

    @Override
    public ISwitchRenderer<Boolean> getToggleSwitchRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        return new ISwitchRenderer<Boolean>() {
            @Override
            public void renderButton(Context context, String title, boolean focus, Boolean state) {
                boolean effFocus = container ? context.hasFocus() : focus;
                if (graphicalLevel <= 0) renderBackground(context, effFocus);
                context.getInterface().drawString(new Point(context.getPos().x + getBaseHeight() + border, context.getPos().y), height, title, getFontColor(focus));
                Color a = getMainColor(effFocus, false);
                Color b = darker(a);
                Rectangle rect = context.getRect();
                rect = new Rectangle(rect.x + rect.width - 2 * rect.height, rect.y, 2 * rect.height, rect.height);
                context.getInterface().fillRect(new Rectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2), a, a, b, b);
                ITheme.drawRect(context.getInterface(), rect, scheme.getColor("Primary Outline Color"));
                a = state ? getMainColor(effFocus, true) : scheme.getColor("Disabled Color");
                b = darker(a);
                rect = state ? getOnField(context) : getOffField(context);
                rect = new Rectangle(rect.x + (state ? 0 : 1), rect.y + 1, rect.width - 1, rect.height - 2);
                context.getInterface().fillRect(rect, a, a, b, b);
            }

            @Override
            public int getDefaultHeight() {
                return getBaseHeight();
            }

            @Override
            public Rectangle getOnField(Context context) {
                Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - rect.height, rect.y, rect.height, rect.height);
            }

            @Override
            public Rectangle getOffField(Context context) {
                Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - 2 * rect.height, rect.y, rect.height, rect.height);
            }
        };
    }

    @Override
    public ISwitchRenderer<String> getCycleSwitchRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        return new ISwitchRenderer<String>() {
            @Override
            public void renderButton(Context context, String title, boolean focus, String state) {
                boolean effFocus = container ? context.hasFocus() : focus;
                if (graphicalLevel <= 0) renderBackground(context, focus);
                Context subContext = new Context(context, context.getSize().width - 2 * context.getSize().height, new Point(0, 0), true, true);
                subContext.setHeight(context.getSize().height);
                Color textColor = getFontColor(effFocus);
                context.getInterface().drawString(new Point(context.getPos().x + context.getSize().height + border, context.getPos().y), height, title, textColor);
                context.getInterface().drawString(new Point(context.getPos().x + context.getSize().width - context.getInterface().getFontWidth(height, state), context.getPos().y), height, state, textColor);
                Rectangle rect = getOnField(context);
                subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
                subContext.setHeight(rect.height);
                getSmallButtonRenderer(ITheme.RIGHT, logicalLevel, graphicalLevel, container).renderButton(subContext, null, effFocus, null);
                rect = getOffField(context);
                subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
                subContext.setHeight(rect.height);
                getSmallButtonRenderer(ITheme.LEFT, logicalLevel, graphicalLevel, container).renderButton(subContext, null, effFocus, null);
            }

            @Override
            public int getDefaultHeight() {
                return getBaseHeight();
            }

            @Override
            public Rectangle getOnField(Context context) {
                Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - rect.height, rect.y, rect.height, rect.height);
            }

            @Override
            public Rectangle getOffField(Context context) {
                Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - 2 * rect.height, rect.y, rect.height, rect.height);
            }
        };
    }

    @Override
    public IColorPickerRenderer getColorPickerRenderer() {
        return new StandardColorPicker() {
            @Override
            public int getPadding() {
                return padding;
            }

            @Override
            public int getBaseHeight() {
                return InfinityTheme.this.getBaseHeight();
            }
        };
    }

    @Override
    public int getBaseHeight() {
        return height;
    }

    @Override
    public Color getMainColor(boolean focus, boolean active) {
        if (active) return scheme.getColor("Enabled Color");
        else return scheme.getColor("Inactive Color");
    }

    @Override
    public Color getBackgroundColor(boolean focus) {
        return scheme.getColor("Background Color");
    }

    @Override
    public Color getFontColor(boolean focus) {
        return scheme.getColor("Active Font Color");
    }
}
