package com.liuyue.igny.utils.itemFlowTracker.display;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class DisplayIndex<K, V extends HighlightEntry> {
    private final Map<ResourceKey<Level>, Map<K, V>> levels = new HashMap<>();

    public Map<K, V> view(ResourceKey<Level> dimension) {
        Map<K, V> entries = this.levels.get(dimension);
        return entries == null ? Collections.emptyMap() : entries;
    }

    @Nullable
    public V get(ResourceKey<Level> dimension, K key) {
        Map<K, V> entries = this.levels.get(dimension);
        return entries == null ? null : entries.get(key);
    }

    public void put(ResourceKey<Level> dimension, K key, V entry) {
        this.levels.computeIfAbsent(dimension, ignored -> new HashMap<>()).put(key, entry);
    }

    @Nullable
    public V remove(ResourceKey<Level> dimension, K key) {
        Map<K, V> entries = this.levels.get(dimension);
        return entries == null ? null : entries.remove(key);
    }

    public boolean active() {
        for (Map<K, V> entries : this.levels.values()) {
            if (!entries.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public void forEach(Consumer<V> sink) {
        for (Map<K, V> entries : this.levels.values()) {
            for (V entry : entries.values()) {
                sink.accept(entry);
            }
        }
    }

    public void clear() {
        this.levels.clear();
    }
}
