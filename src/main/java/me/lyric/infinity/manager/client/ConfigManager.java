package me.lyric.infinity.manager.client;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.setting.settings.*;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.manager.Managers;

import java.awt.*;
import java.io.*;
import java.util.Iterator;

/**
 * @author vikas
 */

public class ConfigManager implements IGlobals {
    public static File socialsPath;
    static File path;
    public void init()
    {
        socialsPath = new File(this.mc.gameDir + File.separator + "Infinity" + File.separator + "Socials");
        if (!socialsPath.exists()) {
            socialsPath.mkdirs();
        }
        Infinity.LOGGER.info("Loaded socials paths.");
        path = new File(this.mc.gameDir + File.separator + "Infinity" + File.separator + "Configs");
        if (!path.exists()) {
            path.mkdirs();
        }
        Infinity.LOGGER.info("Loaded config paths.");
        if (!getActiveConfig().equals("0")) {
            Infinity.LOGGER.info("loading config from init() :" + getActiveConfig());
            load(getActiveConfig());
        }
        else
        {
            Infinity.LOGGER.info("loading default.");
            load("0");
        }
    }

    public String[] getAllConfigs()
    {
        try {
            path = new File(mc.gameDir + File.separator + "Infinity" + File.separator + "Configs" + File.separator);
            if (!path.exists())
            {
                return new String[]{""};
            }
            return path.list();
        }
        catch (Exception e)
        {
            Infinity.LOGGER.info("Exception in all configs.");
            e.printStackTrace();
            return new String[]{""};
        }
    }

    public static void save(String folder) {
        path = new File(mc.gameDir + File.separator + "Infinity" + File.separator + "Configs" + File.separator + folder);
        if (!path.exists()) {
            path.mkdirs();
        }
        Infinity.LOGGER.info("saving config " + folder);
        saveModuleFile();
        saveActiveConfig(folder);
        savePrefix();
    }

    public static boolean load(String folder) {
        path = new File(mc.gameDir + File.separator + "Infinity" + File.separator + "Configs" + File.separator + folder);
        if (!path.exists()) {
            Infinity.LOGGER.info("config does not exist: " + folder);
            return false;
        }
        Infinity.LOGGER.info("loading config " + folder);
        setModuleValue();
        setModuleBind();
        setModuleSettingValues();
        saveActiveConfig(folder);
        return true;
    }

    public static void savePlayer() {
        saveFriendList(socialsPath);
    }

    public static void loadPlayer() {
        loadFriendList(socialsPath);
    }


