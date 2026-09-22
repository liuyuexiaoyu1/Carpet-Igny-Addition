package com.liuyue.igny.mixins.rule.uncraftingTable;

import com.liuyue.igny.helper.uncraftingTable.CrafterUncrafting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?< 12005 ? import net.minecraft.world.level.Level;

@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin {

    //#if >= 12005
    @Shadow
    protected abstract void dispenseItem(ServerLevel level, BlockPos pos, CrafterBlockEntity crafter, ItemStack stack, BlockState state, RecipeHolder<?> recipe);
    //#else
    /*$$@Shadow
    protected abstract void dispenseItem(Level level, BlockPos pos, CrafterBlockEntity crafter, ItemStack stack, BlockState state);$$*/
    //#endif

    @Inject(method = "dispenseFrom", at = @At(value = "HEAD"), cancellable = true)
    private void igny$uncraftFromResultSlot(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
        if (!(level.getBlockEntity(pos) instanceof CrafterBlockEntity crafter)) {
            return;
        }

        CrafterUncrafting.Result result = CrafterUncrafting.tryUncraft(level, crafter);

        if (result == null) {
            return;
        }

        for (ItemStack output : result.outputs()) {
            //#if >= 12005
            this.dispenseItem(level, pos, crafter, output, state, (RecipeHolder<?>) result.holder());
            //#else
            //$$ this.dispenseItem(level, pos, crafter, output, state);
            //#endif
        }
        ci.cancel();
    }
}
