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

    @WrapOperation(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;cycle(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Object;"))
    private Object cycle(BlockState instance, Property<?> property, Operation<Object> original) {
        if (IGNYSettings.NOTE_BLOCK_NOTE_UNCHANGED.value()) {
            return instance;
        }
        return original.call(instance, property);
    }

    @WrapOperation(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean setBlock(Level instance, BlockPos pos, BlockState newState, int flags, Operation<Boolean> original) {
        if (IGNYSettings.NOTE_BLOCK_NOTE_UNCHANGED.value()) {
            original.call(instance, pos, newState.cycle(NOTE), flags);
            return original.call(instance, pos,  newState, 2 | 16);
        }
        return original.call(instance, pos, newState, flags);
    }
}
