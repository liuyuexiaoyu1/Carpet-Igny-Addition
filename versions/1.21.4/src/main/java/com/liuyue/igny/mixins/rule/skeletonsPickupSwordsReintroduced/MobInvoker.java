package com.liuyue.igny.mixins.rule.skeletonsPickupSwordsReintroduced;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mob.class)
public interface MobInvoker {
    @Invoker("getApproximateAttributeWith")
     double invokergetApproximateAttributeWith(ItemStack itemStack, Holder<Attribute> holder, EquipmentSlot equipmentSlot);
}
