package com.liuyue.igny.mixins.rule.invisibleItemFrames;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.interfaces.invisibleItemFrames.ItemFrameRefreshable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 12106
//$$ import net.minecraft.world.level.storage.ValueInput;
//$$ import net.minecraft.world.level.storage.ValueOutput;
//#endif

@Mixin(ItemFrame.class)
public class ItemFrameMixin implements ItemFrameRefreshable {
    @Unique
    private boolean igny$modManagedInvisible = false;

    @Inject(method = "setItem(Lnet/minecraft/world/item/ItemStack;Z)V", at = @At(value = "RETURN"))
    private void onSetItem(ItemStack stack, boolean updateNeighbours, CallbackInfo ci) {
        igny$refreshInvisible(IGNYSettings.invisibleItemFrames);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    //#if MC >= 12106
    //$$ private void onReadNbt(ValueInput tag, CallbackInfo ci)
    //#else
    private void onReadNbt(CompoundTag tag, CallbackInfo ci)
    //#endif
    {
        if (tag.contains("IgnyInvisibleItemFrame")) {
            //#if MC >= 12105
            //$$ igny$modManagedInvisible = tag.getBooleanOr("IgnyInvisibleItemFrame", false);
            //#else
            igny$modManagedInvisible = tag.getBoolean("IgnyInvisibleItemFrame");
            //#endif
        }
        igny$refreshInvisible(IGNYSettings.invisibleItemFrames);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    //#if MC >= 12106
    //$$ private void onWriteNbt(ValueOutput tag, CallbackInfo ci)
    //#else
    private void onWriteNbt(CompoundTag tag, CallbackInfo ci)
    //#endif
    {
        if (igny$modManagedInvisible) {
            tag.putBoolean("IgnyInvisibleItemFrame", true);
        }
    }

    @Override
    public void igny$refreshInvisible(String ruleValue) {
        ItemFrame self = (ItemFrame) (Object) this;
        if (!self.hasCustomName()) return;

        boolean shouldHide;
        if ("false".equalsIgnoreCase(ruleValue)) {
            shouldHide = false;
        } else {
            String frameName = self.getName().getString();
            boolean nameMatches = "true".equalsIgnoreCase(ruleValue)
                    ? "invisible".equalsIgnoreCase(frameName)
                    : frameName.equalsIgnoreCase(ruleValue);
            shouldHide = nameMatches && !self.getItem().isEmpty();
        }

        if (shouldHide) {
            self.setInvisible(true);
            igny$modManagedInvisible = true;
        } else if (igny$modManagedInvisible) {
            self.setInvisible(false);
            igny$modManagedInvisible = false;
        }
    }
}
