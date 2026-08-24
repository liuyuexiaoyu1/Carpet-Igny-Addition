package com.liuyue.igny.tracker;

import com.liuyue.igny.manager.RuleChangeDataManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

public class RuleChangeTracker {

    /**
     * 规则变更数据由 {@link com.liuyue.igny.manager.BaseDataManager#setServerAll} 统一加载，
     * 此方法保留仅为兼容旧调用（如独立使用 RuleChangeDataManager 时）。
     */
    public static void init(MinecraftServer server){
            RuleChangeDataManager.INSTANCE.setServer(server);
    }

    public static <T> void ruleChanged(CommandSourceStack source, carpet.api.settings.CarpetRule<T> rule, T rawValue, String userInput) {
            String sourceName = getSourceName(source);
            long timestamp = System.currentTimeMillis();

            RuleChangeDataManager.INSTANCE.recordRuleChange(
                    rule.name(),
                    rawValue,
                    userInput,
                    sourceName,
                    timestamp
            );
    }

    private static String getSourceName(CommandSourceStack source) {
        try {
            if (source.isPlayer()) {
                return source.getPlayerOrException().getScoreboardName();
            } else if (source.getEntity() != null) {
                return source.getEntity().getScoreboardName();
            } else {
                return source.getTextName();
            }
        } catch (Exception e) {
            return "Console";
        }
    }
}
