package com.liuyue.igny.mixins.features.commands.customItemMaxStackSize;

import carpet.CarpetSettings;
import carpet.helpers.HopperCounter;
import carpet.utils.WoolTool;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.IGNYServerMod;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.RuleUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import com.liuyue.igny.data.CustomItemMaxStackSizeDataManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.liuyue.igny.utils.InventoryUtils;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

/**
 * 以下代码来自<a href="https://github.com/fcsailboat/Carpet-Org-Addition">{@code Carpet-Org-Addition}</a>模组，
 * 其引用了来自<a href="https://github.com/TISUnion/Carpet-TIS-Addition">{@code Carpet TIS Addition}</a>模组的代码：
 * <ul>
 * <li>漏斗计数器无限速度实现：{@link HopperBlockEntityMixin#hopperCountersUnlimitedSpeed(Level, BlockPos, HopperBlockEntity, BooleanSupplier)}</li>
 * <li>漏斗不消耗物品实现：{@link HopperBlockEntityMixin#hopperNoItemCost(Level, BlockPos, Container, int, ItemStack, int)}</li>
 * </ul>
 * <br>
 * <p>
 *     该部分代码遵循LGPL-3.0协议，许可证全文如下：
 * </p>
 * <p>
 *                    GNU LESSER GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 * <p>
 *  Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 * <p>
 * <p>
 *   This version of the GNU Lesser General Public License incorporates
 * the terms and conditions of version 3 of the GNU General Public
 * License, supplemented by the additional permissions listed below.
 * <p>
 *   0. Additional Definitions.
 * <p>
 *   As used herein, "this License" refers to version 3 of the GNU Lesser
 * General Public License, and the "GNU GPL" refers to version 3 of the GNU
 * General Public License.
 * <p>
 *   "The Library" refers to a covered work governed by this License,
 * other than an Application or a Combined Work as defined below.
 * <p>
 *   An "Application" is any work that makes use of an interface provided
 * by the Library, but which is not otherwise based on the Library.
 * Defining a subclass of a class defined by the Library is deemed a mode
 * of using an interface provided by the Library.
 * <p>
 *   A "Combined Work" is a work produced by combining or linking an
 * Application with the Library.  The particular version of the Library
 * with which the Combined Work was made is also called the "Linked
 * Version".
 * <p>
 *   The "Minimal Corresponding Source" for a Combined Work means the
 * Corresponding Source for the Combined Work, excluding any source code
 * for portions of the Combined Work that, considered in isolation, are
 * based on the Application, and not on the Linked Version.
 * <p>
 *   The "Corresponding Application Code" for a Combined Work means the
 * object code and/or source code for the Application, including any data
 * and utility programs needed for reproducing the Combined Work from the
 * Application, but excluding the System Libraries of the Combined Work.
 * <p>
 *   1. Exception to Section 3 of the GNU GPL.
 * <p>
 *   You may convey a covered work under sections 3 and 4 of this License
 * without being bound by section 3 of the GNU GPL.
 * <p>
 *   2. Conveying Modified Versions.
 * <p>
 *   If you modify a copy of the Library, and, in your modifications, a
 * facility refers to a function or data to be supplied by an Application
 * that uses the facility (other than as an argument passed when the
 * facility is invoked), then you may convey a copy of the modified
 * version:
 * <p>
 *    a) under this License, provided that you make a good faith effort to
 *    ensure that, in the event an Application does not supply the
 *    function or data, the facility still operates, and performs
 *    whatever part of its purpose remains meaningful, or
 * <p>
 *    b) under the GNU GPL, with none of the additional permissions of
 *    this License applicable to that copy.
 * <p>
 *   3. Object Code Incorporating Material from Library Header Files.
 * <p>
 *   The object code form of an Application may incorporate material from
 * a header file that is part of the Library.  You may convey such object
 * code under terms of your choice, provided that, if the incorporated
 * material is not limited to numerical parameters, data structure
 * layouts and accessors, or small macros, inline functions and templates
 * (ten or fewer lines in length), you do both of the following:
 * <p>
 *    a) Give prominent notice with each copy of the object code that the
 *    Library is used in it and that the Library and its use are
 *    covered by this License.
 * <p>
 *    b) Accompany the object code with a copy of the GNU GPL and this license
 *    document.
 * <p>
 *   4. Combined Works.
 * <p>
 *   You may convey a Combined Work under terms of your choice that,
 * taken together, effectively do not restrict modification of the
 * portions of the Library contained in the Combined Work and reverse
 * engineering for debugging such modifications, if you also do each of
 * the following:
 * <p>
 *    a) Give prominent notice with each copy of the Combined Work that
 *    the Library is used in it and that the Library and its use are
 *    covered by this License.
 * <p>
 *    b) Accompany the Combined Work with a copy of the GNU GPL and this license
 *    document.
 * <p>
 *    c) For a Combined Work that displays copyright notices during
 *    execution, include the copyright notice for the Library among
 *    these notices, as well as a reference directing the user to the
 *    copies of the GNU GPL and this license document.
 * <p>
 *    d) Do one of the following:
 * <p>
 *        0) Convey the Minimal Corresponding Source under the terms of this
 *        License, and the Corresponding Application Code in a form
 *        suitable for, and under terms that permit, the user to
 *        recombine or relink the Application with a modified version of
 *        the Linked Version to produce a modified Combined Work, in the
 *        manner specified by section 6 of the GNU GPL for conveying
 *        Corresponding Source.
 * <p>
 *        1) Use a suitable shared library mechanism for linking with the
 *        Library.  A suitable mechanism is one that (a) uses at run time
 *        a copy of the Library already present on the user's computer
 *        system, and (b) will operate properly with a modified version
 *        of the Library that is interface-compatible with the Linked
 *        Version.
 * <p>
 *    e) Provide Installation Information, but only if you would otherwise
 *    be required to provide such information under section 6 of the
 *    GNU GPL, and only to the extent that such information is
 *    necessary to install and execute a modified version of the
 *    Combined Work produced by recombining or relinking the
 *    Application with a modified version of the Linked Version. (If
 *    you use option 4d0, the Installation Information must accompany
 *    the Minimal Corresponding Source and Corresponding Application
 *    Code. If you use option 4d1, you must provide the Installation
 *    Information in the manner specified by section 6 of the GNU GPL
 *    for conveying Corresponding Source.)
 * <p>
 *   5. Combined Libraries.
 * <p>
 *   You may place library facilities that are a work based on the
 * Library side by side in a single library together with other library
 * facilities that are not Applications and are not covered by this
 * License, and convey such a combined library under terms of your
 * choice, if you do both of the following:
 * <p>
 *    a) Accompany the combined library with a copy of the same work based
 *    on the Library, uncombined with any other library facilities,
 *    conveyed under the terms of this License.
 * <p>
 *    b) Give prominent notice with the combined library that part of it
 *    is a work based on the Library, and explaining where to find the
 *    accompanying uncombined form of the same work.
 * <p>
 *   6. Revised Versions of the GNU Lesser General Public License.
 * <p>
 *   The Free Software Foundation may publish revised and/or new versions
 * of the GNU Lesser General Public License from time to time. Such new
 * versions will be similar in spirit to the present version, but may
 * differ in detail to address new problems or concerns.
 * <p>
 *   Each version is given a distinguishing version number. If the
 * Library as you received it specifies that a certain numbered version
 * of the GNU Lesser General Public License "or any later version"
 * applies to it, you have the option of following the terms and
 * conditions either of that published version or of any later version
 * published by the Free Software Foundation. If the Library as you
 * received it does not specify a version number of the GNU Lesser
 * General Public License, you may choose any version of the GNU Lesser
 * General Public License ever published by the Free Software Foundation.
 * <p>
 *   If the Library as you received it specifies that a proxy can decide
 * whether future versions of the GNU Lesser General Public License shall
 * apply, that proxy's public statement of acceptance of any version is
 * permanent authorization for you to choose that version for the
 * Library.
 */

