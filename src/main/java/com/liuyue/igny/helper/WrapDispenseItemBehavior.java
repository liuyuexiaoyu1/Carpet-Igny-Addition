package com.liuyue.igny.helper;

import com.liuyue.igny.mixins.rule.disableTntDispense.DefaultDispenseItemBehaviorInvoker;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class WrapDispenseItemBehavior extends DefaultDispenseItemBehavior {

    private final DefaultDispenseItemBehavior originalBehavior;

    private final BiPredicate<BlockSource, ItemStack> beforeExecute;

    private final BiFunction<BlockSource, ItemStack, ItemStack> beforeExecutor;

    private final BiConsumer<BlockSource, ItemStack> preProcess;

    private final BiConsumer<BlockSource, ItemStack> postProcess;

    private final BiFunction<BlockSource, ItemStack, ItemStack> resultProcessor;

    public WrapDispenseItemBehavior(@NotNull DefaultDispenseItemBehavior originalBehavior) {
        this.originalBehavior = originalBehavior;
        this.beforeExecute = (source, stack) -> true;
        this.beforeExecutor = null;
        this.preProcess = (source, stack) -> {};
        this.postProcess = (source, stack) -> {};
        this.resultProcessor = (source, stack) -> stack;
    }

    private WrapDispenseItemBehavior(
            @NotNull DefaultDispenseItemBehavior originalBehavior,
            BiPredicate<BlockSource, ItemStack> beforeExecute,
            BiFunction<BlockSource, ItemStack, ItemStack> beforeExecutor,
            BiConsumer<BlockSource, ItemStack> preProcess,
            BiConsumer<BlockSource, ItemStack> postProcess,
            BiFunction<BlockSource, ItemStack, ItemStack> resultProcessor) {
        this.originalBehavior = originalBehavior;
        this.beforeExecute = beforeExecute != null ? beforeExecute : (source, stack) -> true;
        this.beforeExecutor = beforeExecutor;
        this.preProcess = preProcess != null ? preProcess : (source, stack) -> {};
        this.postProcess = postProcess != null ? postProcess : (source, stack) -> {};
        this.resultProcessor = resultProcessor != null ? resultProcessor : (source, stack) -> stack;
    }

    @Override
    protected @NotNull ItemStack execute(BlockSource blockSource, ItemStack item) {
        if (!beforeExecute.test(blockSource, item)) {
            return item;
        }
        if (beforeExecutor != null) {
            ItemStack beforeResult = beforeExecutor.apply(blockSource, item);
            if (beforeResult != null) {
                return beforeResult;
            }
        }
        preProcess.accept(blockSource, item);
        ItemStack result = ((DefaultDispenseItemBehaviorInvoker) originalBehavior).invokerExecute(blockSource, item);
        postProcess.accept(blockSource, result);
        return resultProcessor.apply(blockSource, result);
    }

    public DefaultDispenseItemBehavior getOriginalBehavior() {
        return originalBehavior;
    }

    public static class Builder {
        private final DefaultDispenseItemBehavior originalBehavior;
        private BiPredicate<BlockSource, ItemStack> beforeExecute;
        private BiFunction<BlockSource, ItemStack, ItemStack> beforeExecutor;
        private BiConsumer<BlockSource, ItemStack> preProcess;
        private BiConsumer<BlockSource, ItemStack> postProcess;
        private BiFunction<BlockSource, ItemStack, ItemStack> resultProcessor;

        public Builder(@NotNull DefaultDispenseItemBehavior originalBehavior) {
            this.originalBehavior = originalBehavior;
        }

        public Builder beforeExecute(BiPredicate<BlockSource, ItemStack> predicate) {
            this.beforeExecute = predicate;
            return this;
        }

        public Builder beforeExecutor(BiFunction<BlockSource, ItemStack, ItemStack> executor) {
            this.beforeExecutor = executor;
            return this;
        }

        public Builder preProcess(BiConsumer<BlockSource, ItemStack> processor) {
            this.preProcess = processor;
            return this;
        }

        public Builder postProcess(BiConsumer<BlockSource, ItemStack> processor) {
            this.postProcess = processor;
            return this;
        }

        public Builder resultProcessor(BiFunction<BlockSource, ItemStack, ItemStack> processor) {
            this.resultProcessor = processor;
            return this;
        }

        public WrapDispenseItemBehavior build() {
            return new WrapDispenseItemBehavior(
                    originalBehavior,
                    beforeExecute,
                    beforeExecutor,
                    preProcess,
                    postProcess,
                    resultProcessor
            );
        }
    }

    public static WrapDispenseItemBehavior withCheck(
            @NotNull DefaultDispenseItemBehavior original,
            BiPredicate<BlockSource, ItemStack> check) {
        return new Builder(original)
                .beforeExecute(check)
                .build();
    }

    public static WrapDispenseItemBehavior withBeforeExecutor(
            @NotNull DefaultDispenseItemBehavior original,
            BiFunction<BlockSource, ItemStack, ItemStack> executor) {
        return new Builder(original)
                .beforeExecutor(executor)
                .build();
    }

    public static WrapDispenseItemBehavior withProcessors(
            @NotNull DefaultDispenseItemBehavior original,
            BiConsumer<BlockSource, ItemStack> pre,
            BiConsumer<BlockSource, ItemStack> post) {
        return new Builder(original)
                .preProcess(pre)
                .postProcess(post)
                .build();
    }

    public static WrapDispenseItemBehavior withResultProcessor(
            @NotNull DefaultDispenseItemBehavior original,
            BiFunction<BlockSource, ItemStack, ItemStack> processor) {
        return new Builder(original)
                .resultProcessor(processor)
                .build();
    }
}