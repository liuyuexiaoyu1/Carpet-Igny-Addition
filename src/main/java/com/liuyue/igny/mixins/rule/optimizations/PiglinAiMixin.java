package com.liuyue.igny.mixins.rule.optimizations;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.accessors.optimizations.IEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
    @Inject(method = "wantsToPickup",at=@At(value = "HEAD"),cancellable = true)
    private static void wantsToPickup(Piglin piglin, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir){
        ResourceLocation resourceLocation = EntityType.getKey(piglin.getType());
        String entityTypeName = resourceLocation.toString();
        if (IGNYSettings.CRAMMING_ENTITIES.contains(entityTypeName)) {
            if (itemStack.getItem() == Items.GOLD_INGOT) {
                return;
            }
            if (((IEntity) piglin).carpet_Igny_Addition$getCrammingCount() > IGNYSettings.optimizedEntityLimit) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "isPlayerHoldingLovedItem",at=@At(value = "HEAD"),cancellable = true)
    private static void isPlayerHoldingLovedItem(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir){
        if (((IEntity) livingEntity).carpet_Igny_Addition$getCrammingCount() > IGNYSettings.optimizedEntityLimit) {
            cir.setReturnValue(false);
        }
    }
}
