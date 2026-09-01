package com.liuyue.igny.mixins.rule.disableChunkBan;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.network.CompressionDecoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CompressionDecoder.class)
public class CompressionDecoderMixin {
    @ModifyConstant(method = "decode", constant = @Constant(intValue = 8388608), require = 0)
    private int decode(int original) {
        return IGNYSettings.DISABLE_CHUNK_BAN.value() ? Integer.MAX_VALUE : original;
    }
}
