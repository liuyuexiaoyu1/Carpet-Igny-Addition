package com.liuyue.igny.mixins.carpet.features.SetDefaultArgument;

import carpet.CarpetSettings;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.RuleHelper;
import carpet.api.settings.SettingsManager;
import carpet.utils.CommandHelper;
import carpet.utils.TranslationKeys;
import com.liuyue.igny.IGNYSettings;
import com.liuyue.igny.utils.CommandPermissions;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static carpet.utils.Translations.tr;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.SharedSuggestionProvider.suggest;

@Mixin(SettingsManager.class)
public abstract class SettingsManagerMixin {


    @Shadow
    public abstract String identifier();

    @Shadow
    protected abstract int listAllSettings(CommandSourceStack source);

    @Shadow
    @Final
    private String identifier;

    @Shadow
    public abstract boolean locked();

    @Shadow
    @Final
    private String fancyName;

    @Shadow
    protected abstract int listSettings(CommandSourceStack source, String title, Collection<CarpetRule<?>> settings_list);

    @Shadow
    protected abstract Collection<CarpetRule<?>> getRulesSorted();

    @Shadow
    protected abstract Collection<CarpetRule<?>> findStartupOverrides();

    @Shadow
    public abstract Iterable<String> getCategories();

    @Shadow
    protected abstract Collection<CarpetRule<?>> getRulesMatching(String search);

    @Shadow
    static CompletableFuture<Suggestions> suggestMatchingContains(Stream<String> stream, SuggestionsBuilder suggestionsBuilder) {
        return null;
    }

    @Shadow
    protected abstract int removeDefault(CommandSourceStack source, CarpetRule<?> rule);

    @Shadow
    protected abstract CarpetRule<?> contextRule(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;

    @Shadow
    protected abstract int setDefault(CommandSourceStack source, CarpetRule<?> rule, String stringValue);

    @Shadow
    protected abstract int displayRuleMenu(CommandSourceStack source, CarpetRule<?> rule);


    @Shadow
    protected abstract int setRule(CommandSourceStack source, CarpetRule<?> rule, String newValue);

    @Inject(method = "registerCommand", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;executes(Lcom/mojang/brigadier/Command;)Lcom/mojang/brigadier/builder/ArgumentBuilder;"), cancellable = true)
    private void onRegisterCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, CallbackInfo ci, @Local(name = "literalargumentbuilder") LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder) {
        literalargumentbuilder.executes((context)-> this.listAllSettings(context.getSource())).
                then(literal("list").
                        executes( (c) -> this.listSettings(c.getSource(), String.format(tr(TranslationKeys.ALL_MOD_SETTINGS), this.fancyName),
                                this.getRulesSorted())).
                        then(literal("defaults").
                                executes( (c)-> listSettings(c.getSource(),
                                        String.format(tr(TranslationKeys.CURRENT_FROM_FILE_HEADER), fancyName, (identifier+".conf")),
                                        this.findStartupOverrides()))).
                        then(argument("tag", StringArgumentType.word()).
                                suggests( (c, b)->suggest(this.getCategories(), b)).
                                executes( (c) -> listSettings(c.getSource(),
                                        String.format(tr(TranslationKeys.MOD_SETTINGS_MATCHING), fancyName, RuleHelper.translatedCategory(identifier(),StringArgumentType.getString(c, "tag"))),
                                        this.getRulesMatching(StringArgumentType.getString(c, "tag")))))).
                then(literal("removeDefault").
                        requires(s -> !locked()).
                        then(argument("rule", StringArgumentType.word()).
                                suggests( (c, b) -> suggestMatchingContains(getRulesSorted().stream().map(CarpetRule::name), b)).
                                executes((c) -> this.removeDefault(c.getSource(), this.contextRule(c))))).
                then(literal("setDefault").
                        requires(s -> !locked()).
                        then(argument("rule", StringArgumentType.word()).
                                suggests( (c, b) -> suggestMatchingContains(getRulesSorted().stream().map(CarpetRule::name), b)).
                                then(argument("value", StringArgumentType.greedyString()).
                                        suggests((c, b)-> suggest(contextRule(c).suggestions(), b)).
                                        executes((c) -> this.setDefault(c.getSource(), contextRule(c), StringArgumentType.getString(c, "value")))))).
                then(argument("rule", StringArgumentType.word()).
                        suggests( (c, b) -> suggestMatchingContains(getRulesSorted().stream().map(CarpetRule::name), b)).
                        requires(s -> !locked()).
                        executes( (c) -> this.displayRuleMenu(c.getSource(), contextRule(c))).
                        then(argument("value", StringArgumentType.string()).
                                suggests((c, b)-> suggest(contextRule(c).suggestions(),b)).
                                executes((c) -> customSetRule(c.getSource(), contextRule(c), StringArgumentType.getString(c, "value"), false)).
                                then(argument("setDefault", BoolArgumentType.bool()).
                                        requires(this::canUseSetDefault).
                                        executes((c) -> customSetRule(c.getSource(), contextRule(c), StringArgumentType.getString(c, "value"), BoolArgumentType.getBool(c, "setDefault"))))));

        dispatcher.register(literalargumentbuilder);
        ci.cancel();
    }
    @Unique
    private boolean canUseSetDefault(CommandSourceStack source){
        return CommandPermissions.canUseCommand(source, IGNYSettings.setDefaultArgument);
    }

    @Unique
    private int customSetRule(CommandSourceStack source, CarpetRule<?> rule, String newValue, Boolean setDefault)
    {
        if (setDefault){
            this.setDefault(source, rule, newValue);
        }else {
            this.setRule(source, rule, newValue);
        }
        return 1;
    }

}