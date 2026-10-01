package com.liuyue.igny.mixins.rule.linkableEnderChest.compat.malilib;

import com.liuyue.igny.client.LinkedChestPreviewCache;
import fi.dy.masa.malilib.util.InventoryUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if >= 1.21.11
/*$$import fi.dy.masa.malilib.util.data.tag.CompoundData;
import net.minecraft.core.RegistryAccess;$$*/
//#else
//#if >= 1.21.8
/*$$import net.minecraft.core.RegistryAccess;$$*/
//#endif
//#endif

@Restriction(require = @Condition("malilib"))
@Mixin(InventoryUtils.class)
public class InventoryUtilsMixin {

    //#if >= 1.21.11
    /*$$@Inject(method = "getPlayerEnderItemsFromData", at = @At("HEAD"), cancellable = true)
    private static void igny$linkedEnderItems(CompoundData data, RegistryAccess registryAccess, CallbackInfoReturnable<PlayerEnderChestContainer> cir) {
        PlayerEnderChestContainer linked = LinkedChestPreviewCache.previewContainer();
        if (linked != null) {
            cir.setReturnValue(linked);
        }
    }$$*/
    //#else
    //#if >= 1.21.8
    /*$$@Inject(method = "getPlayerEnderItemsFromNbt", at = @At("HEAD"), cancellable = true)
    private static void igny$linkedEnderItems(CompoundTag tag, RegistryAccess registryAccess, CallbackInfoReturnable<PlayerEnderChestContainer> cir) {
        PlayerEnderChestContainer linked = LinkedChestPreviewCache.previewContainer();
        if (linked != null) {
            cir.setReturnValue(linked);
        }
    }$$*/
    //#else
    @Inject(method = "getPlayerEnderItemsFromNbt", at = @At("HEAD"), cancellable = true)
    private static void igny$linkedEnderItems(CompoundTag tag, HolderLookup.Provider provider, CallbackInfoReturnable<PlayerEnderChestContainer> cir) {
        PlayerEnderChestContainer linked = LinkedChestPreviewCache.previewContainer();
        if (linked != null) {
            cir.setReturnValue(linked);
        }
    }
    //#endif
    //#endif
}
