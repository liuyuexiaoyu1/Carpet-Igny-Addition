package com.liuyue.igny.mixins.rule.survivalFlyNoClip.compat.tis;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "carpettisaddition.helpers.carpet.tweaks.rule.creativeNoClip.CreativeNoClipHelper")
@Restriction(require = @Condition("carpet-tis-addition"))
public interface CreativeNoClipHelperInvoker {
    @Invoker("isNoClipPlayer")
    static boolean igny$isNoClipPlayer(Entity entity) {
        return false;
    }
}
