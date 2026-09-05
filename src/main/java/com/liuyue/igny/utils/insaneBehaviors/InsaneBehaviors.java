/*
 * This file is part of the JoaCarpet project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Joa and contributors
 *
 * Ported to Carpet IGNY Addition (com.liuyue.igny) under LGPL-3.0.
 * Only the insaneBehaviors rule family is ported; all credits for the
 * original logic belong to the JoaCarpet authors.
 */

package com.liuyue.igny.utils.insaneBehaviors;

import com.liuyue.igny.IGNYSettings;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class InsaneBehaviors {
    private static int counter = 0;
    private static int resolution = 2;

    public static ArrayList<Float> nextEvenlyDistributedPoint(int dimensions) {
        while (true) {
            int remainder = counter;
            ArrayList<Float> list = new ArrayList<>();

            for (int i = 0; i < dimensions; i++) {
                list.add((float) (remainder % resolution) / (resolution-1));
                remainder = remainder / resolution;
            }

            var visited = false;
            float previousStepSize = 1 / ((float) (resolution - 1) / 2);
            if (IGNYSettings.INSANE_BEHAVIORS_SKIP_VISITED_POINTS.value()
                    && resolution != 2
                    && list.stream().allMatch(i -> i % previousStepSize == 0)) {
                visited = true;
            }

            if (!IGNYSettings.INSANE_BEHAVIORS_INCREMENT.value().equals("freeze")) {
                counter++;
                if (counter >= Math.pow(resolution, dimensions)) {
                    if (IGNYSettings.INSANE_BEHAVIORS_INCREMENT.value().equals("normal"))
                        resolution = (resolution - 1) * 2 + 1;
                    counter = 0;
                }
            }

            if (visited && !IGNYSettings.INSANE_BEHAVIORS_INCREMENT.value().equals("freeze")) continue;
            return list;
        }
    }

    public static Vec3 mapUnitVelocityToTriangularDistribution(Vec3 velocity, int scaleFactor, double xCenter, double xVariance, double yCenter, double yVariance, double zCenter, double zVariance) {
        return new Vec3(
                xCenter + (velocity.x -0.5) * 2 * xVariance * scaleFactor,
                yCenter + (velocity.y -0.5) * 2 * yVariance * scaleFactor,
                zCenter + (velocity.z -0.5) * 2 * zVariance * scaleFactor
        );
    }

    public static int resetCounterAndResolution(CommandSourceStack c) {
        counter = 0;
        resolution = 2;
        c.sendSuccess(
                //#if MC > 11904
                () ->
                //#endif
                Component.translatable("igny.command.insanebehaviors.reset"), false);
        return 1;
    }

    public static int getState(CommandSourceStack c) {
        c.sendSuccess(
                //#if MC > 11904
                () ->
                //#endif
                Component.translatable("igny.command.insanebehaviors.getstate", resolution, counter), false);
        return 1;
    }

    public static int setState(CommandSourceStack c, int _resolution, int _counter) {
        resolution = _resolution;
        counter = _counter;
        c.sendSuccess(
                //#if MC > 11904
                () ->
                //#endif
                Component.translatable("igny.command.insanebehaviors.setState", resolution, counter), false);
        return 1;
    }
}