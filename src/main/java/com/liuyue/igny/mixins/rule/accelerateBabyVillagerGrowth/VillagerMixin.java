package com.liuyue.igny.mixins.rule.accelerateBabyVillagerGrowth;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

//#if MC >= 12102
//$$ import net.minecraft.tags.ItemTags;
//#endif

@Mixin(Villager.class)
public abstract class VillagerMixin {
    //#if MC <= 12101
    @Shadow
    @Final
    private static Set<Item> WANTED_ITEMS;
    //#endif

    @Inject(
            method = "mobInteract",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Villager self = (Villager) (Object) this;
        if (IGNYSettings.ACCELERATE_BABY_VILLAGER_GROWTH.value() && self.getAge() < -200) {
            Level level = self.level();
            ItemStack stack = player.getItemInHand(hand);
            if (level.isClientSide() || !self.isAlive() || self.isTrading() || self.isSleeping() || !self.isBaby()) return;
            //#if MC >= 12102
            //$$ if (!stack.is(ItemTags.VILLAGER_PICKS_UP)) return;
            //#else
            if (!WANTED_ITEMS.contains(stack.getItem())) return;
            //#endif
            int age = self.getAge();
            if (age >= 0) return;
            int remainingTicks = -age;
            int reduction = Math.max(20 * 5, (int) (remainingTicks * 0.1));
            int newRemaining = Math.max(20 * 10, remainingTicks - reduction);
            self.setAge(-newRemaining);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            ((ServerLevel) level).sendParticles(ParticleTypes.HAPPY_VILLAGER, self.getX(), self.getY() + 0.5, self.getZ(), 8, 0.3, 0.3, 0.3, 0.01);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}