package com.liuyue.igny.mixins.rule.disableChunkBan;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.network.PacketEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PacketEncoder.class)
public class PacketEncoderMixin {
    @ModifyConstant(method = "encode*", constant = @Constant(intValue = 8388608), require = 0)
    private int encode(int original) {
        if (IGNYSettings.DISABLE_CHUNK_BAN.value()) {
            return Integer.MAX_VALUE;
        }
        return original;
    }
}
