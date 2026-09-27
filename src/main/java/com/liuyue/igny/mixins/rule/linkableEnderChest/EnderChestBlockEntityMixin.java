package com.liuyue.igny.mixins.rule.linkableEnderChest;

import com.liuyue.igny.helper.inventory.LinkedContainer;
import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.LinkedEnderChest;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.ViewingChest;
import net.minecraft.core.BlockPos;
//#if >= 1.20.5
import net.minecraft.core.component.DataComponents;
//#else
/*$$import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;$$*/
//#endif
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?>= 1.21.9 ? import net.minecraft.world.entity.ContainerUser;

@Mixin(EnderChestBlockEntity.class)
public class EnderChestBlockEntityMixin extends BlockEntity implements Container, LinkedEnderChest {
    public EnderChestBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public Container igny$getContainer() {
        if (!LinkedContainerManager.isRuleFully()) return null;
        EnderChestBlockEntity self = (EnderChestBlockEntity)(Object) this;
        //#if >= 1.20.5
        Component component = self.components().get(DataComponents.CUSTOM_NAME);
        //#else
        /*$$CompoundTag tag = self.saveWithFullMetadata();
        Component component = tag.contains("CustomName", Tag.TAG_STRING)
        ? Component.Serializer.fromJson(tag.getString("CustomName")) : null;$$*/
        //#endif
        if (component != null) {
            return LinkedContainerManager.get(
                    component.getString()
            );
        }

        return null;
    }

    @Override
    public boolean igny$isLinked() {
        if (!LinkedContainerManager.isRuleEnabled()) return false;
        EnderChestBlockEntity self = (EnderChestBlockEntity)(Object)this;
        return self.components().has(DataComponents.CUSTOM_NAME); //#replace < 1.20.5 ? return self.saveWithFullMetadata().contains("CustomName", Tag.TAG_STRING);
    }

    @SuppressWarnings("all")
    @Inject(method = "startOpen", at = @At("HEAD"), cancellable = true)
    private void forceStartOpen(Player player, CallbackInfo ci) //#replace >= 1.21.9 ? private void forceStartOpen(ContainerUser player, CallbackInfo ci)
    {
        //?>= 1.21.9 ? if (!(player instanceof Player)) return;
        if (player == null) return;
        if (LinkedContainerManager.isRuleEnabled() && this.components().has(DataComponents.CUSTOM_NAME)) //#replace < 1.20.5 ? if (LinkedContainerManager.isRuleEnabled() && this.saveWithFullMetadata().contains("CustomName", Tag.TAG_STRING))
        {
            Container opened = this.igny$getVirtualContainer();

            if (opened instanceof LinkedContainer linked) {
                if (linked.beginOpen((Player) player) && !this.remove && !((Player) player).isSpectator()) {
                    this.igny$broadcastLinkedOpen(true, !(player instanceof ViewingChest viewing) || viewing.igny$getContextChest() != null);
                }
            }
            ci.cancel();
        }
    }

    @SuppressWarnings("all")
    @Inject(method = "stopOpen", at = @At("HEAD"), cancellable = true)
    private void forceStopOpen(Player player, CallbackInfo ci) //#replace >= 1.21.9 ? private void forceStopOpen(ContainerUser player, CallbackInfo ci)
    {
        //?>= 1.21.9 ? if (!(player instanceof Player)) return;
        if (player == null) return;
        if (LinkedContainerManager.isRuleEnabled() && this.components().has(DataComponents.CUSTOM_NAME)) //#replace < 1.20.5 ? if (LinkedContainerManager.isRuleEnabled() && this.saveWithFullMetadata().contains("CustomName", Tag.TAG_STRING))
        {
            Container opened = this.igny$getVirtualContainer();

            if (opened instanceof LinkedContainer linked && linked.endOpen((Player) player) && !this.remove && !((Player) player).isSpectator()) {
                this.igny$broadcastLinkedOpen(false, true);
            }
            ci.cancel();
        }
    }

    @SuppressWarnings("all")
    @Unique
    private Container igny$getVirtualContainer() {
        if (LinkedContainerManager.isRuleFully() && this.components().has(DataComponents.CUSTOM_NAME)) //#replace < 1.20.5 ? if (LinkedContainerManager.isRuleFully() && this.saveWithFullMetadata().contains("CustomName", Tag.TAG_STRING))
        {
            //#if >= 1.20.5
            String name = this.components().get(DataComponents.CUSTOM_NAME).getString();
            //#else
            /*$$CompoundTag tag = this.saveWithFullMetadata();
            String name = tag.contains("CustomName", Tag.TAG_STRING)
            ? Component.Serializer.fromJson(tag.getString("CustomName")).getString() : null;$$*/
            //#endif
            return LinkedContainerManager.get(name);
        }
        return null;
    }

    @Override
    public int getContainerSize() {
        Container inv = igny$getVirtualContainer();
        return inv != null ? inv.getContainerSize() : 0;
    }

    @Override
    public boolean isEmpty() {
        Container inv = igny$getVirtualContainer();
        return inv == null || inv.isEmpty();
    }

    @SuppressWarnings("all")
    @Override
    public ItemStack getItem(int slot) {
        Container inv = igny$getVirtualContainer();
        return inv != null ? inv.getItem(slot) : ItemStack.EMPTY;
    }

