package com.liuyue.igny.manager;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;

public class CustomPickupDataManager extends BaseDataManager<Map<String, CustomPickupDataManager.PlayerSetting>> {
    public static final CustomPickupDataManager INSTANCE = new CustomPickupDataManager();
    private final Map<String, PlayerSetting> settings = new HashMap<>();

    @Override protected String getFileName() { return "custom_player_pickup.json"; }
    @Override protected Type getDataType() { return new TypeToken<Map<String, PlayerSetting>>(){}.getType(); }
    @Override public Map<String, PlayerSetting> getDefaultData() { return new HashMap<>(); }
    @Override protected StorageScope getScope() {return StorageScope.WORLD;}
    @Override protected SideRestraint getSideRestraint() {return SideRestraint.SERVER;}

    @Override protected void applyData(Map<String, PlayerSetting> data) {
        settings.clear();
        settings.putAll(data);
    }

    @Override public Map<String, PlayerSetting> getCurrentData() { return settings; }

    public PlayerSetting getOrCreate(String playerName) {
        return settings.computeIfAbsent(playerName.toLowerCase(Locale.ROOT), k -> new PlayerSetting());
    }

    public void updateAndSave(String playerName, PlayerSetting setting) {
        settings.put(playerName.toLowerCase(Locale.ROOT), setting);
        this.save();
    }

    public boolean canPickUp(String playerName, String itemId) {
        PlayerSetting setting = settings.get(playerName.toLowerCase(Locale.ROOT));
        return setting == null || setting.canPickUp(itemId);
    }

    public enum Mode {
        @SerializedName("disabled") DISABLED,
        @SerializedName("whitelist") WHITELIST,
        @SerializedName("blacklist") BLACKLIST
    }

    public static class PlayerSetting {
        private Mode mode = Mode.DISABLED;
        private final Set<String> items = Collections.newSetFromMap(new HashMap<>());

        public Mode getMode() { return mode; }
        public void setMode(Mode mode) { this.mode = mode; }
        public void setItems(Collection<String> newItems) {
            this.items.clear();
            newItems.forEach(i -> this.items.add(i.toLowerCase(Locale.ROOT)));
        }
        public Set<String> getItems() { return items; }

        public boolean canPickUp(String itemId) {
            if (mode == Mode.DISABLED) return true;
            boolean contains = items.contains(itemId.toLowerCase(Locale.ROOT));
            return (mode == Mode.WHITELIST) == contains;
        }
    }
}