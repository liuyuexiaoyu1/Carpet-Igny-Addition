package com.liuyue.igny.helper.linkableEnderChest;

import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.minihud.renderer.InventoryOverlayHandler;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
//#if >= 1.21.11
/*$$import fi.dy.masa.malilib.render.InventoryOverlayContext;
import fi.dy.masa.malilib.util.data.tag.CompoundData;$$*/
//#else
import net.minecraft.nbt.CompoundTag;
//#endif

@SuppressWarnings("deprecation")
public final class MiniHudPreview {

    //#if >= 1.21.11
    /*$$public static InventoryOverlayContext build(InventoryOverlayHandler handler, Container container, BlockEntity blockEntity, CompoundData data) {
        return new InventoryOverlayContext(InventoryOverlay.getBestInventoryType(container, data), container, blockEntity, null, data, handler.getRefreshHandler());
    }$$*/
    //#else
    public static InventoryOverlay.Context build(InventoryOverlayHandler handler, Container container, BlockEntity blockEntity, CompoundTag data) {
        return new InventoryOverlay.Context(InventoryOverlay.getBestInventoryType(container, data), container, blockEntity, null, data, handler.getRefreshHandler());
    }
    //#endif
}
