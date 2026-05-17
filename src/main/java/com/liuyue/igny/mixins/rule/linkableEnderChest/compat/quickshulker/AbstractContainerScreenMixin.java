package com.liuyue.igny.mixins.rule.linkableEnderChest.compat.quickshulker;

import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.network.packet.config.SyncLinkedEnderChestPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if MC >= 12109
//$$ import net.minecraft.client.input.MouseButtonEvent;
//$$ import net.minecraft.client.input.KeyEvent;
//#endif

//#if MC >= 12005
import net.minecraft.core.component.DataComponents;
//#else
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
//$$ import com.liuyue.igny.IGNYServer;
//#endif

@Mixin(value = AbstractContainerScreen.class, priority = 999)
public class AbstractContainerScreenMixin {
    @Shadow @Nullable protected Slot hoveredSlot;

    @Inject(method = "keyPressed", at = @At(value = "HEAD"))
    //#if MC >= 12109
    //$$ private void keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir)
    //#else
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir)
    //#endif
    {
        if (!LinkedContainerManager.isRuleEnabled()) return;
        //#if MC < 12005
        //$$ if (!ClientPlayNetworking.canSend(IGNYServer.SYNC_LINKED_ENDER_CHEST_PACKET_ID)) return;
        //#else
        if (!ClientPlayNetworking.canSend(SyncLinkedEnderChestPayload.TYPE)) return;
        //#endif
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            ItemStack stack = this.hoveredSlot.getItem();
            //#if MC >= 12005
            if (stack.is(Items.ENDER_CHEST) && stack.has(DataComponents.CUSTOM_NAME))
            //#else
            //$$ if (stack.is(Items.ENDER_CHEST) && stack.hasCustomHoverName())
            //#endif
            {
                String customName = stack.getHoverName().getString();
                //#if MC < 12005
                //$$ FriendlyByteBuf buf = PacketByteBufs.create();
                //$$ buf.writeUtf(customName);
                //$$ ClientPlayNetworking.send(IGNYServer.SYNC_LINKED_ENDER_CHEST_PACKET_ID, buf);
                //#else
                ClientPlayNetworking.send(new SyncLinkedEnderChestPayload(customName));
                //#endif
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At(value = "HEAD"))
    //#if MC >= 12109
    //$$ private void onBeforeMouseClicked(MouseButtonEvent event, boolean isDoubleClick, CallbackInfoReturnable<Boolean> cir)
    //#else
    private void onBeforeMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir)
    //#endif
    {
        if (!LinkedContainerManager.isRuleEnabled()) return;
        //#if MC < 12005
        //$$ if (!ClientPlayNetworking.canSend(IGNYServer.SYNC_LINKED_ENDER_CHEST_PACKET_ID)) return;
        //#else
        if (!ClientPlayNetworking.canSend(SyncLinkedEnderChestPayload.TYPE)) return;
        //#endif
        //#if MC >= 12109
        //$$ if (event.button() == 1 && this.hoveredSlot != null && this.hoveredSlot.hasItem())
        //#else
        if (button == 1 && this.hoveredSlot != null && this.hoveredSlot.hasItem())
        //#endif
        {
            ItemStack stack = this.hoveredSlot.getItem();
            //#if MC >= 12005
            if (stack.is(Items.ENDER_CHEST) && stack.has(DataComponents.CUSTOM_NAME))
            //#else
            //$$ if (stack.is(Items.ENDER_CHEST) && stack.hasCustomHoverName())
            //#endif
            {
                String customName = stack.getHoverName().getString();
                //#if MC < 12005
                //$$ FriendlyByteBuf buf = PacketByteBufs.create();
                //$$ buf.writeUtf(customName);
                //$$ ClientPlayNetworking.send(IGNYServer.SYNC_LINKED_ENDER_CHEST_PACKET_ID, buf);
                //#else
                ClientPlayNetworking.send(new SyncLinkedEnderChestPayload(customName));
                //#endif
            }
        }
    }
}