    public static void savePrefix() {
        try {
            File file = new File(mc.gameDir + File.separator + "Infinity" + File.separator + "Prefix.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            Infinity.LOGGER.info("saving prefix.");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(Managers.COMMANDS.getPrefix());
            bufferedWriter.close();
        }
        catch (Exception exception) {
            Infinity.LOGGER.info("prefix exception.");
            exception.printStackTrace();
        }
    }

    public static String getPrefix() {
        try {
            File file = new File(mc.gameDir + File.separator + "Infinity" + File.separator + "Prefix.txt");
            if (!file.exists()) {
                Infinity.LOGGER.info("prefix returned incorrect.");
                return ".";
            }
            Infinity.LOGGER.info("loading prefix.");
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
            String line = bufferReader.readLine();
            bufferReader.close();
            Infinity.LOGGER.info("getPrefix returned:" + line);
            return line;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            Infinity.LOGGER.info("prefix returned incorrect.");
            return ".";
        }
    }

    public static void saveActiveConfig(String folder) {
        try {
            File file = new File(mc.gameDir + File.separator + "Infinity" + File.separator + "ActiveConfig.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(folder);
            bufferedWriter.close();
            Infinity.LOGGER.info("ActiveConfig saved.");
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String getActiveConfig() {
        try {
            File file = new File(mc.gameDir + File.separator + "Infinity" + File.separator + "ActiveConfig.txt");
            if (!file.exists()) {
                Infinity.LOGGER.info("getActiveConfig returned 0.");
                return "0";
            }
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
            String line = bufferReader.readLine();
            bufferReader.close();
            Infinity.LOGGER.info("getActiveConfig returned:" + line);
            return line;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            Infinity.LOGGER.info("getActiveConfig returned incorrect.");
            return "0";
        }
    }

    public static void saveFriendList(File path) {
        try {
            File file = new File(path + File.separator + File.separator + "Friends.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (FriendManager.FriendPlayer friendPlayer : Managers.FRIENDS.friendList) {
                bufferedWriter.write(friendPlayer.getName());
                bufferedWriter.write("\r\n");
            }
            bufferedWriter.close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void loadFriendList(File path) {
        try {
            File file = new File(path + File.separator + "Friends.txt");
            if (!file.exists()) {
                return;
            }
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
            bufferReader.lines().forEach(Managers.FRIENDS::addFriend);
            bufferReader.close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void saveModuleFile() {
        try {
            for (Module module : Managers.MODULES.getModules()) {
                File file;
                File categoryPath = new File(path + File.separator + module.category.toString());
                if (!categoryPath.exists()) {
                    categoryPath.mkdirs();
                }
                if (!(file = new File(categoryPath.getAbsolutePath(), module.name + ".txt")).exists()) {
                    file.createNewFile();
                }
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write("State : " + (module.isEnabled() ? "Enabled" : "Disabled"));
                bufferedWriter.write("\r\n");
                for (Setting<?> setting : module.settingList) {
                    if (setting.getName().equals("Keybind") || setting.getName().equals("Enabled")) continue;
                    if (setting instanceof StringSetting) {
                        bufferedWriter.write(setting.getName() + " : " + setting.getValue());
                        bufferedWriter.write("\r\n");
                        continue;
                    }
                    if (setting instanceof ColorSetting) {
                        bufferedWriter.write(setting.getName() + " : " + (((ColorSetting)setting).getValue()).getRGB());
                        bufferedWriter.write("\r\n");
                        continue;
                    }
                    bufferedWriter.write(setting.getName() + " : " + setting.getValue());
                    bufferedWriter.write("\r\n");
                }
                bufferedWriter.write("Keybind : " + module.bind.getValue());
                bufferedWriter.close();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void setModuleValue() {
        for (Module module : Managers.MODULES.getModules()) {
            try {
                File file;
                File categoryPath = new File(path + File.separator + module.category.toString());
                if (!categoryPath.exists() || !(file = new File(categoryPath.getAbsolutePath(), module.name + ".txt")).exists()) continue;
                FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
                bufferReader.lines().forEach(line -> {
                    String clarification = line.split(" : ")[0];
                    String state = line.split(" : ")[1];
                    if (clarification.equals("State")) {
                        if (state.equals("Enabled")) {
                            module.enable();
                        } else if (state.equals("Disabled")) {
                            module.disable();
                        }
                    }
                });
                bufferReader.close();
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void setModuleBind() {
        for (Module module : Managers.MODULES.getModules()) {
            try {
                File file;
                File categoryPath = new File(path + File.separator + module.category.toString());
                if (!categoryPath.exists() || !(file = new File(categoryPath.getAbsolutePath(), module.name + ".txt")).exists()) continue;
                FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
                bufferReader.lines().forEach(line -> {
                    String clarification = line.split(" : ")[0];
                    String state = line.split(" : ")[1];
                    if (clarification.equals("Keybind")) {
                        if (state.equals("0")) {
                            return;
                        }
                        module.bind.setValue(Integer.parseInt(state));
                    }
                });
                bufferReader.close();
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void setModuleSettingValues() {
        for (Module module : Managers.MODULES.getModules()) {
            try {
                File file;
                File categoryPath = new File(path.getAbsolutePath() + File.separator + module.category.toString());
                if (!categoryPath.exists() || !(file = new File(categoryPath.getAbsolutePath(), module.name + ".txt")).exists()) continue;
                FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
                bufferReader.lines().forEach(line -> {
                    String clarification = line.split(" : ")[0];
                    String state = line.split(" : ")[1];
                    Iterator<Setting<?>> iterator = module.settingList.stream().iterator();
                    while (iterator.hasNext())
                    {
                        Setting setting = iterator.next();
                        if (setting.getName().equals(clarification)) {
                            if (setting instanceof StringSetting) {
                                setting.setValue(state);
                            }
                            if (setting instanceof IntegerSetting) {
                                setting.setValue(Integer.parseInt(state));
                            }
                            if (setting instanceof FloatSetting) {
                                setting.setValue(Float.parseFloat(state));
                            }
                            if (setting instanceof BooleanSetting) {
                                setting.setValue(Boolean.parseBoolean(state));
                            }
                            if (setting instanceof KeySetting) {
                                setting.setValue(Integer.parseInt(state));
                            }
                            if (setting instanceof ColorSetting) {
                                ((ColorSetting)setting).setColor(new Color(Integer.parseInt(state), true));
                            }
                            if (setting instanceof ModeSetting) {
                                setting.setValue(state);
                            }
                            else {
                                continue;
                            }
                        }
                    }
                });
                bufferReader.close();
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
