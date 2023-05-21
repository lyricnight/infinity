package me.lyric.infinity.api.module;

import com.google.gson.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.setting.Register;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.Bind;
import me.lyric.infinity.api.setting.settings.ColorPicker;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.impl.modules.client.Notifications;
import me.lyric.infinity.manager.client.AnimationManager;
import me.lyric.infinity.manager.client.ConfigManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author CPacketCustomPayload
 * stuff added by lyric
 */

public class Module extends Register implements IGlobals {

    private final String name;
    private final String description;

    private final Category category;

    private final AnimationManager animation;

    private Supplier<String> info;

    private final Setting<Bind> bind = new Setting<>("Bind", "Key bind for the module.", new Bind());
    private final Setting<Boolean> drawn = new Setting<>("Drawn", "Draws the module on the ArrayList when enabled.", true);

    public Module(final String name, final String description, final Category category) {
        super();

        this.name = name;
        this.description = description;
        this.category = category;

        this.register(bind);
        this.register(drawn);

        animation = new AnimationManager(150, this.isEnabled());
    }

    public Module(final String name, final String description, final Category category, Supplier<String> info) {
        super();

        this.name = name;
        this.description = description;
        this.category = category;
        this.info = info;

        this.register(bind);
        this.register(drawn);

        animation = new AnimationManager(150, this.isEnabled());
    }
    public String getName() {
        return name;
    }

    public boolean isDrawn() {
        return drawn.getValue();
    }

    public AnimationManager getAnimation() {
        return animation;
    }

    public String getDisplayInfo() {
        return info != null ? info.get() : "";
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }
    public void onTotemPop(EntityPlayer player)
    {

    }
    public void onDeath(EntityPlayer player){

    }

    public boolean isEnabled() {
        return this.bind.getValue().isState();
    }
    public final boolean isDisabled() {
        return !isEnabled();
    }

    public void setEnabled(boolean state) {
        if (state != this.bind.getValue().isState()) {
            this.bind.getValue().setState(state);
            this.reloadListener();
        }
    }

    public Bind getBind() {
        return this.bind.getValue();
    }

    public void onSetting() {
    }

    public void onRender2D(float partialTicks) {
    }

    public void onRender3D(float partialTicks) {
    }

    public void onTick() {
    }

    public void onLogout() {
    }

    public void onUpdate() {
    }

    protected void onEnable() {
            animation.setState(true);
            MinecraftForge.EVENT_BUS.register(this);
            Infinity.INSTANCE.eventBus.subscribe(this);
    }

    protected void onDisable() {
            animation.setState(false);
            MinecraftForge.EVENT_BUS.unregister(this);
            Infinity.INSTANCE.eventBus.unsubscribe(this);
    }
    public void toggle() {
        this.setEnabled(!this.isEnabled());
    }

    public void reloadListener() {
        if (this.bind.getValue().isState()) {
            this.setListener();
        } else {
            this.unsetListener();
        }
    }

    public void setListener() {
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(Notifications.class).isEnabled() && Infinity.INSTANCE.moduleManager.getModuleByClass(Notifications.class).modules.getValue()) {
            ChatUtils.sendMessageWithID(ChatFormatting.BOLD + this.name + " " + ChatFormatting.RESET + ChatFormatting.GREEN + "enabled!", hashCode());
        }
        this.bind.getValue().setState(true);

