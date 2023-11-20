package me.lyric.infinity.manager.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;

import java.util.ArrayList;
import java.util.Objects;

public class FriendManager {

    public ArrayList<FriendPlayer> friendList;

    public FriendManager() {
        friendList = new ArrayList<>();
    }

    public void addFriend(String name) {
        if (!isFriend(name)) {
            friendList.add(new FriendPlayer(name));
            ChatUtils.sendMessage(ChatFormatting.AQUA + name + " has been added to your friend list.");
        }
        else
        {
            ChatUtils.sendMessage(ChatFormatting.RED + name + " is already a friend!");
        }
    }

    public void removeFriend(String name) {
        if (!isFriend(name))
        {
            ChatUtils.sendMessage(ChatFormatting.RED + name + " is not a friend!");
            return;
        }
        for(FriendPlayer player : friendList)
        {
            if (Objects.equals(player.getName(), name))
            {
                ChatUtils.sendMessage(ChatFormatting.GREEN + name + " has been deleted from your friend list.");
                friendList.remove(player);
            }
        }
    }

    public boolean isFriend(String name) {
        return friendList.stream().anyMatch(player -> player.getName().equals(name));
    }

    public static class FriendPlayer
    {
        String name;

        public FriendPlayer(final String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

}