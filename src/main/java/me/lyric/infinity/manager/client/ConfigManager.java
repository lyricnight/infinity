package me.lyric.infinity.manager.client;

import com.google.gson.*;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.config.Config;
import me.lyric.infinity.api.util.bytes.ByteChanger;
import me.lyric.infinity.api.util.metadata.MetaDataUtils;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.time.DateTimeUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lyric
 */

public class ConfigManager {
    public static final int SAVE = 331;
    public static final int LOAD = 332;

    public static ConfigManager configManager;
    public static Path PATH = Paths.get(Infinity.CONFIG_PATH);

    private final Set<Config> configSet = new HashSet<>();
    protected String policyProtectionPreset = "default";

    public ConfigManager() {
        configManager = this;
    }

    public static void implement(Config config) {
        if (configManager.configSet.contains(config)) {
            return;
        }

        configManager.configSet.add(config);
    }

    public static void exclude(Config config) {
        configManager.configSet.remove(config);

        final String path = Infinity.CONFIG_PATH + config.getTag();

        if (Files.exists(Paths.get(path))) {
            try {
                FileUtils.deleteDirectory(new File(path));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void sync(Config config) {
        final Config current = current();

        set(config);
        reload();

        process(SAVE);

        if (current != null) {
            set(current);
            reload();
        }
    }

    public static Config get(String name) {
        Config config = null;

        for (Config presets : configManager.getPresetSet()) {
            if (presets.getName().equalsIgnoreCase(name)) {
                config = presets;

                break;
            }
        }

        return config;
    }

    public static boolean contains(String name) {
        return get(name) != null;
    }

    public static void refresh() {
        final Map<String, MetaDataUtils> map = configManager.findForPresetMap();

        for (Map.Entry<String, MetaDataUtils> entry : map.entrySet()) {
            final String name = entry.getKey();
            final MetaDataUtils preset = entry.getValue();

            Config theConfig = get(name);

            if (theConfig != null) {
                continue;
            }

            final JsonObject metadata = preset.getMetaData();

            theConfig = new Config(name, name.toLowerCase(), DateTimeUtils.time(DateTimeUtils.TIME_AND_DATE));

            if (metadata != null && (metadata.get("data") != null || metadata.get("current") != null)) {
                if (metadata.get("data") != null) {
                    theConfig.setData(metadata.get("data").getAsString());
                }

                if (metadata.get("current") != null) {
                    boolean isValid = ByteChanger.byteToBoolean(metadata.get("current").getAsByte());

                    if (isValid) {
                        theConfig.setValidator();
                    } else {
                        theConfig.unsetValidator();
                    }
                }
            }

            implement(theConfig);

            ChatUtils.sendMessage("Found " + theConfig.getName() + " new config!");
        }
    }

    public static void reload() {
        boolean containsOne = false;

        for (Config presets : configManager.getPresetSet()) {
            if (presets.isCurrent()) {
                configManager.setPolicyProtectionPreset(presets.getTag().toLowerCase());

                containsOne = true;

                break;
            }
        }

        if (!containsOne) {
            final Config config = new Config("Default", "default", DateTimeUtils.time(DateTimeUtils.TIME_AND_DATE));

            implement(config);
            configManager.setPolicyProtectionPreset(config.getTag().toLowerCase());

            set(config);
        }
    }

    public static void process(int protocol) {
        switch (protocol) {
            case SAVE: {
                for (Config presets : configManager.getPresetSet()) {
                    configManager.updateMetaData(presets);

                    if (presets.isCurrent()) {
                        configManager.doSaveClient();
                    }
                }

                break;
            }

            case LOAD: {
                for (Config presets : configManager.getPresetSet()) {
                    if (presets.isCurrent()) {
                        configManager.doLoadClient();

                        break;
                    }
                }

                break;
            }
        }
    }

    public static Config current() {
        Config config = null;

        for (Config presets : configManager.getPresetSet()) {
            if (presets.isCurrent()) {
                config = presets;

                break;
            }
        }

        return config;
    }

    public static void set(Config config) {
        for (Config presets : configManager.getPresetSet()) {
            if (presets.getName().equalsIgnoreCase(config.getName())) {
                presets.setValidator();
            } else {
                presets.unsetValidator();
            }
        }
    }

    public static void info() {
        final StringBuilder stringBuilder = new StringBuilder();

        for (Config presets : configManager.getPresetSet()) {
            stringBuilder.append(presets.getName()).append("; ");
        }

        ChatUtils.sendMessage(stringBuilder.toString());
    }

    public void init() {
    }

    public Set<Config> getPresetSet() {
        return configSet;
    }

    public Map<String, MetaDataUtils> findForPresetMap() {
        final HashMap<String, MetaDataUtils> map = new HashMap<>();

        if (!Files.exists(PATH)) {
            try {
                Files.createDirectories(PATH);
            } catch (IOException exc) {
                exc.printStackTrace();
            }

            return map;
        }

        final File[] fileList = new File(Infinity.CONFIG_PATH).listFiles();

        if (fileList != null) {
            int i = 0;

            for (int j = fileList.length; i < j; i++) {
                final File file = fileList[i];

                if (!file.isDirectory() || contains(file.getName())) {
                    continue;
                }

                map.put(file.getName(), this.getPresetEnum(file.getPath()));
            }
        }

        return map;
    }

    public MetaDataUtils getPresetEnum(final String path) {
        final String concurrentPath = path + "/" + "Validator.json";

        MetaDataUtils theEnum = MetaDataUtils.CACHE;

        if (!Files.exists(Paths.get(concurrentPath))) {
            return theEnum;
        }

        try {
            JsonParser jsonParser = new JsonParser();

            InputStream file = Files.newInputStream(Paths.get(concurrentPath));
            JsonObject mainJson = jsonParser.parse(new InputStreamReader(file)).getAsJsonObject();

            if (mainJson != null) {
                theEnum = MetaDataUtils.LOADABLE;
                theEnum.setMetaData(mainJson);
            }

            file.close();
        } catch (IOException exc) {
            theEnum = MetaDataUtils.CACHE;
        }

        return theEnum;
    }

    public void updateMetaData(Config config) {
        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jsonParser = new JsonParser();

        String superiorFolder = Infinity.CONFIG_PATH + config.getTag();
        String superiorFile = superiorFolder + "/Validator.json";

        try {
            if (!Files.exists(Paths.get(superiorFolder))) {
                Files.createDirectories(Paths.get(superiorFolder));
            }

            if (Files.exists(Paths.get(superiorFile))) {
                java.io.File file = new java.io.File(superiorFile);
                file.delete();
            }

            Files.createFile(Paths.get(superiorFile));

            JsonObject metadata = new JsonObject();

            metadata.add("name", new JsonPrimitive(config.getName()));
            metadata.add("tag", new JsonPrimitive(config.getTag()));
            metadata.add("data", new JsonPrimitive(config.getData()));
            metadata.add("current", new JsonPrimitive(config.getCertification()));

            String stringJson = gsonBuilder.toJson(jsonParser.parse(metadata.toString()));
            OutputStreamWriter fileOutputStream = new OutputStreamWriter(new FileOutputStream(superiorFile), StandardCharsets.UTF_8);

            fileOutputStream.write(stringJson);
            fileOutputStream.close();
        } catch (IOException exc) {
        }
    }

    public void setPolicyProtectionPreset(String preset) {
        policyProtectionPreset = preset;
    }

    public String getCurrentPresetPath() {
        return Infinity.CONFIG_PATH + this.policyProtectionPreset + "/";
    }

    public void doSaveClient() {
        ModuleManager.moduleManager.onSave();
    }

    public void doLoadClient() {
        ModuleManager.moduleManager.onLoad();
        ModuleManager.moduleManager.onReload();
    }
}