        Infinity.INSTANCE.eventBus.subscribe(this);
        MinecraftForge.EVENT_BUS.register(this);
        this.onEnable();
    }

    public void unsetListener() {
            if (Infinity.INSTANCE.moduleManager.getModuleByClass(Notifications.class).isEnabled() && Infinity.INSTANCE.moduleManager.getModuleByClass(Notifications.class).modules.getValue()) {
                ChatUtils.sendMessageWithID(ChatFormatting.BOLD + this.name + " " + ChatFormatting.RESET + ChatFormatting.RED + "disabled!", hashCode());
            }
            this.bind.getValue().setState(false);
            Infinity.INSTANCE.eventBus.unsubscribe(this);
            MinecraftForge.EVENT_BUS.unregister(this);
            this.onDisable();
    }

    public void onSave() {
        try {
            String pathFolder = ConfigManager.configManager.getCurrentPresetPath() + "/module/" + this.category.name().toLowerCase() + "/";
            String pathFile = pathFolder + this.getName() + ".json";

            Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jsonParser = new JsonParser();

            if (Files.exists(Paths.get(pathFolder)) == false) {
                Files.createDirectories(Paths.get(pathFolder));
            }

            if (Files.exists(Paths.get(pathFile))) {
                java.io.File file = new java.io.File(pathFile);
                file.delete();
            }

            Files.createFile(Paths.get(pathFile));

            JsonObject mainJson = new JsonObject();
            JsonObject jsonSettingList = new JsonObject();

            for (Map.Entry<String, Setting> entry : this.getSettings().entrySet()) {
                final String name = entry.getKey();
                final Setting setting = entry.getValue();

                if (setting.getValue() instanceof Boolean) {
                    jsonSettingList.add(setting.getName(), new JsonPrimitive((Boolean) setting.getValue()));
                }
                if (setting.getValue() instanceof String) {
                    jsonSettingList.add(setting.getName(), new JsonPrimitive((String) setting.getValue()));
                }

                if (setting.getValue() instanceof Number) {
                    jsonSettingList.add(setting.getName(), new JsonPrimitive((Number) setting.getValue()));
                }

                if (setting.getValue() instanceof Enum) {
                    jsonSettingList.add(setting.getName(), new JsonPrimitive(((Enum) setting.getValue()).name()));
                }

                if (setting.getValue() instanceof Bind) {
                    JsonObject object = new JsonObject();

                    final Bind bind = (Bind) setting.getValue();

                    object.add("key", new JsonPrimitive(bind.getKey()));
                    object.add("state", new JsonPrimitive(bind.isState()));

                    jsonSettingList.add(setting.getName(), object);
                }

                if (setting.getValue() instanceof ColorPicker) {
                    JsonObject object = new JsonObject();

                    final ColorPicker colorPicker = (ColorPicker) setting.getValue();

                    object.add("red", new JsonPrimitive(colorPicker.getColor().getRed()));
                    object.add("green", new JsonPrimitive(colorPicker.getColor().getGreen()));
                    object.add("blue", new JsonPrimitive(colorPicker.getColor().getBlue()));
                    object.add("alpha", new JsonPrimitive(colorPicker.getColor().getAlpha()));
                    object.add("rgb", new JsonPrimitive(colorPicker.isRGB()));

                    jsonSettingList.add(setting.getName(), object);
                }
            }

            mainJson.add("settings", jsonSettingList);

            String stringJson = gsonBuilder.toJson(jsonParser.parse(mainJson.toString()));
            OutputStreamWriter fileOutputStream = new OutputStreamWriter(new FileOutputStream(pathFile), StandardCharsets.UTF_8);

            fileOutputStream.write(stringJson);
            fileOutputStream.close();
        } catch (IOException | IllegalStateException exc) {
        }
    }

    public void onLoad() {
        try {
            String pathFolder = ConfigManager.configManager.getCurrentPresetPath() + "/module/" + this.category.name().toLowerCase() + "/";
            String pathFile = pathFolder + this.getName() + ".json";

            if (!Files.exists(Paths.get(pathFile))) {
                return;
            }

            JsonParser jsonParser = new JsonParser();

            InputStream file = Files.newInputStream(Paths.get(pathFile));
            JsonObject mainJson = jsonParser.parse(new InputStreamReader(file)).getAsJsonObject();

            if (mainJson.get("settings") != null) {
                JsonObject jsonSettingList = mainJson.get("settings").getAsJsonObject();

                for (Map.Entry<String, Setting> entry : this.getSettings().entrySet()) {
                    final String name = entry.getKey();
                    final Setting setting = entry.getValue();

                    if (jsonSettingList.get(name) == null) {
                        continue;
                    }

                    if (setting.getValue() instanceof Boolean) {
                        setting.setValue(jsonSettingList.get(setting.getName()).getAsBoolean());
                    }
                    if (setting.getValue() instanceof String) {
                        setting.setValue(jsonSettingList.get(setting.getName()).getAsString());
                    }

                    if (setting.getValue() instanceof Number) {
                        if (setting.getValue() instanceof Float) {
                            setting.setValue(jsonSettingList.get(setting.getName()).getAsFloat());
                        }

                        if (setting.getValue() instanceof Double) {
                            setting.setValue(jsonSettingList.get(setting.getName()).getAsDouble());
                        }

                        if (setting.getValue() instanceof Integer) {
                            setting.setValue(jsonSettingList.get(setting.getName()).getAsInt());
                        }
                    }

                    if (setting.getValue() instanceof Enum) {
                        for (Enum enums : ((Enum) setting.getValue()).getClass().getEnumConstants()) {
                            if (jsonSettingList.get(setting.getName()).getAsString().equalsIgnoreCase(enums.name())) {
                                setting.setValue(enums);

                                break;
                            }
                        }
                    }

                    if (setting.getValue() instanceof Bind) {
                        final Bind bind = (Bind) setting.getValue();

                        if (jsonSettingList.get(setting.getName()) != null) {
                            JsonObject object = jsonSettingList.get(setting.getName()).getAsJsonObject();

                            if (object.get("key") != null) {
                                bind.setKey(object.get("key").getAsInt());
                            }

                            if (object.get("state") != null) {
                                bind.setState(object.get("state").getAsBoolean());
                            }
                        }
                    }

                    if (setting.getValue() instanceof ColorPicker) {
                        final ColorPicker colorPicker = (ColorPicker) setting.getValue();

                        if (jsonSettingList.get(setting.getName()) != null) {
                            JsonObject object = jsonSettingList.get(setting.getName()).getAsJsonObject();

                            final Color color = new Color(object.get("red").getAsInt(), object.get("green").getAsInt(), object.get("blue").getAsInt(), object.get("alpha").getAsInt());

                            colorPicker.setColor(color);

                            if (object.get("rgb") != null && object.get("rgb").getAsBoolean()) {
                                colorPicker.setRGB();
                            } else {
                                colorPicker.unsetRGB();
                            }

                            colorPicker.updateSB();
                        }
                    }
                }
            }

            file.close();
        } catch (IOException | IllegalStateException exc) {
        }
    }

    protected boolean nullSafe() {
        return mc.player != null && mc.world != null;
    }
}
