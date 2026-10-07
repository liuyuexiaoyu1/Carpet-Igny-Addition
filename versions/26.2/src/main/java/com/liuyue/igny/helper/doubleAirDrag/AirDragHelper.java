package com.liuyue.igny.helper.doubleAirDrag;

public final class AirDragHelper {
    public static double restoreDouble(final double drag) {
        if (drag == 0.98F) {
            return 0.98D;
        }
        if (drag == 0.99F) {
            return 0.99D;
        }
        if (drag == 0.95F) {
            return 0.95D;
        }
        if (drag == 0.91F) {
            return 0.91D;
        }
        return drag;
    }
}
