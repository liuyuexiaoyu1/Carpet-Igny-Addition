package com.liuyue.igny.mixins.features.rule.trialSpawnerLoot;

import com.liuyue.igny.IGNYSettings;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrialSpawner.class)
public class TrialSpawnerMixin {

    @Shadow
    private boolean isOminous;

    // 修改 loot 列表：应用倍率 + 钥匙逻辑
    @Inject(
            method = "ejectReward",
            at = @At(
                    value = "INVOKE",
                    target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;isEmpty()Z"
            )
    )
    private void modifyLootList(ServerLevel serverLevel, BlockPos blockPos, ResourceKey<LootTable> resourceKey, CallbackInfo ci,@Local ObjectArrayList<ItemStack> objectArrayList) {

        if (objectArrayList.isEmpty()) return;

        RandomSource random = serverLevel.getRandom();

        if (IGNYSettings.trialSpawnerLootMultiplier != 1) {
            ObjectArrayList<ItemStack> newList = new ObjectArrayList<>();
            for (ItemStack stack : objectArrayList) {
                int count = (int) (double) (stack.getCount() * IGNYSettings.trialSpawnerLootMultiplier);
                if (count <= 0) continue;
                ItemStack newStack = stack.copy();
                newStack.setCount(Math.min(count, newStack.getMaxStackSize()));
                newList.add(newStack);

                int remaining = count - newStack.getMaxStackSize();
                while (remaining > 0) {
                    ItemStack extra = stack.copy();
                    extra.setCount(Math.min(remaining, extra.getMaxStackSize()));
                    newList.add(extra);
                    remaining -= extra.getMaxStackSize();
                }
            }
            objectArrayList.clear();
            objectArrayList.addAll(newList);
        }

        if (IGNYSettings.trialSpawnerDropKeyProbability != -1) {
            boolean shouldHaveKey = random.nextDouble() < IGNYSettings.trialSpawnerDropKeyProbability;
            if (shouldHaveKey) {
                boolean hasKey = false;
                for (ItemStack stack : objectArrayList) {
                    if (stack.is(Items.TRIAL_KEY) || stack.is(Items.OMINOUS_TRIAL_KEY)) {
                        hasKey = true;
                        break;
                    }
                }
                if (!hasKey) {
                    if (this.isOminous) {
                        objectArrayList.add(new ItemStack(Items.OMINOUS_TRIAL_KEY, IGNYSettings.trialSpawnerLootMultiplier));
                    }else {
                        objectArrayList.add(new ItemStack(Items.TRIAL_KEY, IGNYSettings.trialSpawnerLootMultiplier));
                    }
                }
            }
        }
    }
}