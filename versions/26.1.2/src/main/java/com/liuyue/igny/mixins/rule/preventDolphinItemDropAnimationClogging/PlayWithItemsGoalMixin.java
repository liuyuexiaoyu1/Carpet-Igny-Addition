package com.liuyue.igny.mixins.rule.preventDolphinItemDropAnimationClogging;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.EnumSet;

@Mixin(Dolphin.PlayWithItemsGoal.class) //#replace >= 26.3 ? @Mixin(Dolphin.MoveToItemGoal.class)
public class PlayWithItemsGoalMixin {
    @WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/dolphin/Dolphin$PlayWithItemsGoal;setFlags(Ljava/util/EnumSet;)V")) //#replace >= 26.3 ? @WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/dolphin/Dolphin$MoveToItemGoal;setFlags(Ljava/util/EnumSet;)V"))
    private static boolean onInit(Dolphin.PlayWithItemsGoal instance, EnumSet<?> enumSet) //#replace >= 26.3 ? private static boolean onInit(Dolphin.MoveToItemGoal instance, EnumSet<?> enumSet)
    {
        return !IGNYSettings.PREVENT_DOLPHIN_ITEM_DROP_ANIMATION_CLOGGING.value();
    }
}
