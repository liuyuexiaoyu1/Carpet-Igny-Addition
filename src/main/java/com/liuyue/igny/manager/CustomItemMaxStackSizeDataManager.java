package com.liuyue.igny.manager;

//#if MC >= 12006
import com.google.gson.reflect.TypeToken;
import com.liuyue.igny.network.packet.PacketUtil;
import com.mojang.brigadier.StringReader;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
//#endif

//#if MC >= 12006
public class CustomItemMaxStackSizeDataManager extends BaseDataManager<Map<String, Integer>> {
    //#else
    //$$ public class CustomItemMaxStackSizeDataManager {
    //#endif
    //#if MC >= 12006
    public static final CustomItemMaxStackSizeDataManager INSTANCE = new CustomItemMaxStackSizeDataManager();

    private Map<String, Integer> customStacks = new HashMap<>();
    private final List<StackRule> runtimeRules = new ArrayList<>();

    @Override protected String getFileName() { return "custom_item_max_stack_size.json"; }
    @Override protected Type getDataType() { return new TypeToken<Map<String, Integer>>(){}.getType(); }
    @Override public Map<String, Integer> getDefaultData() { return new HashMap<>(); }

    @Override
    protected void applyData(Map<String, Integer> data) {
        this.customStacks = new HashMap<>(data);
        if (server != null) {
            rebuildRuntimeRules(CommandBuildContext.simple(server.registryAccess(), server.getWorldData().enabledFeatures()));
        }
    }

    public void remove(String pattern, CommandBuildContext context) {
        customStacks.remove(pattern);
        rebuildAndBroadcast(context);
    }

    public void clear() {
        customStacks.clear();
        runtimeRules.clear();
        save();
    }

    private void rebuildAndBroadcast(CommandBuildContext context) {
        rebuildRuntimeRules(context);
        save();
        if (server != null) {
            server.getPlayerList().getPlayers().forEach(PacketUtil::sendCustomStackSizeToClient);
        }
    }

    @Override public Map<String, Integer> getCurrentData() { return customStacks; }

    public int getCustomStackSize(ItemStack stack) {
        for (StackRule rule : runtimeRules) {
            if (rule.predicate.test(stack)) return rule.size;
        }
        return -1;
    }

    public void clientUpdateData(Map<String, Integer> newData) {
        this.customStacks = new HashMap<>(newData);
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null && minecraft.getConnection() != null) {
            CommandBuildContext context = CommandBuildContext.simple(
                    minecraft.getConnection().registryAccess(),
                    minecraft.getConnection().enabledFeatures()
            );
            rebuildRuntimeRules(context);
        }
    }

    public void set(String pattern, int count, CommandBuildContext context) {
        customStacks.put(pattern, count);
        rebuildRuntimeRules(context);
        save();
        if (server != null) {
            server.getPlayerList().getPlayers().forEach(PacketUtil::sendCustomStackSizeToClient);
        }
    }

    private void rebuildRuntimeRules(CommandBuildContext context) {
        List<StackRule> newRules = new ArrayList<>();
        customStacks.forEach((pattern, count) -> {
            try {
                var result = ItemPredicateArgument.itemPredicate(context).parse(new StringReader(pattern));
                newRules.add(new StackRule(pattern, result, count));
            } catch (Exception e) {
                ResourceLocation rl = ResourceLocation.tryParse(pattern);
                if (rl != null && BuiltInRegistries.ITEM.containsKey(rl)) {
                    Item item = BuiltInRegistries.ITEM.
                            //#if MC >= 12102
                            //$$ getValue(rl);
                            //#else
                                    get(rl);
                    //#endif
                    newRules.add(new StackRule(pattern, stack -> stack.is(item), count));
                }
            }
        });
        runtimeRules.clear();
        runtimeRules.addAll(newRules);
    }

    private record StackRule(String pattern, Predicate<ItemStack> predicate, int size) {}
    //#endif
}