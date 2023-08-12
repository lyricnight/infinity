package me.lyric.infinity.gui.panelstudio;

import com.lukflug.panelstudio.base.*;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IResizable;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.container.GUI;
import com.lukflug.panelstudio.layout.*;
import com.lukflug.panelstudio.layout.ChildUtil.ChildMode;
import com.lukflug.panelstudio.mc12.MinecraftGUI;
import com.lukflug.panelstudio.popup.CenteredPositioner;
import com.lukflug.panelstudio.popup.MousePositioner;
import com.lukflug.panelstudio.popup.PanelPositioner;
import com.lukflug.panelstudio.popup.PopupTuple;
import com.lukflug.panelstudio.setting.*;
import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.widget.*;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.Bind;
import me.lyric.infinity.api.setting.settings.ColorPicker;
import me.lyric.infinity.gui.theme.InfinityTheme;
import me.lyric.infinity.impl.modules.client.ClickGUI;
import me.lyric.infinity.manager.client.ModuleManager;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.stream.Stream;

public class PanelStudioGUI extends MinecraftGUI {

    public static final int WIDTH = 120, HEIGHT = 12, DISTANCE = 6, BORDER = 2;
    private final GUIInterface inter;
    private final GUI gui;

    public PanelStudioGUI() {
        // Getting client structure ...
        IClient client = () -> Arrays.stream(Category.values()).map(category -> new ICategory() {
            @Override
            public String getDisplayName() {
                return category.toString();
            }

            @Override
            public Stream<IModule> getModules() {
                return ModuleManager.getModuleManager().getModules().stream().sorted(Comparator.comparing(Module::getName)).filter(module -> module.getCategory() == category).map(module -> new IModule() {
                    @Override
                    public String getDisplayName() {
                        return module.getName();
                    }

                    @Override
                    public String getDescription() {
                        return module.getDescription();
                    }

                    @Override
                    public IToggleable isEnabled() {
                        return new IToggleable() {
                            @Override
                            public void toggle() {
                                module.toggle();
                            }

                            @Override
                            public boolean isOn() {
                                return module.isEnabled();
                            }
                        };
                    }

                    @Override
                    public Stream<ISetting<?>> getSettings() {
                        IBooleanSetting enable = new IBooleanSetting() {
                            @Override
                            public void toggle() {
                                module.toggle();
                            }

                            @Override
                            public boolean isOn() {
                                return module.isEnabled();
                            }

                            @Override
                            public String getDisplayName() {
                                return "Enabled";
                            }
                        };
                        return Stream.concat(Stream.of(enable), module.getSettings().entrySet().stream().sorted((a, b) -> {
                            // get depth
                            int c = 0, d = 0;
                            for (Setting s = a.getValue(); s != null; c++)
                                s = s.getMaster();
                            for (Setting s = b.getValue(); s != null; d++)
                                s = s.getMaster();
                            // equalize depth
                            Setting x = a.getValue(), y = b.getValue();
                            for (; c > d; c--)
                                x = x.getMaster();
                            for (; d > c; d--)
                                y = y.getMaster();
                            // find lowest common ancestor
                            while (x.getMaster() != y.getMaster()) {
                                x = x.getMaster();
                                y = y.getMaster();
                            }
                            // if comparing with parent, prioritize parent
                            if (a.getValue() == y) return -1;
                            else if (b.getValue() == x) return 1;
                            return x.getName().compareTo(y.getName());
                        }).map(setting -> getSetting(module, setting.getValue())));
                    }
                });
            }
        });

        /* Set to false to disable horizontal clipping, this may cause graphical glitches,
         * but will let you see long text, even if it is too long to fit in the panel. */
        inter = new GUIInterface(true) {
            @Override
            protected String getResourcePrefix() {
                return "infinity:gui/";
            }
        };
        // Instantiating theme ...
        ITheme theme = new OptimizedTheme(new InfinityTheme(new IColorScheme() {
            @Override
            public void createSetting(ITheme theme, String name, String description, boolean hasAlpha, boolean allowsRainbow, Color color, boolean rainbow) {
                ClickGUI.clickGUI.register(new Setting<me.lyric.infinity.api.setting.settings.ColorPicker>(name, description, new me.lyric.infinity.api.setting.settings.ColorPicker(color)));
            }

            @Override
            public Color getColor(String name) {
                return ((Setting<me.lyric.infinity.api.setting.settings.ColorPicker>) ClickGUI.clickGUI.getSetting(name)).getValue().getColor();
            }
        }, 9, 4, 10, 6, () -> ClickGUI.clickGUI.rainbowGradient.getValue()));
        theme.loadAssets(inter);

        // Instantiating GUI ...
        gui = new GUI(inter, theme.getDescriptionRenderer(), new MousePositioner(new Point(10, 10)));
        // Creating animation ...
        Supplier<Animation> animation = () -> new SettingsAnimation(() -> ClickGUI.clickGUI.animationSpeed.getValue(), () -> inter.getTime());
        // Creating popup types ...
        BiFunction<Context, Integer, Integer> scrollHeight = (context, componentHeight) -> Math.min(componentHeight, Math.max(HEIGHT * 4, PanelStudioGUI.this.height - context.getPos().y - HEIGHT));
        PopupTuple popupType = new PopupTuple(new PanelPositioner(new Point(0, 0)), false, new IScrollSize() {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return scrollHeight.apply(context, componentHeight);
            }
        });
        PopupTuple colorPopup = new PopupTuple(new CenteredPositioner(() -> new Rectangle(new Point(0, 0), inter.getWindowSize())), true, new IScrollSize() {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return scrollHeight.apply(context, componentHeight);
            }
        });
        // Defining resize behavior ...
        IntFunction<IResizable> resizable = width -> new IResizable() {
            final Dimension size = new Dimension(width, 320);

            @Override
            public Dimension getSize() {
                return new Dimension(size);
            }

            @Override
            public void setSize(Dimension size) {
                this.size.width = size.width;
                this.size.height = size.height;
                if (size.width < 75) this.size.width = 75;
                if (size.height < 50) this.size.height = 50;
            }
        };
        // Defining scroll behavior ...
        Function<IResizable, IScrollSize> resizableHeight = size -> new IScrollSize() {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return size.getSize().height;
            }
        };
        // Defining function keys ...
        IntPredicate keybindKey = scancode -> scancode == Keyboard.KEY_DELETE;
        IntPredicate charFilter = character -> {
            return character >= ' ';
        };
        ITextFieldKeys keys = new ITextFieldKeys() {
            @Override
            public boolean isBackspaceKey(int scancode) {
                return scancode == Keyboard.KEY_BACK;
            }

            @Override
            public boolean isDeleteKey(int scancode) {
                return scancode == Keyboard.KEY_DELETE;
            }

            @Override
            public boolean isInsertKey(int scancode) {
                return scancode == Keyboard.KEY_INSERT;
            }

            @Override
            public boolean isLeftKey(int scancode) {
                return scancode == Keyboard.KEY_LEFT;
            }

            @Override
            public boolean isRightKey(int scancode) {
                return scancode == Keyboard.KEY_RIGHT;
            }

            @Override
            public boolean isHomeKey(int scancode) {
                return scancode == Keyboard.KEY_HOME;
            }

            @Override
            public boolean isEndKey(int scancode) {
                return scancode == Keyboard.KEY_END;
            }

            @Override
            public boolean isCopyKey(int scancode) {
                return scancode == Keyboard.KEY_C;
            }

            @Override
            public boolean isPasteKey(int scancode) {
                return scancode == Keyboard.KEY_V;
            }

            @Override
            public boolean isCutKey(int scancode) {
                return scancode == Keyboard.KEY_X;
            }

            @Override
            public boolean isAllKey(int scancode) {
                return scancode == Keyboard.KEY_A;
            }
        };

        // Normal generator
        IComponentGenerator generator = new ComponentGenerator(keybindKey, charFilter, keys);
        // Use cycle switches instead of buttons
        IComponentGenerator cycleGenerator = new ComponentGenerator(keybindKey, charFilter, keys) {
            @Override
            public IComponent getEnumComponent(IEnumSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new CycleSwitch(setting, theme.getCycleSwitchRenderer(isContainer));
            }
        };
        // Use all the fancy widgets with text boxes
        IComponentGenerator csgoGenerator = new ComponentGenerator(keybindKey, charFilter, keys) {
            @Override
            public IComponent getBooleanComponent(IBooleanSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new ToggleSwitch(setting, theme.getToggleSwitchRenderer(isContainer));
            }

            @Override
            public IComponent getEnumComponent(IEnumSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new DropDownList(setting, theme, isContainer, false, keys, new IScrollSize() {
                }, adder::addPopup) {
                    @Override
                    protected Animation getAnimation() {
                        return animation.get();
                    }

                    @Override
                    public boolean allowCharacter(char character) {
                        return charFilter.test(character);
                    }

                    @Override
                    protected boolean isUpKey(int key) {
                        return key == Keyboard.KEY_UP;
                    }

                    @Override
                    protected boolean isDownKey(int key) {
                        return key == Keyboard.KEY_DOWN;
                    }

                    @Override
                    protected boolean isEnterKey(int key) {
                        return key == Keyboard.KEY_RETURN;
                    }
                };
            }

            @Override
            public IComponent getNumberComponent(INumberSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new Spinner(setting, theme, isContainer, true, keys);
            }

            @Override
            public IComponent getColorComponent(IColorSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new ColorPickerComponent(setting, new ThemeTuple(theme.theme, theme.logicalLevel, colorLevel));
            }
        };
        IComponentGenerator swagGenerator = new ComponentGenerator(keybindKey, charFilter, keys) {
            @Override
            public IComponent getEnumComponent(IEnumSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new DropDownList(setting, theme, isContainer, false, keys, new IScrollSize() {
                }, adder::addPopup) {
                    @Override
                    protected Animation getAnimation() {
                        return animation.get();
                    }

                    @Override
                    public boolean allowCharacter(char character) {
                        return charFilter.test(character);
                    }

                    @Override
                    protected boolean isUpKey(int key) {
                        return key == Keyboard.KEY_UP;
                    }

                    @Override
                    protected boolean isDownKey(int key) {
                        return key == Keyboard.KEY_DOWN;
                    }

                    @Override
                    protected boolean isEnterKey(int key) {
                        return key == Keyboard.KEY_RETURN;
                    }
                };
            }

            @Override
            public IComponent getColorComponent(IColorSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new ColorPickerComponent(setting, new ThemeTuple(theme.theme, theme.logicalLevel, colorLevel));
            }
        };
        //Classic Panel
        IComponentAdder classicPanelAdder=new PanelAdder(gui,false,()-> ClickGUI.clickGUI.layout.getValue()== ClickGUI.Layout.ClassicPanel, title->"classicPanel_" + title) {
            @Override
            protected IResizable getResizable (int width) {
                return resizable.apply(width);
            }

            @Override
            protected IScrollSize getScrollSize (IResizable size) {
                return resizableHeight.apply(size);
            }
        };
        ILayout classicPanelLayout=new PanelLayout(WIDTH,new Point(DISTANCE,DISTANCE),(WIDTH+DISTANCE)/2,HEIGHT+DISTANCE,animation,level->ChildMode.DOWN,level->ChildMode.DOWN,popupType);
        classicPanelLayout.populateGUI(classicPanelAdder, generator, client, theme);

        // Horizontal CSGO
        AtomicReference<IResizable> horizontalResizable = new AtomicReference<IResizable>(null);
        IComponentAdder horizontalCSGOAdder = new PanelAdder(gui, true, () -> ClickGUI.clickGUI.layout.getValue() == ClickGUI.Layout.CSGOHorizontal, title -> "horizontalCSGO_" + title) {
            @Override
            protected IResizable getResizable(int width) {
                horizontalResizable.set(resizable.apply(width));
                return horizontalResizable.get();
            }
        };
        ILayout horizontalCSGOLayout = new CSGOLayout(new Labeled("Example", null, () -> true), new Point(100, 100), 480, WIDTH, animation, "Enabled", true, true, 2, ChildMode.POPUP, colorPopup) {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return resizableHeight.apply(horizontalResizable.get()).getScrollHeight(null, height);
            }
        };
        horizontalCSGOLayout.populateGUI(horizontalCSGOAdder, csgoGenerator, client, theme);
        // Vertical CSGO
        AtomicReference<IResizable> verticalResizable = new AtomicReference<IResizable>(null);
        IComponentAdder verticalCSGOAdder = new PanelAdder(gui, true, () -> ClickGUI.clickGUI.layout.getValue() == ClickGUI.Layout.CSGOVertical, title -> "verticalCSGO_" + title) {
            @Override
            protected IResizable getResizable(int width) {
                verticalResizable.set(resizable.apply(width));
                return verticalResizable.get();
            }
        };
        ILayout verticalCSGOLayout = new CSGOLayout(new Labeled("Example", null, () -> true), new Point(100, 100), 480, WIDTH, animation, "Enabled", false, true, 2, ChildMode.POPUP, colorPopup) {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return resizableHeight.apply(verticalResizable.get()).getScrollHeight(null, height);
            }
        };
        verticalCSGOLayout.populateGUI(verticalCSGOAdder, csgoGenerator, client, theme);
        // Category CSGO
        AtomicReference<IResizable> categoryResizable = new AtomicReference<IResizable>(null);
        IComponentAdder categoryCSGOAdder = new PanelAdder(gui, true, () -> ClickGUI.clickGUI.layout.getValue() == ClickGUI.Layout.CSGOCategory, title -> "categoryCSGO_" + title) {
            @Override
            protected IResizable getResizable(int width) {
                categoryResizable.set(resizable.apply(width));
                return categoryResizable.get();
            }
        };
        ILayout categoryCSGOLayout = new CSGOLayout(new Labeled("Window Title", null, () -> true), new Point(100, 100), 480, WIDTH, animation, "Enabled", false, false, 64, 1, ChildMode.POPUP, colorPopup) {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return resizableHeight.apply(categoryResizable.get()).getScrollHeight(null, height);
            }

            @Override
            public void populateGUI(IComponentAdder gui, IComponentGenerator components, IClient client, ITheme theme) {
                util = new ChildUtil(WIDTH, animation, colorPopup) {
                    @Override
                    protected AnimatedToggleable getAnimatedToggleable(Animation animation) {
                        return new AnimatedToggleable(new ConstantToggleable(true), animation);
                    }
                };
                super.populateGUI(gui, components, client, theme);
            }
        };
        categoryCSGOLayout.populateGUI(categoryCSGOAdder, swagGenerator, client, theme);
        // Searchable CSGO
        AtomicReference<IResizable> searchableResizable = new AtomicReference<IResizable>(null);
        IComponentAdder searchableCSGOAdder = new PanelAdder(gui, true, () -> ClickGUI.clickGUI.layout.getValue() == ClickGUI.Layout.SearchableCSGO, title -> "searchableCSGO_" + title) {
            @Override
            protected IResizable getResizable(int width) {
                searchableResizable.set(resizable.apply(width));
                return searchableResizable.get();
            }
        };
        ILayout searchableCSGOLayout = new SearchableLayout(new Labeled("Example", null, () -> true), new Labeled("Search", null, () -> true), new Point(100, 100), 480, WIDTH, animation, "Enabled", 2, ChildMode.POPUP, colorPopup, (a, b) -> a.getDisplayName().compareTo(b.getDisplayName()), charFilter, keys) {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return resizableHeight.apply(searchableResizable.get()).getScrollHeight(null, height);
            }
        };
        searchableCSGOLayout.populateGUI(searchableCSGOAdder, csgoGenerator, client, theme);
    }

    @Override
    public void exitGUI() {
        if (ClickGUI.clickGUI.isEnabled()) ClickGUI.clickGUI.toggle();
    }

    @Override
    protected GUI getGUI() {
        return gui;
    }

    @Override
    protected GUIInterface getInterface() {
        return inter;
    }

    @Override
    protected int getScrollSpeed() {
        return ClickGUI.clickGUI.scrollSpeed.getValue();
    }

    private ISetting<?> getSetting(Module module, Setting<?> setting) {
        if (setting.getValue() instanceof Boolean)
            return new IBooleanSetting() {
                @Override
                public IBoolean isVisible() {
                    return () -> {
                        if (!setting.isSub()) return true;
                        if (setting.getMaster().getValue() instanceof Boolean)
                            return (Boolean) setting.getMaster().getValue();
                        return true;
                    };
                }

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public String getDescription() {
                    return setting.getDescription();
                }

                @Override
                public boolean isOn() {
                    return (Boolean) setting.getValue();
                }

                @Override
                public void toggle() {
                    Setting<Boolean> set = (Setting<Boolean>) setting;
                    set.setValue(!set.getValue());
                }
            };
        else if (setting.getValue() instanceof Double)
            return new INumberSetting() {
                @Override
                public double getNumber() {
                    return (Double) setting.getValue();
                }

                @Override
                public double getMaximumValue() {
                    return (Double) setting.getMaximum();
                }

                @Override
                public double getMinimumValue() {
                    return (Double) setting.getMinimum();
                }

                @Override
                public int getPrecision() {
                    return 2;
                }

                @Override
                public IBoolean isVisible() {
                    return () -> {
                        if (!setting.isSub()) return true;
                        if (setting.getMaster().getValue() instanceof Boolean)
                            return (Boolean) setting.getMaster().getValue();
                        return true;
                    };
                }

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public String getDescription() {
                    return setting.getDescription();
                }

                @Override
                public void setNumber(double value) {
                    Setting<Double> set = (Setting<Double>) setting;
                    set.setValue(value);
                }


            };
        else if (setting.getValue() instanceof Float)
            return new INumberSetting() {
                @Override
                public double getNumber() {
                    return (Float) setting.getValue();
                }

                @Override
                public double getMaximumValue() {
                    return (Float) setting.getMaximum();
                }

                @Override
                public double getMinimumValue() {
                    return (Float) setting.getMinimum();
                }

                @Override
                public int getPrecision() {
                    return 2;
                }

                @Override
                public IBoolean isVisible() {
                    return () -> {
                        if (!setting.isSub()) return true;
                        if (setting.getMaster().getValue() instanceof Boolean)
                            return (Boolean) setting.getMaster().getValue();
                        return true;
                    };
                }

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public String getDescription() {
                    return setting.getDescription();
                }

                @Override
                public void setNumber(double value) {
                    Setting<Float> set = (Setting<Float>) setting;
                    set.setValue((float) value);
                }


            };
        else if (setting.getValue() instanceof Integer)
            return new INumberSetting() {
                @Override
                public double getNumber() {
                    return (Integer) setting.getValue();
                }

                @Override
                public double getMaximumValue() {
                    return (Integer) setting.getMaximum();
                }

                @Override
                public double getMinimumValue() {
                    return (Integer) setting.getMinimum();
                }

                @Override
                public int getPrecision() {
                    return 0;
                }

                @Override
                public IBoolean isVisible() {
                    return () -> {
                        if (!setting.isSub()) return true;
                        if (setting.getMaster().getValue() instanceof Boolean)
                            return (Boolean) setting.getMaster().getValue();
                        return true;
                    };
                }

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public String getDescription() {
                    return setting.getDescription();
                }

                @Override
                public void setNumber(double value) {
                    Setting<Integer> set = (Setting<Integer>) setting;
                    set.setValue((int) Math.round(value));
                }


            };
        else if (setting.getValue() instanceof Enum<?>)
            return new IEnumSetting() {
                final ILabeled[] values = Arrays.stream(setting.getValue().getClass().getEnumConstants()).map(v -> {
                    return new ILabeled() {
                        @Override
                        public String getDisplayName() {
                            return v.toString();
                        }
                    };
                }).toArray(ILabeled[]::new);

                @Override
                public IBoolean isVisible() {
                    return () -> {
                        if (!setting.isSub()) return true;
                        if (setting.getMaster().getValue() instanceof Boolean)
                            return (Boolean) setting.getMaster().getValue();
                        return true;
                    };
                }

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public String getDescription() {
                    return setting.getDescription();
                }

                @Override
                public void increment() {
                    Enum<?>[] array = ((Enum<?>) setting.getValue()).getClass().getEnumConstants();
                    int index = ((Enum<?>) setting.getValue()).ordinal() + 1;
                    if (index >= array.length) index = 0;
                    ((Setting<Enum<?>>) setting).setValue(array[index]);
                }

                @Override
                public void decrement() {
                    Enum<?>[] array = ((Enum<?>) setting.getValue()).getClass().getEnumConstants();
                    int index = ((Enum<?>) setting.getValue()).ordinal() - 1;
                    if (index < 0) index = array.length - 1;
                    ((Setting<Enum<?>>) setting).setValue(array[index]);
                }

                @Override
                public String getValueName() {
                    return setting.getValue().toString();
                }

                @Override
                public int getValueIndex() {
                    return ((Enum<?>) setting.getValue()).ordinal();
                }

                @Override
                public void setValueIndex(int index) {
                    ((Setting<Enum<?>>) setting).setValue(((Enum<?>) setting.getValue()).getClass().getEnumConstants()[index]);
                }

                @Override
                public ILabeled[] getAllowedValues() {
                    return values;
                }
            };
        else if (setting.getValue() instanceof String)
            return new IStringSetting() {
                @Override
                public IBoolean isVisible() {
                    return () -> {
                        if (!setting.isSub()) return true;
                        if (setting.getMaster().getValue() instanceof Boolean)
                            return (Boolean) setting.getMaster().getValue();
                        return true;
                    };
                }

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public String getDescription() {
                    return setting.getDescription();
                }

                @Override
                public String getValue() {
                    return (String) setting.getValue();
                }

                @Override
                public void setValue(String string) {
                    ((Setting<String>) setting).setValue(string);
                }
            };
        else if (setting.getValue() instanceof me.lyric.infinity.api.setting.settings.ColorPicker)
            return new IColorSetting() {
                @Override
                public Color getValue() {
                    return ((me.lyric.infinity.api.setting.settings.ColorPicker) setting.getValue()).getColor();
                }

                @Override
                public Color getColor() {
                    return ((me.lyric.infinity.api.setting.settings.ColorPicker) setting.getValue()).getRawColor();
                }

                @Override
                public boolean getRainbow() {
                    return ((me.lyric.infinity.api.setting.settings.ColorPicker) setting.getValue()).isRGB();
                }

                @Override
                public boolean hasAlpha() {
                    return true;
                }

                @Override
                public void setRainbow(boolean rainbow) {
                    if (rainbow) ((me.lyric.infinity.api.setting.settings.ColorPicker) setting.getValue()).setRGB();
                    else ((me.lyric.infinity.api.setting.settings.ColorPicker) setting.getValue()).unsetRGB();
                }

                @Override
                public IBoolean isVisible() {
                    return () -> {
                        if (!setting.isSub()) return true;
                        if (setting.getMaster().getValue() instanceof Boolean)
                            return (Boolean) setting.getMaster().getValue();
                        return true;
                    };
                }

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public String getDescription() {
                    return setting.getDescription();
                }

                @Override
                public void setValue(Color value) {
                    ((ColorPicker) setting.getValue()).setColor(value);
                }


            };
        else if (setting.getValue() instanceof Bind)
            return new IKeybindSetting() {
                @Override
                public int getKey() {
                    return ((Bind) setting.getValue()).getKey();
                }

                @Override
                public void setKey(int key) {
                    ((Bind) setting.getValue()).setKey(key);
                }

                @Override
                public String getKeyName() {
                    return setting.getValue().toString();
                }

                @Override
                public IBoolean isVisible() {
                    return () -> {
                        if (!setting.isSub()) return true;
                        if (setting.getMaster().getValue() instanceof Boolean)
                            return (Boolean) setting.getMaster().getValue();
                        return true;
                    };
                }

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public String getDescription() {
                    return setting.getDescription();
                }
            };
        else
            return new ISetting<Void>() {
                @Override
                public Void getSettingState() {
                    return null;
                }

                @Override
                public Class<Void> getSettingClass() {
                    return Void.class;
                }

                @Override
                public IBoolean isVisible() {
                    return () -> {
                        if (!setting.isSub()) return true;
                        if (setting.getMaster().getValue() instanceof Boolean)
                            return (Boolean) setting.getMaster().getValue();
                        return true;
                    };
                }

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public String getDescription() {
                    return setting.getDescription();
                }
            };
    }
}