@SuppressWarnings("JavadocLinkAsPlainText")
@Mixin(value = HopperBlockEntity.class, priority = 900)
public abstract class HopperBlockEntityMixin extends BlockEntity {

    public HopperBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    private static boolean isFullContainer(Container inventory, Direction direction) {
        return false;
    }

    @Shadow
    @Nullable
    private static Container getAttachedContainer(Level world, BlockPos pos, BlockState blockState) {
        return null;
    }

    @Shadow
    public static ItemStack addItem(@Nullable Container from, Container to, ItemStack stack, @Nullable Direction side) {
        throw new AssertionError();
    }

    @Shadow
    @Nullable
    private static Container getSourceContainer(Level world, Hopper hopper) {
        return null;
    }

    @Shadow
    private static IntStream getSlots(Container inventory, Direction side){
        return IntStream.empty();
    }

    @Shadow
    private static boolean tryTakeInItemFromSlot(Hopper hopper, Container inventory, int slot, Direction side) {
        return false;
    }

    @Shadow
    public static List<ItemEntity> getItemsAtAndAbove(Level world, Hopper hopper) {
        return List.of();
    }

    @Shadow
    public static boolean addItem(Container inventory, ItemEntity itemEntity) {
        return false;
    }

    @Shadow
    private static boolean ejectItems(Level world, BlockPos pos, BlockState blockState, Container container) {
        return false;
    }

    @Shadow
    protected abstract boolean isOnCooldown();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Shadow
    protected abstract boolean inventoryFull();

