package com.liuyue.igny.mixins.rule.survivalFlyNoClip.compat.tis;

import com.liuyue.igny.helper.NoClipHelper;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "carpettisaddition.helpers.carpet.tweaks.rule.creativeNoClip.CreativeNoClipHelper")
@Restriction(require = @Condition("carpet-tis-addition"))
public class CreativeNoClipHelperMixin {
    @WrapMethod(method = "isNoClipPlayer")
    private static boolean igny$modifyNoClipCheck(Entity entity, Operation<Boolean> original) {
        return original.call(entity) || NoClipHelper.isActiveFlyingPlayer(entity);
    }
}
