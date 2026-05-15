package com.liuyue.igny.mixins.rule.linkableEnderChest;

import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.helper.inventory.LinkedContainer;
import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.utils.interfaces.linkableEnderChest.LinkedEnderChest;
import net.minecraft.core.BlockPos;
//#if MC >= 12005
import net.minecraft.core.component.DataComponents;
//#else
//$$ import net.minecraft.nbt.Tag;
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
//#if MC >= 12109
//$$ import net.minecraft.world.entity.ContainerUser;
//#endif

@Mixin(EnderChestBlockEntity.class)
public class EnderChestBlockEntityMixin extends BlockEntity implements Container, LinkedEnderChest {
    public EnderChestBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public Container carpet_Igny_Addition$getContainer() {
        EnderChestBlockEntity self = (EnderChestBlockEntity)(Object)this;
        //#if MC >= 12005
        Component component = self.components().get(DataComponents.CUSTOM_NAME);
        //#else
        //$$ Component component = Component.Serializer.fromJson(self.saveWithFullMetadata().getString("CustomName"));
        //#endif
        if (component != null) {
            return LinkedContainerManager.get(
                    component.getString()
            );
        }

        return null;
    }

    @Override
    public boolean carpet_Igny_Addition$isLinked() {
        if (!IGNYSettings.linkableEnderChest) return false;
        EnderChestBlockEntity self = (EnderChestBlockEntity)(Object)this;
        //#if MC >= 12005
        return self.components().has(DataComponents.CUSTOM_NAME);
        //#else
        //$$ return self.saveWithFullMetadata().contains("CustomName", Tag.TAG_STRING);
        //#endif
    }

    @SuppressWarnings("all")
    @Inject(method = "startOpen", at = @At("HEAD"), cancellable = true)
    //#if MC >= 12109
    //$$ private void forceStartOpen(ContainerUser player, CallbackInfo ci)
    //#else
    private void forceStartOpen(Player player, CallbackInfo ci)
    //#endif
    {
        //#if MC >= 12109
        //$$ if (!(player instanceof Player)) return;
        //#endif
        //#if MC >= 12005
        if (IGNYSettings.linkableEnderChest && this.components().has(DataComponents.CUSTOM_NAME))
        //#else
        //$$ if (IGNYSettings.linkableEnderChest && this.saveWithFullMetadata().contains("CustomName", Tag.TAG_STRING))
        //#endif
        {
            if (!this.remove && !((Player) player).isSpectator()) {
                this.level.blockEvent(this.worldPosition, Blocks.ENDER_CHEST, 1, 1);
                this.level.playSound(null, worldPosition, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
            }
            ci.cancel();
        }
    }

    @SuppressWarnings("all")
    @Inject(method = "stopOpen", at = @At("HEAD"), cancellable = true)
    //#if MC >= 12109
    //$$ private void forceStopOpen(ContainerUser player, CallbackInfo ci)
    //#else
    private void forceStopOpen(Player player, CallbackInfo ci)
    //#endif
    {
        //#if MC >= 12109
        //$$ if (!(player instanceof Player)) return;
        //#endif
        //#if MC >= 12005
        if (IGNYSettings.linkableEnderChest && this.components().has(DataComponents.CUSTOM_NAME))
        //#else
        //$$ if (IGNYSettings.linkableEnderChest && this.saveWithFullMetadata().contains("CustomName", Tag.TAG_STRING))
        //#endif
        {
            if (!this.remove && !((Player) player).isSpectator()) {
                this.level.blockEvent(this.worldPosition, Blocks.ENDER_CHEST, 1, 0);
                this.level.playSound(null, worldPosition, SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
            }
            ci.cancel();
        }
    }

    @SuppressWarnings("all")
    @Unique
    private Container igny$getVirtualContainer() {
        //#if MC >= 12005
        if (IGNYSettings.linkableEnderChest && this.components().has(DataComponents.CUSTOM_NAME))
        //#else
        //$$ if (IGNYSettings.linkableEnderChest && this.saveWithFullMetadata().contains("CustomName", Tag.TAG_STRING))
        //#endif
        {
            //#if MC >= 12005
            String name = this.components().get(DataComponents.CUSTOM_NAME).getString();
            //#else
            //$$ String name = Component.Serializer.fromJson(this.saveWithFullMetadata().getString("CustomName")).getString();
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
        //#if MC >= 12005
        if (IGNYSettings.linkableEnderChest && this.components().has(DataComponents.CUSTOM_NAME))
        //#else
        //$$ if (IGNYSettings.linkableEnderChest && this.saveWithFullMetadata().contains("CustomName", Tag.TAG_STRING))
        //#endif
        {
            return Container.stillValidBlockEntity(this, player);
        }
        return true;
    }

    @Override
    public void clearContent() {}

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level != null && !level.isClientSide()) {
            //#if MC >= 12005
            Component customName = this.components().get(DataComponents.CUSTOM_NAME);
            //#else
            //$$ Component customName = Component.Serializer.fromJson(this.saveWithFullMetadata().getString("CustomName"));
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
            //#if MC >= 12005
            Component customName = this.components().get(DataComponents.CUSTOM_NAME);
            //#else
            //$$ Component customName = Component.Serializer.fromJson(this.saveWithFullMetadata().getString("CustomName"));
            //#endif
            if (customName != null && !LinkedContainerManager.get(customName.getString()).isActiveChest((EnderChestBlockEntity) (Object) this)) {
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
        //#if MC >= 12005
        Component customName = this.components().get(DataComponents.CUSTOM_NAME);
        //#else
        //$$ Component customName = Component.Serializer.fromJson(this.saveWithFullMetadata().getString("CustomName"));
        //#endif
        if (customName != null) {
            LinkedContainer linked = LinkedContainerManager.get(customName.getString());
            linked.removeActiveChest((EnderChestBlockEntity)(Object)this);
        }
    }
}
