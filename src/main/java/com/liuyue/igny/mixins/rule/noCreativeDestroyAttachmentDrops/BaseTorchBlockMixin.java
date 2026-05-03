package com.liuyue.igny.mixins.rule.noCreativeDestroyAttachmentDrops;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseTorchBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;
import java.util.List;

@Mixin(BaseTorchBlock.class)
public class BaseTorchBlockMixin extends Block {
    public BaseTorchBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (IGNYSettings.noCreativeDestroyAttachmentDrops && IGNYSettings.CREATIVE_BREAKING.get()) {
            return Collections.emptyList();
        }
        return super.getDrops(state, builder);
    }
}
