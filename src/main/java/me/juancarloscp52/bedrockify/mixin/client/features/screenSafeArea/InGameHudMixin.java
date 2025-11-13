package me.juancarloscp52.bedrockify.mixin.client.features.screenSafeArea;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = InGameHud.class, priority = 500)
public abstract class InGameHudMixin {
    @Unique
    private int screenBorder;

    /**
     * Set the screenBorder area before anything renders.
     */
    @Inject(method = "render", at = @At("HEAD"))
    private void setScreenBorder(CallbackInfo info) {
        this.screenBorder = BedrockifyClient.getInstance().settings.getScreenSafeArea();
    }

    /**
     * Render the item Hotbar applying the screen border distance and transparency.
     */
    @WrapOperation(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
    private void drawTextureHotbar(DrawContext drawContext, RenderPipeline pipeline, Identifier texture, int x, int y, int width, int height, Operation<Void> original) {
        if(texture.equals(Identifier.ofVanilla("hud/hotbar_selection"))){
            original.call(drawContext, pipeline, texture, x, y - screenBorder, width, 24);
            if(BedrockifyClient.getInstance().settings.hotBarOverhang)
                drawContext.fill(x,y + height - screenBorder,x+width,y+height+1 - screenBorder, ColorHelper.getArgb((int)(255 * BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false)),0,0,0));
        }else{
            original.call(drawContext, pipeline, texture, x, y - screenBorder, width, height);
        }
    }
    @WrapOperation(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIIIIIII)V"))
    private void drawTextureHotbar(DrawContext drawContext, RenderPipeline pipeline, Identifier texture, int i, int j, int k, int l, int x, int y, int width, int height, Operation<Void> original) {
        if((width ==29 && height == 24) || width == 182){
            drawContext.drawGuiTexture(pipeline, texture, i, j, k, l, x, y - screenBorder, width, height, ColorHelper.withAlpha(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(true), -1));
        }else{
            boolean raisedEnabled = FabricLoader.getInstance().isModLoaded("raised");
            drawContext.drawGuiTexture(pipeline, texture, i, j, k, l, x, y - screenBorder, width, (width  == 24 && !raisedEnabled) ? height+2 : height, ColorHelper.withAlpha(BedrockifyClient.getInstance().hudOpacity.getHudOpacity(true), -1));
        }
    }

    /**
     * Render the items in the Hotbar with the screen border distance.
     */
    @ModifyArg(method = "renderHotbar", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V"),index = 2)
    public int modifyHotbarItemPossition(int y){
        return y-screenBorder;
    }

    /**
     * Apply screen border offset to mount health bars.
     */
    @ModifyArg(method = "renderMountHealth", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"),index = 3)
    public int modifyTextureMountHealth(int y){
        return y-screenBorder;
    }

    /**
     * Apply screen border offset to health bars.
     */
    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V"),index = 3)
    private int modifyTextureStatusBarsHearts(int y){
        return y-screenBorder;
    }
    /**
     * Apply screen border offset to armor bars.
     */
    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/hud/InGameHud;renderArmor(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIII)V"), index = 2)
    private int modifyTextureStatusBarsArmor(int i){
        return i-screenBorder;
    }
    /**
     * Apply screen border offset to food bars.
     */
    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/hud/InGameHud;renderFood(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;II)V"),index = 2)
    private int modifyTextureStatusBarsFood(int y){
        return y-screenBorder;
    }

    /**
     * Render the status effect overlay with the screen border distance applied.
     */
    @ModifyVariable(method = "renderStatusEffectOverlay", at = @At("STORE"),ordinal = 2)
    public int modifyStatusEffectOverlayX(int x){
        return x-screenBorder;
    }
    @ModifyVariable(method = "renderStatusEffectOverlay", at = @At("STORE"),ordinal = 3)
    public int modifyStatusEffectOverlayY(int y){
        return y+screenBorder;
    }

    // Apply screen borders to Titles, subtitles and other messages.
    @ModifyArg(method = "renderTitleAndSubtitle", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithBackground(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIII)V", ordinal = 0),index = 3)
    public int modifyOverlayMessage(int y){
        return y-screenBorder;
    }

    @ModifyExpressionValue(method = "renderAirBubbles", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/hud/InGameHud;getAirBubbleY(II)I"))
    private int bedrockify$modifyTextureStatusBarsBubbleY(int original){
        return original - screenBorder;
    }
}