    @Shadow
    protected abstract void setCooldown(int transferCooldown);

    @Shadow
    private static boolean isEmptyContainer(Container par1, Direction par2) {
        return false;
    }

    @WrapOperation(method = "pushItemsTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;tryMoveItems(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/HopperBlockEntity;Ljava/util/function/BooleanSupplier;)Z"))
    private static boolean pushItemsTick(Level world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier, Operation<Boolean> original) {
        if (IGNYServerMod.LITHIUM) {
            return original.call(world, pos, state, blockEntity, booleanSupplier);
        }
        return RuleUtils.itemStackableWrap(() -> original.call(world, pos, state, blockEntity, booleanSupplier));
    }

    @WrapOperation(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;tryMoveItems(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/HopperBlockEntity;Ljava/util/function/BooleanSupplier;)Z"))
    private static boolean entityInside(Level world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier, Operation<Boolean> original) {
        if (IGNYServerMod.LITHIUM) {
            return original.call(world, pos, state, blockEntity, booleanSupplier);
        }
        return RuleUtils.itemStackableWrap(() -> original.call(world, pos, state, blockEntity, booleanSupplier));
    }

    @WrapOperation(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack extract(Container from, Container to, ItemStack stack, Direction side, Operation<ItemStack> original, @Local LocalBooleanRef bl) {
        if (IGNYSettings.itemStackCountChanged.get()) {
            return original.call(from, to, stack, side);
        }
        if (CustomItemMaxStackSizeDataManager.hasCustomStack(stack.getItem()) || (Boolean.TRUE.equals(RuleUtils.getCarpetRulesValue("carpet-org-addition", "shulkerBoxStackable")) && InventoryUtils.isShulkerBoxItem(stack))) {
            ItemStack split = stack.split(stack.getMaxStackSize());
            int count = split.getCount();
            ItemStack result = original.call(from, to, split.copy(), side);
            stack.grow(result.getCount());
            if (count != result.getCount()) {
                bl.set(true);
            }
            return stack;
        }
        return original.call(from, to, stack, side);
    }

    /**
     * 保持与锂的兼容
     */
    @Unique
    private static void compatible(Runnable runnable) {
        if (IGNYServerMod.LITHIUM) {
            boolean changed = IGNYSettings.itemStackCountChanged.get();
            try {
                IGNYSettings.itemStackCountChanged.set(false);
                runnable.run();
            } finally {
                IGNYSettings.itemStackCountChanged.set(changed);
            }
        }
    }

    @Inject(method = "tryMoveItems", at = @At("HEAD"), cancellable = true)
    private static void insertAndExtract(Level world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier, CallbackInfoReturnable<Boolean> cir) {
        compatible(() -> cir.setReturnValue(tryInsertAndExtract(world, pos, state, blockEntity, booleanSupplier)));
    }

    @Inject(method = "ejectItems", at = @At("HEAD"), cancellable = true)
    private static void insert(Level level, BlockPos blockPos, BlockState blockState, Container container, CallbackInfoReturnable<Boolean> cir) {
        compatible(() -> cir.setReturnValue(tryInsert(level, blockPos, blockState, container)));
    }

    @Inject(method = "suckInItems(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/Hopper;)Z", at = @At("HEAD"), cancellable = true)
    private static void extract(Level world, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
        compatible(() -> cir.setReturnValue(tryExtract(world, hopper)));
    }

    @Unique
    private static boolean tryInsertAndExtract(Level world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier) {
        if (world.isClientSide()) {
            return false;
        }
        HopperBlockEntityMixin self = (HopperBlockEntityMixin) (Object) blockEntity;
        if (self != null && !self.isOnCooldown() && state.getValue(HopperBlock.ENABLED)) {
            boolean bl = false;
            if (!blockEntity.isEmpty()) {
                bl = ejectItems(world, pos, state, getAttachedContainer(world,pos, state));
            }
            if (!self.inventoryFull()) {
                bl |= booleanSupplier.getAsBoolean();
            }
            if (bl) {
                self.setCooldown(8);
                hopperCountersUnlimitedSpeed(world, pos, blockEntity, booleanSupplier);
                setChanged(world, pos, state);
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean tryInsert(Level world, BlockPos pos, BlockState blockState, Container container) {
        if (hopperCounters(world, pos)) return true;
        Container inventory = getAttachedContainer(world, pos, blockState);
        if (inventory == null) {
            return false;
        }
        Direction direction = blockState.getValue(HopperBlock.FACING).getOpposite();
        if (isFullContainer(inventory, direction)) {
            return false;
        }
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack itemStack = inventory.getItem(i);
            if (!itemStack.isEmpty()) {
                int prevCount = itemStack.getCount();
                ItemStack itemStack2 = addItem(container, inventory, container.removeItem(i, 1), direction);
                if (itemStack2.isEmpty()) {
                    inventory.setChanged();
                    hopperNoItemCost(world, pos, container, i, itemStack, prevCount);
                    return true;
                }
                itemStack.setCount(prevCount);
                if (prevCount == 1) {
                    container.setItem(i, itemStack);
                }
            }
        }
        return false;
    }

    /**
     * 漏斗计数器相关逻辑
     *
     * @see <a href="https://github.com/gnembon/fabric-carpet/blob/master/src/main/java/carpet/mixins/HopperBlockEntity_counterMixin.java">漏斗计数器</a>
     */
    @Unique
    private static boolean hopperCounters(Level world, BlockPos blockPos) {
        if (CarpetSettings.hopperCounters) {
            Direction hopperFacing = world.getBlockState(blockPos).getValue(HopperBlock.FACING);
            DyeColor woolColor = WoolTool.getWoolColorAtPosition(
                    world,
                    blockPos.relative(hopperFacing));
            if (woolColor != null)
            {
                Container inventory = HopperBlockEntity.getContainerAt(world, blockPos);
                if (inventory == null) return false;
                for (int i = 0; i < inventory.getContainerSize(); ++i)
                {
                    if (!inventory.getItem(i).isEmpty())
                    {
                        ItemStack itemstack = inventory.getItem(i);//.copy();
                        HopperCounter.getCounter(woolColor).add(world.getServer(), itemstack);
                        inventory.setItem(i, ItemStack.EMPTY);
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 漏斗计数器无限速度相关逻辑
     *
     * @see <a href="https://github.com/TISUnion/Carpet-TIS-Addition/tree/master/src/main/java/carpettisaddition/mixins/rule/hopperCountersUnlimitedSpeed">漏斗计数器无限速度</a>
     */
    @Unique
    private static void hopperCountersUnlimitedSpeed(Level world, BlockPos blockPos, HopperBlockEntity blockEntity, BooleanSupplier supplier) {
        if (CarpetSettings.hopperCounters && Boolean.TRUE.equals(RuleUtils.getCarpetRulesValue("carpet-tis-addition", "hopperCountersUnlimitedSpeed"))) {
            Direction direction = blockEntity.getBlockState().getValue(HopperBlock.FACING);
            DyeColor color = WoolTool.getWoolColorAtPosition(world, blockPos.relative(direction));
            if (color == null) {
                return;
            }
            HopperBlockEntityMixin mixin = (HopperBlockEntityMixin) (Object) blockEntity;
            for (int i = Short.MAX_VALUE - 1; i >= 0; i--) {
                boolean flag = false;
                if (!blockEntity.isEmpty()) {
                    flag = ejectItems(world, blockPos, world.getBlockState(blockPos), blockEntity);
                }
                if (!mixin.inventoryFull()) {
                    flag |= supplier.getAsBoolean();
                }
                if (!flag) {
                    break;
                }
                if (i == 0) {
                    IGNYServer.LOGGER.warn("Hopper at {} exceeded hopperCountersUnlimitedSpeed operation limit {}", blockEntity, Short.MAX_VALUE);
                }
            }
            mixin.setCooldown(0);
        }
    }

    /**
     * 漏斗不消耗物品相关逻辑
     *
     * @see <a href="https://github.com/TISUnion/Carpet-TIS-Addition/tree/master/src/main/java/carpettisaddition/mixins/rule/hopperNoItemCost">漏斗不消耗物品</a>
     */
    @Unique
    private static void hopperNoItemCost(Level world, BlockPos blockPos, Container container, int index, ItemStack itemStack, int prevCount) {
        if (Boolean.TRUE.equals(RuleUtils.getCarpetRulesValue("carpet-tis-addition", "hopperNoItemCost"))) {
            DyeColor color = WoolTool.getWoolColorAtPosition(world, blockPos.relative(Direction.UP));
            if (color == null) {
                return;
            }
            int currentCount = itemStack.getCount();
            itemStack.setCount(prevCount);
            ItemStack prevStack = itemStack.copy();
            itemStack.setCount(currentCount);
            container.setItem(index, prevStack);
        }
    }

    @Unique
    private static boolean tryExtract(Level world, Hopper hopper) {
        Container inventory = getSourceContainer(world, hopper);
        if (inventory != null) {
            Direction direction = Direction.DOWN;
            return !isEmptyContainer(inventory, direction) && getSlots(inventory, direction).anyMatch(i -> tryTakeInItemFromSlot(hopper, inventory, i, direction));
        } else {
            for (ItemEntity itemEntity : getItemsAtAndAbove(world, hopper)) {
                if (addItem(hopper, itemEntity)) {
                    return true;
                }
            }
            return false;
        }
    }
}