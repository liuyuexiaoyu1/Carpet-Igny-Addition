package com.liuyue.igny.helper;

import net.minecraft.core.BlockPos;

public class PistonResolveContext {
    public static final ThreadLocal<Boolean> RECORDING = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<FailureReason> FAILURE_REASON = new ThreadLocal<>();

    public static void startRecording() {
        RECORDING.set(true);
        FAILURE_REASON.set(null);
    }

    public static void stopRecording() {
        RECORDING.set(false);
        FAILURE_REASON.remove();
    }

    public static boolean isRecording() {
        return RECORDING.get();
    }

    public static void setFailureReason(FailureReason reason) {
        if (isRecording()) {
            FAILURE_REASON.set(reason);
        }
    }

    public static FailureReason getFailureReason() {
        return FAILURE_REASON.get();
    }

    public enum FailureType {
        TOO_MANY_BLOCKS,
        UNPUSHABLE_BLOCK,
        UNKNOWN
    }

    public static class FailureReason {
        public final FailureType type;
        public final int currentCount;   // for TOO_MANY_BLOCKS
        public final BlockPos blockPos;  // ✅ 单个，for UNPUSHABLE_BLOCK

        public FailureReason(FailureType type) {
            this.type = type;
            this.blockPos = null;
            this.currentCount = -1;
        }

        public FailureReason(FailureType type, BlockPos pos) {
            this.type = type;
            this.blockPos = pos;
            this.currentCount = -1;
        }
    }
}