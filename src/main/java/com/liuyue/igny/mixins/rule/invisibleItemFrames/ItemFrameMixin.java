package com.liuyue.igny.mixins.rule.invisibleItemFrames;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//#if MC < 12108
import net.minecraft.nbt.CompoundTag;
//#else
//$$ import net.minecraft.world.level.storage.ValueOutput;
//#endif
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemFrame.class)
public class ItemFrameMixin {
    @WrapMethod(method = "addAdditionalSaveData")
    private void saving(
            //#if MC < 12108
            CompoundTag value,
            //#else
            //$$ ValueOutput value,
            //#endif
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
