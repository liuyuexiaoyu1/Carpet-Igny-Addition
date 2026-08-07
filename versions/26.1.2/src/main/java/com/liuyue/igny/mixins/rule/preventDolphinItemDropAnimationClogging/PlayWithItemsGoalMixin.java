package com.liuyue.igny.mixins.rule.preventDolphinItemDropAnimationClogging;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.EnumSet;

//#if MC >= 26.3
//$$ @Mixin(Dolphin.MoveToItemGoal.class)
//#else
@Mixin(Dolphin.PlayWithItemsGoal.class)
//#endif
public class PlayWithItemsGoalMixin {
    //#if MC >= 26.3
    //$$ @WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/dolphin/Dolphin$MoveToItemGoal;setFlags(Ljava/util/EnumSet;)V"))
    //#else
    @WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/dolphin/Dolphin$PlayWithItemsGoal;setFlags(Ljava/util/EnumSet;)V"))
    //#endif
    //#if MC >= 26.3
    //$$ private static boolean onInit(Dolphin.MoveToItemGoal instance, EnumSet<?> enumSet)
    //#else
    private static boolean onInit(Dolphin.PlayWithItemsGoal instance, EnumSet<?> enumSet)
    //#endif
    {
        return !IGNYSettings.PREVENT_DOLPHIN_ITEM_DROP_ANIMATION_CLOGGING.value();
    }
}
