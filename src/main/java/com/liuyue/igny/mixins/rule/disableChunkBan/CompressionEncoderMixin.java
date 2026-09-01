package com.liuyue.igny.mixins.rule.disableChunkBan;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.network.CompressionEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CompressionEncoder.class)
public class CompressionEncoderMixin {
    @ModifyConstant(method = "encode*", constant = @Constant(intValue = 8388608), require = 0)
    private int encode(int original) {
        return IGNYSettings.DISABLE_CHUNK_BAN.value() ? Integer.MAX_VALUE : original;
    }
}
