package com.liuyue.igny.mixins.rule.magmaBlockMelt;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
//#if MC >= 12101
import net.minecraft.tags.EnchantmentTags;
//#else
//$$ import net.minecraft.world.item.enchantment.Enchantments;
//#endif
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin extends Block {
    public MagmaBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);
        //#if MC >= 12101
        if (IGNYSettings.MAGMA_BLOCK_MELT.value() && !EnchantmentHelper.hasTag(tool, EnchantmentTags.PREVENTS_ICE_MELTING))
        //#else
        //$$ if (IGNYSettings.MAGMA_BLOCK_MELT.value() && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) == 0)
        //#endif
        {
            level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
            dropResources(Blocks.AIR.defaultBlockState(), level, pos, blockEntity, player, tool);
        } else {
            dropResources(state, level, pos, blockEntity, player, tool);
        }
    }
}