    @SuppressWarnings("all")
    @Override
    public ItemStack removeItem(int slot, int amount) {
        Container inv = igny$getVirtualContainer();
        if (inv == null) return ItemStack.EMPTY;
        ItemStack result = inv.removeItem(slot, amount);
        if (!result.isEmpty()) inv.setChanged();
        return result;
    }

    @SuppressWarnings("all")
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        Container inv = igny$getVirtualContainer();
        return inv != null ? inv.removeItemNoUpdate(slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        Container inv = igny$getVirtualContainer();
        if (inv != null) {
            inv.setItem(slot, stack);
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (LinkedContainerManager.isRuleEnabled() && this.components().has(DataComponents.CUSTOM_NAME)) //#replace < 1.20.5 ? if (LinkedContainerManager.isRuleEnabled() && this.saveWithFullMetadata().contains("CustomName", Tag.TAG_STRING))
        {
            return Container.stillValidBlockEntity(this, player);
        }
        return true;
    }

    @Unique
    private void igny$broadcastLinkedOpen(boolean opening, boolean allChests) {
        if (this.level == null) {
            return;
        }

        this.level.blockEvent(this.worldPosition, Blocks.ENDER_CHEST, 1, opening ? 1 : 0);
        this.level.playSound(null, this.worldPosition, opening ? SoundEvents.ENDER_CHEST_OPEN : SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, this.level.getRandom().nextFloat() * 0.1F + 0.9F);

        if (!allChests) {
            return;
        }

        Container container = this.igny$getVirtualContainer();

        if (!(container instanceof LinkedContainer linked)) {
            return;
        }

        for (EnderChestBlockEntity chest : linked.getActiveChests()) {
            if (chest != (Object) this && !chest.isRemoved()) {
                Level level = chest.getLevel();
                if (level != null) {
                    level.blockEvent(chest.getBlockPos(), Blocks.ENDER_CHEST, 1, opening ? 1 : 0);
                }
            }
        }
    }

    @Override
    public void clearContent() {}

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level != null && !level.isClientSide()) {
            //#if >= 1.20.5
            Component customName = this.components().get(DataComponents.CUSTOM_NAME);
            //#else
            /*$$CompoundTag tag = this.saveWithFullMetadata();
            Component customName = tag.contains("CustomName", Tag.TAG_STRING) ? Component.Serializer.fromJson(tag.getString("CustomName")) : null;$$*/
            //#endif
            if (customName != null) {
                this.registerToLinkedContainer(customName.getString());
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide()) {
            //#if >= 1.20.5
            Component customName = this.components().get(DataComponents.CUSTOM_NAME);
            //#else
            /*$$CompoundTag tag = this.saveWithFullMetadata();
            Component customName = tag.contains("CustomName", Tag.TAG_STRING)
            ? Component.Serializer.fromJson(tag.getString("CustomName")) : null;$$*/
            //#endif
            if (customName != null) {
                this.registerToLinkedContainer(customName.getString());
            }
            this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level != null && !this.level.isClientSide()) {
            unregisterFromLinkedContainer();
        }
    }

    @Unique
    private void registerToLinkedContainer(String name) {
        LinkedContainer linked = LinkedContainerManager.get(name);
        linked.setActiveChest((EnderChestBlockEntity)(Object)this);
    }

    @Unique
    private void unregisterFromLinkedContainer() {
        //#if >= 1.20.5
        Component customName = this.components().get(DataComponents.CUSTOM_NAME);
        //#else
        /*$$CompoundTag tag = this.saveWithFullMetadata();
        Component customName = tag.contains("CustomName", Tag.TAG_STRING) ? Component.Serializer.fromJson(tag.getString("CustomName")) : null;$$*/
        //#endif
        if (customName != null) {
            LinkedContainer linked = LinkedContainerManager.get(customName.getString());
            linked.removeActiveChest((EnderChestBlockEntity)(Object)this);
        }
    }

    @Inject(method = "lidAnimateTick", at = @At(value = "HEAD"), cancellable = true)
    private static void lidAnimateTick(Level level, BlockPos pos, BlockState state, EnderChestBlockEntity blockEntity, CallbackInfo ci) {
        if (!level.isClientSide()) {
            //#if >= 1.20.5
            Component customName = blockEntity.components().get(DataComponents.CUSTOM_NAME);
            //#else
            /*$$CompoundTag tag = blockEntity.saveWithFullMetadata();
            Component customName = tag.contains("CustomName", Tag.TAG_STRING) ? Component.Serializer.fromJson(tag.getString("CustomName")) : null;$$*/
            //#endif
            if (customName != null) {
                LinkedContainer linked = LinkedContainerManager.get(customName.getString());
                linked.setActiveChest(blockEntity);
            }
            ci.cancel();
        }
    }
    
    //#if < 1.20.5
    /*$$@Unique private Component customName;

     @Override
     public void load(CompoundTag tag) {
         super.load(tag);
         if (tag.contains("CustomName", Tag.TAG_STRING)) {
             this.customName = Component.Serializer.fromJson(tag.getString("CustomName"));
         }
     }
     @Override
     public void saveAdditional(CompoundTag tag) {
         super.saveAdditional(tag);
         if (this.customName != null) {
             tag.putString("CustomName", Component.Serializer.toJson(this.customName));
         }
    }$$*/
    //#endif
}
