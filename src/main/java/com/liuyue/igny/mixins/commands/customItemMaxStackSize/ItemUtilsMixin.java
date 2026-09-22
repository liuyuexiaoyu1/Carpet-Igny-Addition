package com.liuyue.igny.mixins.commands.customItemMaxStackSize;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.manager.CustomItemMaxStackSizeDataManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;
//#if >= 26.1
/*$$import java.util.stream.Stream;
import java.util.List;$$*/
//#endif

@Mixin(ItemUtils.class)
public class ItemUtilsMixin {
    //#if >= 26.1
    /*$$@SuppressWarnings("unchecked")
    @WrapOperation(method = "onContainerDestroyed", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"))
    private static <T> void forEach(Stream<?> instance, Consumer<? super T> consumer, Operation<Void> original, @Local(argsOnly = true) ItemEntity itemEntity)$$*/
    //#else
    @WrapOperation(method = "onContainerDestroyed", at = @At(value = "INVOKE", target = "Ljava/lang/Iterable;forEach(Ljava/util/function/Consumer;)V"))
    private static <T> void forEach(Iterable<?> instance, Consumer<? super T> consumer, Operation<Void> original, @Local(argsOnly = true) ItemEntity itemEntity)
    //#endif
    {
        int customMax = CustomItemMaxStackSizeDataManager.INSTANCE.getCustomStackSize(itemEntity.getItem());
        if (IGNYSettings.itemStackCountChanged.get() && customMax != -1) {
            //?>= 26.1 ? List<?> contents = instance.toList();
            for (int i = 0; i < itemEntity.getItem().getCount(); i++) {
                original.call(instance, consumer); //#replace >= 26.1 ? contents.forEach((Consumer) consumer);
            }
            return;
        }
        original.call(instance, consumer);
    }
}
