package com.liuyue.igny.utils.interfaces.linkableEnderChest;

import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public interface ViewingChest {
    void igny$setLinkedKey(String key);
    String igny$getLinkedKey();
    void igny$setContextChest(EnderChestBlockEntity chest);
    EnderChestBlockEntity igny$getContextChest();
}
