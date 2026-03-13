package com.liuyue.igny.mixins.rule.noCreativeDestroyAttachmentDrops;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow public abstract boolean isCreative();

    @WrapMethod(method = "destroyBlock")
    private boolean destroyBlock(BlockPos pos, Operation<Boolean> original) {
        try {
            if (IGNYSettings.noCreativeDestroyAttachmentDrops && isCreative()) {
                IGNYSettings.CREATIVE_BREAKING.set(true);
            }
            return original.call(pos);
        } finally {
            IGNYSettings.CREATIVE_BREAKING.set(false);
        }
    }
}
