package me.lyric.infinity.manager.client;

import java.util.ArrayList;

public class FriendManager {

    public ArrayList<FriendPlayer> friendList;

    public FriendManager() {
        friendList = new ArrayList<>();
    }

    public void addFriend(String name) {
        if (!isFriend(name)) {
            friendList.add(new FriendPlayer(name));
        }
    }

    public void removeFriend(String name) {
        friendList.removeIf(player -> player.getName().equals(name));
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