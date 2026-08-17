package com.liuyue.igny.mixins.rule.noteBlockNoteUnchanged;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NoteBlock.class)
public class NoteBlockMixin {
    @Shadow
    @Final
    public static IntegerProperty NOTE;

    @WrapOperation(
            //#if MC >= 12006
            method = "useWithoutItem",
            //#else
            //$$ method = "use",
            //#endif
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;cycle(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Object;"))
    private Object cycle(BlockState instance, Property<?> property, Operation<Object> original) {
        if (IGNYSettings.NOTE_BLOCK_NOTE_UNCHANGED.value()) {
            return instance;
        }
        return original.call(instance, property);
    }

    //#if MC >= 26.3
    //$$ @WrapOperation(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    //$$ private boolean setBlock(Level instance, BlockPos pos, BlockState state, Operation<Boolean> original)
    //#else
    @WrapOperation(
            //#if MC >= 12006
            method = "useWithoutItem",
            //#else
            //$$ method = "use",
            //#endif
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean setBlock(Level instance, BlockPos pos, BlockState newState, int flags, Operation<Boolean> original)
    //#endif
    {
        if (IGNYSettings.NOTE_BLOCK_NOTE_UNCHANGED.value()) {
            //#if MC >= 26.3
            //$$ original.call(instance, pos, state.cycle(NOTE));
            //$$ return instance.setBlock(pos, state, 2 | 16);
            //#else
            original.call(instance, pos, newState.cycle(NOTE), flags);
            return original.call(instance, pos,  newState, 2 | 16);
            //#endif
        }
        //#if MC >= 26.3
        //$$ return original.call(instance, pos, state);
        //#else
        return original.call(instance, pos, newState, flags);
        //#endif
    }
}
