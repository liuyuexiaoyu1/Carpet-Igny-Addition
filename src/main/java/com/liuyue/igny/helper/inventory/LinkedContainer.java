package com.liuyue.igny.helper.inventory;

import com.liuyue.igny.utils.interfaces.linkableEnderChest.ViewingChest;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
//#if MC >= 12109
//$$ import net.minecraft.world.entity.ContainerUser;
//#endif

public class LinkedContainer extends PlayerEnderChestContainer {

    private final String key;

    private final Set<EnderChestBlockEntity>
            activeChests = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public LinkedContainer(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public void setActiveChest(EnderChestBlockEntity chest) {
        activeChests.add(chest);
    }

    @Override
    //#if MC >= 12109
    //$$ public void startOpen(ContainerUser player)
    //#else
    public void startOpen(Player player)
    //#endif
    {
        if (player instanceof ViewingChest) {
            EnderChestBlockEntity chest = ((ViewingChest) player).igny$getContextChest();
            if (chest != null && !chest.isRemoved()) {
                chest.startOpen(player);
            }
        }
    }

    @Override
    //#if MC >= 12109
    //$$ public void stopOpen(ContainerUser player)
    //#else
    public void stopOpen(Player player)
    //#endif
    {
        if (player instanceof ViewingChest) {
            EnderChestBlockEntity chest = ((ViewingChest) player).igny$getContextChest();
            if (chest != null) {
                chest.stopOpen(player);
                ((ViewingChest) player).igny$setContextChest(null);
            }
            ((ViewingChest) player).igny$setLinkedKey(null);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public boolean isActiveChest(EnderChestBlockEntity chest) {
        return activeChests.contains(chest);
    }

    public void removeActiveChest(EnderChestBlockEntity chest) {
        activeChests.remove(chest);
    }

    @Override
    public void clearContent() {}

    @Override
    public void setChanged() {
        super.setChanged();
        for (EnderChestBlockEntity chest : activeChests) {
            Level level = chest.getLevel();
            if (level != null) {
                level.updateNeighbourForOutputSignal(chest.getBlockPos(), chest.getBlockState().getBlock());
            }
        }
    }
}