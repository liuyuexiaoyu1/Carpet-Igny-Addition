package com.liuyue.igny.mixins.rule.invisibleItemFrames;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.nbt.CompoundTag; //#replace >= 1.21.8 ? import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemFrame.class)
public class ItemFrameMixin {
    @WrapMethod(method = "addAdditionalSaveData")
    private void saving(
            CompoundTag value, //#replace >= 1.21.8 ? ValueOutput value,
            Operation<Void> original
    ) {
        try {
            IGNYSettings.ITEM_FRAME_SAVE_NBT.set(true);
            original.call(value);
        } finally {
            IGNYSettings.ITEM_FRAME_SAVE_NBT.set(false);
        }
    }
}
