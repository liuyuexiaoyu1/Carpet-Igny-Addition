package com.liuyue.igny.manager;

import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
//#if MC >= 12106
//$$ import net.minecraft.world.level.storage.TagValueInput;
//$$ import com.mojang.logging.LogUtils;
//$$ import net.minecraft.util.ProblemReporter;
//#endif

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockVaultManager extends BaseDataManager<BlockVaultManager.VaultData> {
    public static final BlockVaultManager INSTANCE = new BlockVaultManager();

    private VaultData data;

    public static class VaultData {
        public Map<String, String[]> vault = new HashMap<>();
        public Set<String> pendingRestore = new HashSet<>();
    }

    @Override protected String getFileName() { return "nightmarish_vault.json"; }
    @Override protected Type getDataType() { return new TypeToken<VaultData>(){}.getType(); }
    @Override public VaultData getDefaultData() { return new VaultData(); }

    @Override protected void applyData(VaultData data) {
        this.data = data != null ? data : new VaultData();
    }

    @Override public VaultData getCurrentData() {
        if (this.data == null) this.data = new VaultData();
        return this.data;
    }

    private String getDictKey(Level level, BlockPos pos) {
        return level.dimension().location() + ":" + pos.asLong();
    }

    public void storeBlock(Level level, BlockPos pos, BlockState state) {
        String key = getDictKey(level, pos);
        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        String snbt = "";

        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            //#if MC >= 12005
            HolderLookup.Provider provider = level.registryAccess();
            snbt = be.saveWithFullMetadata(provider).toString();
            //#else
            //$$ snbt = be.saveWithFullMetadata().toString();
            //#endif
        }

        getCurrentData().vault.put(key, new String[]{blockId, snbt});
    }

    public boolean has(Level level, BlockPos pos) {
        return getCurrentData().vault.containsKey(getDictKey(level, pos));
    }

    public void restoreBlock(Level level, BlockPos pos) {
        String[] info = getCurrentData().vault.remove(getDictKey(level, pos));
        getCurrentData().pendingRestore.remove(getDictKey(level, pos));

        if (info == null || info.length < 1) return;

        ResourceLocation rl = ResourceLocation.tryParse(info[0]);
        if (rl == null) return;

        Block block = BuiltInRegistries.BLOCK.
                //#if MC >= 12102
                //$$ getValue(rl);
                //#else
                        get(rl);
        //#endif

        if (block != Blocks.AIR) {
            level.setBlock(pos, block.defaultBlockState(), 2 | 16);
            if (info.length > 1 && !info[1].isEmpty()) {
                try {
                    //#if MC >= 12105
                    //$$ CompoundTag nbt = TagParser.parseCompoundFully(info[1]);
                    //#else
                    CompoundTag nbt = TagParser.parseTag(info[1]);
                    //#endif
                    BlockEntity be = level.getBlockEntity(pos);
                    if (be != null) {
                        //#if MC >= 12005
                        HolderLookup.Provider provider = level.registryAccess();
                        //#if MC >= 12106
                        //$$ ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(be.problemPath(), LogUtils.getLogger());
                        //$$ be.loadWithComponents(TagValueInput.create(reporter, provider, nbt));
                        //#else
                        be.loadWithComponents(nbt, provider);
                        //#endif
                        //#else
                        //$$ be.load(nbt);
                        //#endif
                        be.setChanged();
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    public void markPending(Level level, BlockPos pos) {
        if (has(level, pos)) {
            getCurrentData().pendingRestore.add(getDictKey(level, pos));
        }
    }
    public Set<String> getPendingRestore() { return getCurrentData().pendingRestore; }
    @Override protected StorageScope getScope() { return StorageScope.WORLD; }
    @Override protected SideRestraint getSideRestraint() { return SideRestraint.SERVER; }
}