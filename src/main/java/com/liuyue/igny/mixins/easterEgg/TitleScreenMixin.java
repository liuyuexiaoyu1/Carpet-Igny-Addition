package com.liuyue.igny.mixins.easterEgg;

import com.liuyue.igny.manager.EasterEggDataManager;
import com.liuyue.igny.utils.FestivalUtil;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.HashMap;
import java.util.Map;

//#if MC >= 12110
//$$ import net.minecraft.client.input.MouseButtonEvent;
//#endif
//#if MC >= 26.1
//$$ import net.minecraft.client.gui.GuiGraphicsExtractor;
//#elseif MC > 11904
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) { super(title); }

    @Unique private static boolean isPrankFinishedPermanently = false;

    @Unique private final Map<AbstractWidget, Vector2d> originalPos = new HashMap<>();
    @Unique private final Map<AbstractWidget, Vector2d> currentPos = new HashMap<>();
    @Unique private final Map<AbstractWidget, Vector2d> velocity = new HashMap<>();
    @Unique private Button surrenderButton;

    @Unique private boolean isAprilFoolsActive = false;
    @Unique private double totalFleeDistance = 0;
    @Unique private int idleTicks = 0;
    @Unique private double lastMouseX = -1, lastMouseY = -1;

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (isPrankFinishedPermanently || !EasterEggDataManager.INSTANCE.isAprilFoolsActive()) return;

        if (!FestivalUtil.isAprilFoolsDay()) {
            isAprilFoolsActive = false;
            return;
        }

        isAprilFoolsActive = true;
        originalPos.clear();
        currentPos.clear();
        velocity.clear();
        totalFleeDistance = 0;
        idleTicks = 0;

        for (Renderable renderable : ((ScreenAccessor) this).getRenderalbe()) {
            if (renderable instanceof AbstractWidget widget) {
                Vector2d pos = new Vector2d(widget.getX(), widget.getY());
                originalPos.put(widget, new Vector2d(pos));
                currentPos.put(widget, new Vector2d(pos));
                velocity.put(widget, new Vector2d(0, 0));
            }
        }

        this.surrenderButton = Button.builder(Component.translatable("menu.igny.surrender"), button -> this.stopThePrank())
                .bounds(this.width - 80 - 4, this.height - 32, 80, 20)
                .build();

        this.surrenderButton.visible = false;
        this.addRenderableWidget(this.surrenderButton);
    }

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    //#if MC >= 12110
    //$$ private void onButtonClicked(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir)
    //#else
    private void onButtonClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir)
    //#endif
    {
        if (!isAprilFoolsActive || !cir.getReturnValueZ() || !EasterEggDataManager.INSTANCE.isAprilFoolsActive()) return;

        for (Renderable renderable : ((ScreenAccessor) this).getRenderalbe()) {
            if (renderable instanceof AbstractWidget widget && widget != surrenderButton) {
                //#if MC >= 12110
                //$$ if (widget.isMouseOver(event.x(), event.y()))
                //#else
                if (widget.isMouseOver(mouseX, mouseY))
                //#endif
                {
                    String key = getTranslationKey(widget);
                    if ("menu.singleplayer".equals(key) || "menu.multiplayer".equals(key)) {
                        this.stopThePrank();
                        break;
                    }
                }
            }
        }
    }

    @Unique
    private void stopThePrank() {
        isAprilFoolsActive = false;
        isPrankFinishedPermanently = true;

        for (Renderable renderable : ((ScreenAccessor) this).getRenderalbe()) {
            if (renderable instanceof AbstractWidget widget && originalPos.containsKey(widget)) {
                Vector2d orig = originalPos.get(widget);
                widget.setX((int) Math.round(orig.x));
                widget.setY((int) Math.round(orig.y));
            }
        }

        if (this.surrenderButton != null) {
            this.removeWidget(this.surrenderButton);
            this.surrenderButton = null;
        }
    }

    //#if MC >= 26.1
    //$$ @Inject(method = "extractRenderState", at = @At("HEAD"))
    //#else
    @Inject(method = "render", at = @At("HEAD"))
    //#endif
    //#if MC >= 26.1
    //$$ private void applyPclPhysics(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci)
    //#elseif MC > 11904
    private void applyPclPhysics(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci)
    //#else
    //$$ private void applyPclPhysics(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci)
    //#endif
    {
        if (!isAprilFoolsActive || !EasterEggDataManager.INSTANCE.isAprilFoolsActive()) return;

        if (Math.abs(mouseX - lastMouseX) < 0.01 && Math.abs(mouseY - lastMouseY) < 0.01) {
            idleTicks++;
        } else {
            idleTicks = 0;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }

        for (Renderable renderable : ((ScreenAccessor) this).getRenderalbe()) {
            if (!(renderable instanceof AbstractWidget widget) || widget == surrenderButton) continue;

            String key = getTranslationKey(widget);
            if (!("menu.singleplayer".equals(key) || "menu.multiplayer".equals(key))) continue;

            if (!currentPos.containsKey(widget)) continue;
            Vector2d pos = currentPos.get(widget);
            Vector2d vel = velocity.get(widget);

            double rL = pos.x, rT = pos.y, rR = pos.x + widget.getWidth(), rB = pos.y + widget.getHeight();
            double closestX = Math.max(rL, Math.min(mouseX, rR));
            double closestY = Math.max(rT, Math.min(mouseY, rB));
            double dxToRect = mouseX - closestX;
            double dyToRect = mouseY - closestY;
            double distance = Math.sqrt(dxToRect * dxToRect + dyToRect * dyToRect);

            double centerX = pos.x + (widget.getWidth() / 2.0);
            double centerY = pos.y + (widget.getHeight() / 2.0);
            double dirX = mouseX - centerX;
            double dirY = mouseY - centerY;
            double dirDist = Math.sqrt(dirX * dirX + dirY * dirY);

            Vector2d acc = new Vector2d(0, 0);
            double difficulty = 1.0 / (1.0 + Math.pow(totalFleeDistance / 15000.0, 2)) + 0.15;

            double radius = 35.0;
            if (distance < radius) {
                double safeDirDist = Math.max(dirDist, 1.0);
                double falloff = 1.0 - (distance / radius);
                double strength = 16.0 * difficulty;

                acc.x = -(dirX / safeDirDist) * strength * falloff;
                acc.y = -(dirY / safeDirDist) * strength * falloff;

                if (distance < 0.1) {
                    acc.x += (Math.random() - 0.5) * 10;
                    acc.y += (Math.random() - 0.5) * 10;
                }
            }

            if (idleTicks > 100) {
                Vector2d orig = originalPos.get(widget);
                double distToOrig = pos.distance(orig);
                if (distance < radius + 20.0) {
                    idleTicks = 0;
                } else if (distToOrig > 0.1) {
                    if (distToOrig < 0.5 && vel.length() < 0.1) {
                        pos.set(orig.x, orig.y);
                        vel.set(0, 0);
                    } else {
                        acc.x += (orig.x - pos.x) * 0.06;
                        acc.y += (orig.y - pos.y) * 0.06;
                    }
                }
            }

            vel.x = vel.x * 0.78 + acc.x;
            vel.y = vel.y * 0.78 + acc.y;

            double maxSpeed = 16.0;
            if (vel.length() > maxSpeed) vel.normalize().mul(maxSpeed);

            pos.x += vel.x;
            pos.y += vel.y;

            if (pos.x < -widget.getWidth()) pos.x = this.width;
            else if (pos.x > this.width) pos.x = -widget.getWidth();
            if (pos.y < -widget.getHeight()) pos.y = this.height;
            else if (pos.y > this.height) pos.y = -widget.getHeight();

            widget.setX((int) Math.round(pos.x));
            widget.setY((int) Math.round(pos.y));
            totalFleeDistance += vel.length();
            if (this.surrenderButton != null && !this.surrenderButton.visible && totalFleeDistance > 1.0) {
                this.surrenderButton.visible = true;
            }
        }
    }

    @Unique
    private String getTranslationKey(AbstractWidget widget) {
        if (widget.getMessage().getContents() instanceof TranslatableContents tc) {
            return tc.getKey();
        }
        return "";
    }
}