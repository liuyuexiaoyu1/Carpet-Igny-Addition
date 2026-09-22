package com.liuyue.igny.mixins.rule.linkableEnderChest.compat.quickshulker;

import com.liuyue.igny.manager.LinkedContainerManager;
import com.liuyue.igny.network.packet.config.SyncLinkedEnderChestPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//?>= 1.21.9 ? import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?<= 1.19.4 ? import com.mojang.blaze3d.vertex.PoseStack;

//#if >= 1.20.5
import net.minecraft.core.component.DataComponents;
//#else
/*$$import net.minecraft.network.FriendlyByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import com.liuyue.igny.IGNYServer;$$*/
//#endif

@Mixin(value = AbstractContainerScreen.class, priority = 999)
public abstract class AbstractContainerScreenMixin {
    @Shadow @Nullable protected Slot hoveredSlot;

    @Shadow
    @Final
    protected AbstractContainerMenu menu;

    @Inject(method = "mouseClicked", at = @At(value = "HEAD"))
    private void onClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) //#replace >= 1.21.9 ? private void onClick(MouseButtonEvent event, boolean isDoubleClick, CallbackInfoReturnable<Boolean> cir)
    {
        if (!LinkedContainerManager.isRuleEnabled()) return;

        if (this.menu.getCarried().isEmpty() && button == 1 && this.hoveredSlot != null) //#replace >= 1.21.9 ? if (this.menu.getCarried().isEmpty() && event.button() == 1 && this.hoveredSlot != null)
        {
            if (!ClientPlayNetworking.canSend(SyncLinkedEnderChestPayload.TYPE)) return; //#replace < 1.20.5 ? if (!ClientPlayNetworking.canSend(IGNYServer.SYNC_LINKED_ENDER_CHEST_PACKET_ID)) return;

            ItemStack stack = this.hoveredSlot.getItem();
            if (stack.is(Items.ENDER_CHEST)) {
                String customName = stack.has(DataComponents.CUSTOM_NAME) ? stack.getHoverName().getString() : ""; //#replace < 1.20.5 ? String customName = stack.hasCustomHoverName() ? stack.getHoverName().getString() : "";
                syncEnderChest(customName);
            }
        }
    }

    @Unique
    private void syncEnderChest(String customName) {
        //#if < 1.20.5
        /*$$FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(customName);
        ClientPlayNetworking.send(IGNYServer.SYNC_LINKED_ENDER_CHEST_PACKET_ID, buf);$$*/
        //#else
        ClientPlayNetworking.send(new SyncLinkedEnderChestPayload(customName));
        //#endif
    }
}