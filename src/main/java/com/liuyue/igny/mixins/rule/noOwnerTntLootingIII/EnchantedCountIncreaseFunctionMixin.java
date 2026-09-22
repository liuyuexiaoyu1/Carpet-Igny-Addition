package com.liuyue.igny.mixins.rule.noOwnerTntLootingIII;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if >= 26.3
/*$$import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;$$*/
//#else
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
//#endif

@Mixin(EnchantedCountIncreaseFunction.class)
public abstract class EnchantedCountIncreaseFunctionMixin {
    @Shadow protected abstract boolean hasLimit();

    //#if >= 26.3
    //$$ @Shadow @Final private Holder<ContextFloatProvider> count;
    //#elseif >= 26.1
    //$$ @Shadow @Final private NumberProvider count;
    //#else
    @Shadow @Final private NumberProvider value;
    //#endif

    @Shadow @Final private int limit;

    @Inject(method = "run", at = @At(value = "HEAD"), cancellable = true)
    private void run(ItemStack itemStack, LootContext lootContext, CallbackInfoReturnable<ItemStack> cir) {
        //#if >= 26.3
        //$$ DamageSource damageSource = lootContext.getOptional(LootContextParams.DAMAGE_SOURCE);
        //#elseif >= 1.21.2
        //$$ DamageSource damageSource = lootContext.getOptionalParameter(LootContextParams.DAMAGE_SOURCE);
        //#else
        DamageSource damageSource = lootContext.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        //#endif
        if (IGNYSettings.NO_OWNER_TNT_LOOTING_III.value() &&
                damageSource != null &&
                damageSource.getDirectEntity() instanceof PrimedTnt &&
                ((PrimedTnt) damageSource.getDirectEntity()).getOwner() == null) {
            //#if >= 26.3
            //$$ float f = 3 * this.count.value().getFloat(lootContext);
            //#elseif >= 26.1
            //$$ float f = 3 * this.count.getFloat(lootContext);
            //#else
            float f = 3 * this.value.getFloat(lootContext);
            //#endif
            itemStack.grow(Math.round(f));
            if (this.hasLimit()) { //#replace <= 1.20.4 ? if (this.hasLimit() && itemStack.getCount() > this.limit) {
                itemStack.limitSize(this.limit); //#replace <= 1.20.4 ? itemStack.setCount(this.limit);
            }
            cir.setReturnValue(itemStack);
        }
    }
}
