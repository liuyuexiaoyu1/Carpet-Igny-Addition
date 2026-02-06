package com.liuyue.igny.mixins.rule.dispenserTrade;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.RuleUtils;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.*;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {
    @Inject(method = "dispenseFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/DispenserBlockEntity;getRandomSlot(Lnet/minecraft/util/RandomSource;)I"), cancellable = true)
    private void dispenseFrom(ServerLevel serverLevel,
                              //#if MC > 12002
                              BlockState blockState,
                              //#endif
                              BlockPos blockPos, CallbackInfo ci, @Local DispenserBlockEntity blockEntity) {
        if (!IGNYSettings.dispenserTrade) return;
        Component component = blockEntity.getName();
        String name = component.getString();
        int tradeIndex;
        try {
            tradeIndex = Integer.parseInt(name.trim()) - 1;
            if (tradeIndex < 0) return;
        } catch (NumberFormatException e) {
            return;
        }
        //#if MC <= 12002
        //$$ BlockState blockState = serverLevel.getBlockState(blockPos);
        //#endif
        Direction facing = blockState.getValue(DispenserBlock.FACING);
        List<Villager> villagers = serverLevel.getEntities(EntityType.VILLAGER, new AABB(blockPos.relative(facing)), e -> true);
        if (!villagers.isEmpty()) {
            Villager villager = villagers.getFirst();
            MerchantOffers offers = villager.getOffers();
            if (tradeIndex >= offers.size()) return;
            MerchantOffer offer = offers.get(tradeIndex);
            if (offer == null || offer.isOutOfStock()) {
                if (!IGNYSettings.dispenserTradeFailDisperseItem){
                    ci.cancel();
                    serverLevel.levelEvent(1001, blockPos, facing.get3DDataValue());
                    //#if MC > 12004
                    villager.makeSound(SoundEvents.VILLAGER_NO);
                    //#else
                    //$$ villager.playSound(SoundEvents.VILLAGER_NO);
                    //#endif
                    return;
                }
                return;
            }
            ItemStack costA = offer.getCostA();
            ItemStack costB = offer.getCostB();
            if (!hasAndConsumeItems(blockEntity, costA, costB)) {
                if (!IGNYSettings.dispenserTradeFailDisperseItem){
                    ci.cancel();
                    serverLevel.levelEvent(1001, blockPos, facing.get3DDataValue());
                    //#if MC > 12004
                    villager.makeSound(SoundEvents.VILLAGER_NO);
                    //#else
                    //$$ villager.playSound(SoundEvents.VILLAGER_NO);
                    //#endif
                    return;
                }
                return;
            }
            villager.notifyTrade(offer);
            ItemStack result = offer.getResult().copy();
            Position position = DispenserBlock.getDispensePosition(
                    //#if MC > 12001
                    new BlockSource(serverLevel, blockPos, blockState, blockEntity)
                    //#else
                    //$$ new BlockSourceImpl(serverLevel, blockPos)
                    //#endif
            );
            double spawnX = position.x();
            double spawnY = position.y();
            double spawnZ = position.z();
            if (facing.getAxis() == Direction.Axis.Y) {
                spawnY -= 0.125;
            } else {
                spawnY -= 0.15625;
            }
            ItemEntity itemEntity = new ItemEntity(serverLevel, spawnX, spawnY, spawnZ, result);
            itemEntity.setPickUpDelay(20);
            RandomSource random = serverLevel.getRandom();
            double pow = random.nextDouble() * 0.1 + 0.2;
            itemEntity.setDeltaMovement(
                    random.triangle(facing.getStepX() * pow, 0.0172275 * 6),
                    random.triangle(0.2, 0.0172275 * 6),
                    random.triangle(facing.getStepZ() * pow, 0.0172275 * 6)
            );
            serverLevel.addFreshEntity(itemEntity);
            serverLevel.levelEvent(1000, blockPos, facing.get3DDataValue());
            serverLevel.broadcastEntityEvent(villager, (byte)14);
            //#if MC > 12004
            villager.makeSound(SoundEvents.VILLAGER_YES);
            //#else
            //$$ villager.playSound(SoundEvents.VILLAGER_YES);
            //#endif
            ci.cancel();
        }
    }

    @Unique
    private static boolean hasAndConsumeItems(DispenserBlockEntity dispenser, ItemStack costA, ItemStack costB) {
        if (!hasStack(dispenser, costA)) return false;
        if (!costB.isEmpty() && !hasStack(dispenser, costB)) return false;
        if (Boolean.TRUE.equals(RuleUtils.getCarpetRulesValue("carpet-tis-addition", "dispenserNoItemCost"))) return true;
        consumeItem(dispenser, costA.getItem(), costA.getCount());
        consumeItem(dispenser, costB.getItem(), costB.getCount());
        return true;
    }

    @Unique
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean hasStack(DispenserBlockEntity dispenser, ItemStack target) {
        int countNeeded = target.getCount();
        int found = 0;
        for (int i = 0; i < dispenser.getContainerSize(); i++) {
            ItemStack invStack = dispenser.getItem(i);
            if (ItemStack.isSameItemSameComponents(invStack, target)) {
                found += invStack.getCount();
                if (found >= countNeeded) return true;
            }
        }
        return false;
    }

    @Unique
    private static void consumeItem(DispenserBlockEntity dispenser, net.minecraft.world.item.Item item, int count) {
        int remaining = count;
        for (int i = 0; i < dispenser.getContainerSize() && remaining > 0; i++) {
            ItemStack stack = dispenser.getItem(i);
            if (!stack.isEmpty() && stack.is(item)) {
                int toRemove = Math.min(stack.getCount(), remaining);
                stack.shrink(toRemove);
                remaining -= toRemove;
                if (stack.isEmpty()) {
                    dispenser.setItem(i, ItemStack.EMPTY);
                }
            }
        }
    }
}