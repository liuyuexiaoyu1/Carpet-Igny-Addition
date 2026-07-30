package com.liuyue.igny.mixins.rule.airCompost;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ComposterBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {
    @WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;containsKey(Ljava/lang/Object;)Z"))
    private boolean containsKey(Object2FloatMap<?> instance, Object o, Operation<Boolean> original) {
        if (IGNYSettings.AIR_COMPOST.value() && o instanceof Item item) {
            if (item.equals(Items.AIR)) {
                return true;
            }
        }
        return original.call(instance, o);
    }

    @WrapWithCondition(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"))
    private boolean useItemOn(ItemStack instance, int i, LivingEntity livingEntity) {
        if (IGNYSettings.AIR_COMPOST.value()) {
            return !instance.is(Items.AIR);
        }
        return true;
    }
}
