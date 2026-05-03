package com.liuyue.igny.mixins.rule.noCreativeDestroyAttachmentDrops;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
//#if MC > 11904
import net.minecraft.world.level.storage.loot.LootParams;
//#else
//$$ import net.minecraft.world.level.storage.loot.LootContext;
//#endif
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;
import java.util.List;

@Mixin(FaceAttachedHorizontalDirectionalBlock.class)
public class FaceAttachedHorizontalDirectionalBlockMixin extends Block {
    public FaceAttachedHorizontalDirectionalBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    //#if MC > 11904
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    //#else
    //$$ public @NotNull List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    //#endif
    {
        if (IGNYSettings.noCreativeDestroyAttachmentDrops && IGNYSettings.CREATIVE_BREAKING.get()) {
            return Collections.emptyList();
        }
        return super.getDrops(state, builder);
    }
}
