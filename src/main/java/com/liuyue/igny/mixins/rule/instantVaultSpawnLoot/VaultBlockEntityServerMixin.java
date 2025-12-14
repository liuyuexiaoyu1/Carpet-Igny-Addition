package com.liuyue.igny.mixins.rule.instantVaultSpawnLoot;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.vault.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VaultBlockEntity.Server.class)
public class VaultBlockEntityServerMixin {
    @Inject(
            method = "unlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void injectUnlock(
            ServerLevel serverLevel,
            BlockState blockState,
            BlockPos blockPos,
            VaultConfig vaultConfig,
            VaultServerData vaultServerData,
            VaultSharedData vaultSharedData,
            List<ItemStack> list,
            CallbackInfo ci
    ) {
        if (IGNYSettings.instantVaultSpawnLoot) {
            ci.cancel();
            RandomSource random = serverLevel.getRandom();
            Vec3 dropPos = Vec3.atCenterOf(blockPos).add(0.0, 0.5, 0.0);

            for (ItemStack stack : list) {
                if (!stack.isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(
                            serverLevel,
                            dropPos.x(), dropPos.y(), dropPos.z(),
                            stack.copy()
                    );
                    itemEntity.setDeltaMovement(
                            random.nextGaussian() * 0.05,
                            random.nextGaussian() * 0.05 + 0.2,
                            random.nextGaussian() * 0.05
                    );
                    serverLevel.addFreshEntity(itemEntity);
                }
            }
            BlockState newState = blockState.setValue(
                    VaultBlock.STATE,
                    VaultState.ACTIVE
            );
            VaultBlockEntityServerInvoker.invokeSetVaultState(
                    serverLevel, blockPos, blockState, newState,
                    vaultConfig, vaultSharedData
            );
        }
    }
}