package com.liuyue.igny.manager;

import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AmethystVaultManager extends BaseDataManager<Map<Long, String>> {
    public static final AmethystVaultManager INSTANCE = new AmethystVaultManager();

    private Map<Long, String> vault = new HashMap<>();

    @Override
    protected String getFileName() {
        return "amethyst_vault.json";
    }

    @Override
    protected Type getDataType() {
        return new TypeToken<Map<Long, String>>(){}.getType();
    }

    @Override
    public Map<Long, String> getDefaultData() {
        return new HashMap<>();
    }

    @Override
    protected void applyData(Map<Long, String> data) {
        this.vault = data != null ? data : new HashMap<>();
    }

    @Override
    public Map<Long, String> getCurrentData() {
        return this.vault;
    }

    @Override
    protected StorageScope getScope() {
        return StorageScope.WORLD;
    }

    @Override
    protected SideRestraint getSideRestraint() {
        return SideRestraint.SERVER;
    }

    public void storeBud(BlockPos pos, BlockState state) {
        String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        vault.put(pos.asLong(), id);
        this.save();
    }

    public BlockState getAndRemove(BlockPos pos) {
        String id = vault.remove(pos.asLong());
        if (id == null) return null;

        this.save();
        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null) return null;

        Block block = BuiltInRegistries.BLOCK.
        //#if MC >= 12102
        //$$ getValue(rl);
        //#else
        get(rl);
        //#endif
        return (block != Blocks.AIR) ? block.defaultBlockState() : null;
    }

    public boolean has(BlockPos pos) {
        return vault.containsKey(pos.asLong());
    }
}