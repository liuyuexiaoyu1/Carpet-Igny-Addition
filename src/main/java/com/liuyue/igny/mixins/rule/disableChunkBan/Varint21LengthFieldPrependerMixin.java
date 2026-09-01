package com.liuyue.igny.mixins.rule.disableChunkBan;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.network.Varint21LengthFieldPrepender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Varint21LengthFieldPrepender.class)
public class Varint21LengthFieldPrependerMixin {
    @ModifyConstant(method = "encode*", constant = @Constant(intValue = 3), require = 0)
    private int encode(int constant) {
        return IGNYSettings.DISABLE_CHUNK_BAN.value() ? Integer.MAX_VALUE : constant;
    }
}
