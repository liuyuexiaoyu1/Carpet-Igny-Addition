package com.liuyue.igny.network.handler;

//#if MC >= 12005
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.network.packet.entity.PlaceEntityPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//#if MC >= 12102
//$$ import net.minecraft.world.entity.EntitySpawnReason;
//#endif

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaceEntityHandler {
    private static final Map<ResourceLocation, ResourceLocation> PLACE_ITEM_BY_ENTITY = Map.ofEntries(
            // 盔甲架 / 末影水晶 / 画 / 物品展示框 / 荧光物品展示框
            entry("armor_stand", "armor_stand"),
            entry("end_crystal", "end_crystal"),
            entry("painting", "painting"),
            entry("item_frame", "item_frame"),
            entry("glow_item_frame", "glow_item_frame"),
            // 船 / 竹筏
            entry("oak_boat", "oak_boat"),
            entry("oak_chest_boat", "oak_chest_boat"),
            entry("spruce_boat", "spruce_boat"),
            entry("spruce_chest_boat", "spruce_chest_boat"),
            entry("birch_boat", "birch_boat"),
            entry("birch_chest_boat", "birch_chest_boat"),
            entry("jungle_boat", "jungle_boat"),
            entry("jungle_chest_boat", "jungle_chest_boat"),
            entry("acacia_boat", "acacia_boat"),
            entry("acacia_chest_boat", "acacia_chest_boat"),
            entry("dark_oak_boat", "dark_oak_boat"),
            entry("dark_oak_chest_boat", "dark_oak_chest_boat"),
            entry("pale_oak_boat", "pale_oak_boat"),
            entry("pale_oak_chest_boat", "pale_oak_chest_boat"),
            entry("mangrove_boat", "mangrove_boat"),
            entry("mangrove_chest_boat", "mangrove_chest_boat"),
            entry("cherry_boat", "cherry_boat"),
            entry("cherry_chest_boat", "cherry_chest_boat"),
            entry("bamboo_raft", "bamboo_raft"),
            entry("bamboo_chest_raft", "bamboo_chest_raft"),
            // 矿车
            entry("minecart", "minecart"),
            entry("chest_minecart", "chest_minecart"),
            entry("furnace_minecart", "furnace_minecart"),
            entry("tnt_minecart", "tnt_minecart"),
            entry("hopper_minecart", "hopper_minecart"),
            entry("command_block_minecart", "command_block_minecart")
    );

    private static final Map<UUID, Long> LAST_PLACE_TICK = new ConcurrentHashMap<>();
    private static final long MIN_INTERVAL_TICKS = 1L;

    private static ResourceLocation id(String path) {
        //#if MC <= 12006
        //$$ new ResourceLocation("minecraft", path);
        //#else
        return ResourceLocation.fromNamespaceAndPath("minecraft", path);
        //#endif
    }

    private static Map.Entry<ResourceLocation, ResourceLocation> entry(String entity, String item) {
        return Map.entry(id(entity), id(item));
    }

    public static void handle(ServerPlayer player, PlaceEntityPayload payload) {
        if (!IGNYSettings.BETTER_EASY_PLACE_PROTOCOL.value()) return;
        ServerLevel level = player.serverLevel();
        ResourceLocation itemId = PLACE_ITEM_BY_ENTITY.get(payload.entityTypeId());
        if (itemId == null) {
            return;
        }
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(payload.entityTypeId()).orElse(null);
        Item item = BuiltInRegistries.ITEM.getOptional(itemId).orElse(null);
        if (type == null || item == null) {
            return;
        }
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean hasMain = mainHand.getItem() == item;
        boolean hasOff = offHand.getItem() == item;
        if (!hasMain && !hasOff) {
            return;
        }
        if (player.distanceToSqr(payload.x(), payload.y(), payload.z()) > 36.0) {
            return;
        }
        if (payload.y() < level.getMinBuildHeight() || payload.y() > level.getMaxBuildHeight()) {
            return;
        }
        BlockPos pos = BlockPos.containing(payload.x(), payload.y(), payload.z());
        if (!level.isLoaded(pos)) {
            return;
        }
        float yaw = Mth.wrapDegrees(payload.yaw());
        float pitch = Mth.clamp(payload.pitch(), -90.0F, 90.0F);
        long gameTime = level.getGameTime();
        Long last = LAST_PLACE_TICK.get(player.getUUID());
        if (last != null && gameTime - last < MIN_INTERVAL_TICKS) {
            return;
        }
        LAST_PLACE_TICK.put(player.getUUID(), gameTime);
        Entity entity;
        //#if MC >= 12102
        //$$ entity = type.create(level, EntitySpawnReason.TRIGGERED);
        //#else
        entity = type.create(level);
        //#endif
        if (entity == null) {
            return;
        }
        entity.moveTo(payload.x(), payload.y(), payload.z(), yaw, pitch);
        level.addFreshEntityWithPassengers(entity);

        if (!player.getAbilities().instabuild) {
            if (hasMain) {
                mainHand.shrink(1);
            } else {
                offHand.shrink(1);
            }
        }
    }
}
//#endif
