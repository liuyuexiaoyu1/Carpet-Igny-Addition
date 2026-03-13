package com.liuyue.igny.mixins.rule.noOwnerTntLootingIII;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantedCountIncreaseFunction.class)
public abstract class EnchantedCountIncreaseFunctionMixin {
    @Shadow protected abstract boolean hasLimit();

    //#if MC >= 26.1
    //$$ @Shadow @Final private NumberProvider count;
    //#else
    @Shadow @Final private NumberProvider value;
    //#endif

    @Shadow @Final private int limit;

    @Inject(method = "run", at = @At(value = "HEAD"), cancellable = true)
    private void run(ItemStack itemStack, LootContext lootContext, CallbackInfoReturnable<ItemStack> cir) {
        //#if MC >= 12102
        //$$ DamageSource damageSource = lootContext.getOptionalParameter(LootContextParams.DAMAGE_SOURCE);
        //#else
        DamageSource damageSource = lootContext.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        //#endif
        if (IGNYSettings.noOwnerTntLootingIII &&
                damageSource != null &&
                damageSource.getDirectEntity() instanceof PrimedTnt &&
                ((PrimedTnt) damageSource.getDirectEntity()).getOwner() == null) {
            //#if MC >= 26.1
            //$$ float f = 3 * this.count.getFloat(lootContext);
            //#else
            float f = 3 * this.value.getFloat(lootContext);
            //#endif
            itemStack.grow(Math.round(f));
            //#if MC <= 12004
            //$$ if (this.hasLimit() && itemStack.getCount() > this.limit) {
            //#else
            if (this.hasLimit()) {
                //#endif
                //#if MC <= 12004
                //$$ itemStack.setCount(this.limit);
                //#else
                itemStack.limitSize(this.limit);
                //#endif
            }
            cir.setReturnValue(itemStack);
        }
    }
}
