// MIT License
//
// Copyright (c) 2024 fcsailboat
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.liuyue.igny.mixins.commands.customItemMaxStackSize;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.data.CustomItemMaxStackSizeDataManager;
import com.liuyue.igny.utils.InventoryUtils;
import com.liuyue.igny.utils.RuleUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 900)
public class ItemStackMixin {
    @Unique
    private final ItemStack thisStack = (ItemStack) (Object) this;

    //#if MC >= 12005
    @Inject(method = "limitSize", at = @At("HEAD"), cancellable = true)
    private void limitSize(int maxCount, CallbackInfo ci) {
        if ((CustomItemMaxStackSizeDataManager.hasCustomStack(thisStack.getItem()) || ShulkerBoxStackableRuleEnabled()) && !IGNYSettings.itemStackCountChanged.get()) {
            ci.cancel();
        }
    }
    //#endif

    //#if MC < 26.1
    @Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
    private void getMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        Item item = thisStack.getItem();
        if (IGNYSettings.itemStackCountChanged.get()) {
            if (CustomItemMaxStackSizeDataManager.hasCustomStack(item)) {
                cir.setReturnValue(CustomItemMaxStackSizeDataManager.getCustomStackSize(item));
            }
        }else if (ShulkerBoxStackableRuleEnabled()){
            cir.setReturnValue(item.getDefaultMaxStackSize());
        }
    }
    //#endif

    @Unique
    private boolean ShulkerBoxStackableRuleEnabled() {
        return Boolean.TRUE.equals(RuleUtils.getCarpetRulesValue("carpet-org-addition", "shulkerBoxStackable"))
                && InventoryUtils.isShulkerBoxItem(thisStack)
                && InventoryUtils.isEmptyShulkerBox(thisStack);
    }
}
