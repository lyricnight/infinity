package me.lyric.infinity.manager.client;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FriendManager {

    private static List<Friend> friends = new ArrayList<>();

    public void init() {
        if (!directory.exists()) {
            try {
                directory.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loadFriends();
    }

    public static void unload() {
        saveFriends();
    }

    private static File directory;

    public void setDirectory(File directory) {
        FriendManager.directory = directory;
    }



    public static void saveFriends() {
        if (directory.exists()) {
            try (final Writer writer = new FileWriter(directory)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(friends));
            } catch (IOException e) {
                directory.delete();
            }
        }
    }

    public void loadFriends() {
        if (!directory.exists())
            return;

        try (FileReader inFile = new FileReader(directory)) {
            friends = new ArrayList<>(new GsonBuilder().setPrettyPrinting().create().fromJson(inFile, new TypeToken<ArrayList<Friend>>() {
            }.getType()));
        } catch (Exception ignored) {}
    }

    public void addFriend(String name) {
        ChatUtils.sendMessage(ChatFormatting.BOLD + "Added " +ChatFormatting.BLUE + name + ChatFormatting.RESET + ChatFormatting.BOLD + " as a friend!");
        friends.add(new Friend(name));
    }

    public final Friend getFriend(String ign) {
        for (Friend friend : friends) {
            if (friend.getName().equalsIgnoreCase(ign))
                return friend;
        }
        return null;
    }

    public final boolean isFriend(String ign) {
        return getFriend(ign) != null;
    }

    public boolean isFriend(EntityPlayer ign) {
        return getFriend(ign.getName()) != null;
    }

    public void clearFriends() {
        int val = friends.size();
        friends.clear();
        ChatUtils.sendMessage(ChatFormatting.BOLD + "Successfully cleared " + val + " friends!");
    }

    public void returnAllFriends()
    {
        StringBuilder message = new StringBuilder(ChatFormatting.BOLD +"Friends: ");
        for (int i = 0; i <= friends.size(); i++)
        {
            message.append(friends.get(i).getName()).append(" ");
        }
        ChatUtils.sendMessage(String.valueOf(message));
    }



    public void removeFriend(String name) {
        Friend f = getFriend(name);
        if (f != null)
            friends.remove(f);
        ChatUtils.sendMessage(ChatFormatting.BOLD + "Removed " +ChatFormatting.RED + name + ChatFormatting.RESET + ChatFormatting.BOLD + " as a friend!");
    }

    public static final class Friend  {

        final String name;

        public Friend(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

    }

}
