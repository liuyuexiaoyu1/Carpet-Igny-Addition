package com.liuyue.igny.mixins.rule.noDuplicateVillagerEnchants;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.core.Holder;
//#if MC >= 12005
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
//#endif
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
//#if MC >= 26.1
//$$ import net.minecraft.world.entity.npc.VillagerProfession;
//$$ import net.minecraft.resources.ResourceKey;
//#endif
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 12111
//$$ import net.minecraft.server.level.ServerLevel;
//#endif
//#if MC < 12005
//$$ import net.minecraft.core.registries.BuiltInRegistries;
//$$ import java.util.Map;
//$$ import net.minecraft.world.item.enchantment.EnchantmentHelper;
//#endif



@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {

    @Unique
    private static final int MAX_RETRIES = 100;
    @Unique
    private static final int RESET_THRESHOLD = 30;
    @Unique
    private static final String HISTORY_KEY = "EnchantmentHistory";
    @Unique
    private ListTag enchantmentHistory = new ListTag();

    public VillagerMixin(net.minecraft.world.entity.EntityType<? extends AbstractVillager> entityType, net.minecraft.world.level.Level level) {
        super(entityType, level);
    }

    //#if MC >= 12108
//$$    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
//$$    private void injectHistorySave(net.minecraft.world.level.storage.ValueOutput output, CallbackInfo ci) {
//$$        if (this.enchantmentHistory != null && !this.enchantmentHistory.isEmpty()) {
//$$            net.minecraft.world.level.storage.ValueOutput.ValueOutputList listOutput = output.childrenList(HISTORY_KEY);
//$$            for (int i = 0; i < this.enchantmentHistory.size(); i++) {
//$$                CompoundTag node = this.enchantmentHistory.getCompoundOrEmpty(i);
//$$                net.minecraft.world.level.storage.ValueOutput element = listOutput.addChild();
//$$                element.putString("id", node.getString("id").orElse(""));
//$$                element.putInt("max_level", node.getInt("max_level").orElse(0));
//$$                element.putInt("min_price_at_max_level", node.getInt("min_price_at_max_level").orElse(0));
//$$            }
//$$        }
//$$    }
//#else
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void injectHistorySave(CompoundTag compound, CallbackInfo ci) {
        if (this.enchantmentHistory != null && !this.enchantmentHistory.isEmpty()) {
            compound.put(HISTORY_KEY, this.enchantmentHistory);
        }
    }
//#endif

    //#if MC >= 12108
//$$    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
//$$    private void injectHistoryLoad(net.minecraft.world.level.storage.ValueInput input, CallbackInfo ci) {
//$$        this.enchantmentHistory = new ListTag();
//$$        try {
//$$            net.minecraft.world.level.storage.ValueInput.ValueInputList listInput = input.childrenListOrEmpty(HISTORY_KEY);
//$$            for (net.minecraft.world.level.storage.ValueInput element : listInput) {
//$$                CompoundTag node = new CompoundTag();
//$$                node.putString("id", element.getStringOr("id", ""));
//$$                node.putInt("max_level", element.getIntOr("max_level", 0));
//$$                node.putInt("min_price_at_max_level", element.getIntOr("min_price_at_max_level", 0));
//$$                this.enchantmentHistory.add(node);
//$$            }
//$$        } catch (Exception e) {
//$$            this.enchantmentHistory = new ListTag();
//$$        }
//$$    }
//#else
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void injectHistoryLoad(CompoundTag compound, CallbackInfo ci) {
        //#if MC >= 12105
        //$$ if (compound.contains(HISTORY_KEY))
        //#else
        if (compound.contains(HISTORY_KEY, 9))
        //#endif
        {
            //#if MC >= 12105
            //$$ this.enchantmentHistory = compound.getList(HISTORY_KEY).orElse(new ListTag());
            //#else
            this.enchantmentHistory = compound.getList(HISTORY_KEY, 10);
            //#endif
        } else {
            this.enchantmentHistory = new ListTag();
        }
    }
//#endif

    //#if MC >= 26.1
//$$    @Inject(method = "updateTrades", at = @At("TAIL"))
//$$    private void filterAndReRollEnchants(net.minecraft.server.level.ServerLevel serverLevel, CallbackInfo ci) {
//$$        if (this.level().isClientSide() || !IGNYSettings.NO_DUPLICATE_VILLAGER_ENCHANTS.value()) return;
//$$
//$$        Villager villager = (Villager) (Object) this;
//$$        VillagerData data = villager.getVillagerData();
//$$        VillagerProfession profession = (VillagerProfession) data.profession().value();
//$$
//$$        ResourceKey<net.minecraft.world.item.trading.TradeSet> tradesKey = profession.getTrades(data.level());
//$$        if (tradesKey == null) return;
//$$
//$$        MerchantOffers offers = villager.getOffers();
//$$        int newTradesCount = 2;
//$$        int totalOffers = offers.size();
//$$        int startIndex = Math.max(0, totalOffers - newTradesCount);
//$$
//$$        for (int i = startIndex; i < totalOffers; i++) {
//$$            MerchantOffer offer = offers.get(i);
//$$
//$$            if (offer.getResult().getItem() != Items.ENCHANTED_BOOK) continue;
//$$
//$$            int retries = 0;
//$$            boolean valid = checkAndRecordOffer(offer);
//$$
//$$            while (!valid && retries < MAX_RETRIES) {
//$$
//$$                if (retries >= RESET_THRESHOLD) {
//$$                    this.enchantmentHistory = new ListTag();
//$$                    checkAndRecordOffer(offer);
//$$                    break;
//$$                }
//$$
//$$                MerchantOffers tempOffers = new MerchantOffers();
//$$                this.addOffersFromTradeSet(serverLevel, tempOffers, tradesKey);
//$$
//$$                if (!tempOffers.isEmpty()) {
//$$                    MerchantOffer replacementOffer = tempOffers.get(this.random.nextInt(tempOffers.size()));
//$$                    if (replacementOffer != null) {
//$$                        offer = replacementOffer;
//$$                        valid = checkAndRecordOffer(offer);
//$$                    }
//$$                }
//$$                retries++;
//$$            }
//$$
//$$            offers.set(i, offer);
//$$        }
//$$    }
//#else
    @Inject(method = "updateTrades", at = @At("TAIL"))
    private void filterAndReRollEnchants(CallbackInfo ci) {
        if (this.level().isClientSide() || !IGNYSettings.NO_DUPLICATE_VILLAGER_ENCHANTS.value()) return;

        Villager villager = (Villager) (Object) this;
        VillagerData villagerData = villager.getVillagerData();

        it.unimi.dsi.fastutil.ints.Int2ObjectMap<net.minecraft.world.entity.npc.VillagerTrades.ItemListing[]> tradesMap;
        //#if MC >= 12006
        if (this.level().enabledFeatures().contains(net.minecraft.world.flag.FeatureFlags.TRADE_REBALANCE)) {
            //#if MC >= 12105
            //$$ var experimental = net.minecraft.world.entity.npc.VillagerTrades.EXPERIMENTAL_TRADES.get(villagerData.profession().unwrapKey().orElse(null));
            //$$ tradesMap = experimental != null ? experimental : net.minecraft.world.entity.npc.VillagerTrades.TRADES.get(villagerData.profession().unwrapKey().orElse(null));
            //#else
            var experimental = net.minecraft.world.entity.npc.VillagerTrades.EXPERIMENTAL_TRADES.get(villagerData.getProfession());
            tradesMap = experimental != null ? experimental : net.minecraft.world.entity.npc.VillagerTrades.TRADES.get(villagerData.getProfession());
            //#endif
        } else
        //#endif
        {
            //#if MC >= 12105
            //$$ tradesMap = net.minecraft.world.entity.npc.VillagerTrades.TRADES.get(villagerData.profession().unwrapKey().orElse(null));
            //#else
            tradesMap = net.minecraft.world.entity.npc.VillagerTrades.TRADES.get(villagerData.getProfession());
            //#endif
        }

        if (tradesMap == null || tradesMap.isEmpty()) return;
        //#if MC >= 12105
        //$$ net.minecraft.world.entity.npc.VillagerTrades.ItemListing[] listings = tradesMap.get(villagerData.level());
        //#else
        net.minecraft.world.entity.npc.VillagerTrades.ItemListing[] listings = tradesMap.get(villagerData.getLevel());
        //#endif
        if (listings == null || listings.length == 0) return;

        MerchantOffers offers = villager.getOffers();
        int newTradesCount = 2;
        int totalOffers = offers.size();
        int startIndex = Math.max(0, totalOffers - newTradesCount);

        for (int i = startIndex; i < totalOffers; i++) {
            MerchantOffer offer = offers.get(i);

            if (offer.getResult().getItem() != Items.ENCHANTED_BOOK) continue;

            int retries = 0;
            boolean valid = checkAndRecordOffer(offer);

            while (!valid && retries < MAX_RETRIES) {

                if (retries >= RESET_THRESHOLD) {
                    this.enchantmentHistory = new ListTag();
                    checkAndRecordOffer(offer);
                    break;
                }

                net.minecraft.world.entity.npc.VillagerTrades.ItemListing randomListing = listings[this.random.nextInt(listings.length)];
                //#if MC >= 12111
                //$$ MerchantOffer replacementOffer = randomListing.getOffer((ServerLevel) level(), villager, this.random);
                //#else
                MerchantOffer replacementOffer = randomListing.getOffer(villager, this.random);
                //#endif

                if (replacementOffer != null) {
                    offer = replacementOffer;
                    valid = checkAndRecordOffer(offer);
                }
                retries++;
            }

            offers.set(i, offer);
        }
    }
//#endif

    @Unique
    private boolean checkAndRecordOffer(MerchantOffer offer) {
        ItemStack book = offer.getResult();
        //#if MC >= 12005
        ItemEnchantments enchantmentsComponent = book.get(DataComponents.STORED_ENCHANTMENTS);
        if (enchantmentsComponent == null || enchantmentsComponent.isEmpty()) return true;
        var entry = enchantmentsComponent.entrySet().iterator().next();
        Holder<Enchantment> currentEnchantHolder = entry.getKey();
        int currentLevel = entry.getIntValue();
        int currentPrice = offer.getBaseCostA().getCount();
        ResourceLocation enchantId = currentEnchantHolder.unwrapKey()
                .map(net.minecraft.resources.ResourceKey::location)
                .orElse(null);
        if (enchantId == null) return true;
        String idStr = enchantId.toString();
        //#else
        //$$ Map<Enchantment, Integer> enchantmentsMap = EnchantmentHelper.getEnchantments(book);
        //$$ if (enchantmentsMap.isEmpty()) return true;
        //$$ Map.Entry<Enchantment, Integer> entry = enchantmentsMap.entrySet().iterator().next();
        //$$ Enchantment currentEnchant = entry.getKey();
        //$$ int currentLevel = entry.getValue();
        //$$ int currentPrice = offer.getBaseCostA().getCount();
        //$$ ResourceLocation actualId = BuiltInRegistries.ENCHANTMENT.getKey(currentEnchant);
        //$$ if (actualId == null) return true;
        //$$ String idStr = actualId.toString();
        //#endif

        boolean foundHistory = false;
        boolean allowed = true;

        for (int i = 0; i < this.enchantmentHistory.size(); i++) {
            //#if MC >= 12105
            //$$ CompoundTag historyNode = this.enchantmentHistory.getCompoundOrEmpty(i);
            //$$ if (idStr.equals(historyNode.getString("id").orElse("")))
            //#else
            CompoundTag historyNode = this.enchantmentHistory.getCompound(i);
            if (idStr.equals(historyNode.getString("id")))
            //#endif
            {
                foundHistory = true;
                //#if MC >= 12105
                //$$ int maxLevelSaved = historyNode.getInt("max_level").orElse(0);
                //$$ int minPriceAtMaxLevel = historyNode.getInt("min_price_at_max_level").orElse(0);
                //#else
                int maxLevelSaved = historyNode.getInt("max_level");
                int minPriceAtMaxLevel = historyNode.getInt("min_price_at_max_level");
                //#endif

                if (currentLevel > maxLevelSaved) {
                    historyNode.putInt("max_level", currentLevel);
                    historyNode.putInt("min_price_at_max_level", currentPrice);
                } else if (currentLevel == maxLevelSaved && currentPrice < minPriceAtMaxLevel) {
                    historyNode.putInt("min_price_at_max_level", currentPrice);
                } else {
                    allowed = false;
                }
                break;
            }
        }

        if (!foundHistory) {
            CompoundTag newNode = new CompoundTag();
            newNode.putString("id", idStr);
            newNode.putInt("max_level", currentLevel);
            newNode.putInt("min_price_at_max_level", currentPrice);
            this.enchantmentHistory.add(newNode);
        }

        return allowed;
    }
}