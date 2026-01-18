package com.liuyue.igny.mixins.rule.skeletonsPickupSwordsReintroduced;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {

    @Inject(method = "compareWeapons", at = @At("HEAD"), cancellable = true)
    private void pickupSwords(ItemStack itemStack, ItemStack itemStack2, EquipmentSlot equipmentSlot, CallbackInfoReturnable<Boolean> cir) {
        if (IGNYSettings.skeletonsPickupSwordsReintroduced) {
            Mob self = (Mob) (Object) this;
            if (self instanceof AbstractSkeleton) {
                TagKey<Item> tagKey = ItemTags.SKELETON_PREFERRED_WEAPONS;
                TagKey<Item> tagKey2 = ItemTags.SWORDS;
                if (tagKey != null && tagKey2 != null) {
                    if (itemStack2.is(tagKey) && !itemStack.is(tagKey)) {
                        if (itemStack2.is(tagKey2) && !itemStack.is(tagKey2)) {
                            cir.setReturnValue(false);
                        }
                    }

                    if (!itemStack2.is(tagKey) && itemStack.is(tagKey)) {
                        if (!itemStack2.is(tagKey2) && itemStack.is(tagKey2)) {
                            cir.setReturnValue(true);
                        }
                    }
                }

            }
            double d = ((MobInvoker) self).invokergetApproximateAttributeWith(itemStack, Attributes.ATTACK_DAMAGE, equipmentSlot);
            double e = ((MobInvoker) self).invokergetApproximateAttributeWith(itemStack2, Attributes.ATTACK_DAMAGE, equipmentSlot);
            cir.setReturnValue(d != e ? d > e : self.canReplaceEqualItem(itemStack, itemStack2));
        }
    }
}
