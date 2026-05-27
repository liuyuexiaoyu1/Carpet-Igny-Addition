package com.liuyue.igny.mixins.rule.linkableEnderChest.compat.quickshulker;

import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.network.packet.config.SyncLinkedEnderChestPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//#if MC >= 26.1
//$$ import net.minecraft.client.gui.GuiGraphicsExtractor;
//#else
import net.minecraft.client.gui.GuiGraphics;
//#endif
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12005
import net.minecraft.core.component.DataComponents;
//#else
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
//$$ import com.liuyue.igny.IGNYServer;
//#endif

@Mixin(value = AbstractContainerScreen.class, priority = 999)
public abstract class AbstractContainerScreenMixin {
    @Shadow @Nullable protected Slot hoveredSlot;

    @Unique
    @Nullable
    private Slot igny$lastHoveredSlot = null;

    //#if MC >= 26.1
    //$$ @Inject(method = "extractRenderState", at = @At(value = "RETURN"))
    //$$ private void onRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci)
    //#else
    @Inject(method = "render", at = @At(value = "RETURN"))
    private void onRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci)
    //#endif
    {
        if (!LinkedContainerManager.isRuleEnabled()) return;

        if (this.hoveredSlot == this.igny$lastHoveredSlot) {
            return;
        }

        this.igny$lastHoveredSlot = this.hoveredSlot;

        if (this.hoveredSlot == null || !this.hoveredSlot.hasItem()) {
            return;
        }

        //#if MC < 12005
        //$$ if (!ClientPlayNetworking.canSend(IGNYServer.SYNC_LINKED_ENDER_CHEST_PACKET_ID)) return;
        //#else
        if (!ClientPlayNetworking.canSend(SyncLinkedEnderChestPayload.TYPE)) return;
        //#endif

        ItemStack stack = this.hoveredSlot.getItem();
        if (stack.is(Items.ENDER_CHEST)) {
            //#if MC >= 12005
            String customName = stack.has(DataComponents.CUSTOM_NAME) ? stack.getHoverName().getString() : null;
            //#else
            //$$ String customName = stack.hasCustomHoverName() ? stack.getHoverName().getString() : null;
            //#endif
            syncEnderChest(customName);
        }
    }

    @Unique
    private void syncEnderChest(String customName) {
        //#if MC < 12005
        //$$ FriendlyByteBuf buf = PacketByteBufs.create();
        //$$ buf.writeUtf(customName);
        //$$ ClientPlayNetworking.send(IGNYServer.SYNC_LINKED_ENDER_CHEST_PACKET_ID, buf);
        //#else
        ClientPlayNetworking.send(new SyncLinkedEnderChestPayload(customName));
        //#endif
    }
}