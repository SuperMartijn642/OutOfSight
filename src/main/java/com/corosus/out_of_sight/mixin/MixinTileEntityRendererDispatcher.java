package com.corosus.out_of_sight.mixin;

import com.corosus.out_of_sight.OutOfSight;
import com.corosus.out_of_sight.config.Config;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class MixinTileEntityRendererDispatcher {

    @Redirect(method = "renderEntities",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;render(Lnet/minecraft/tileentity/TileEntity;FI)V"))
    public <E extends TileEntity> void renderTileEntity(TileEntityRendererDispatcher dispatcher, E tileEntityIn, float partialTicks, int destroyStage) {
        double dist = getDistanceSq(tileEntityIn, dispatcher.renderInfo.getProjectedView().x, dispatcher.renderInfo.getProjectedView().y, dispatcher.renderInfo.getProjectedView().z);
        if (dist > Config.GENERAL.tileEntityRenderRangeMax.get() * Config.GENERAL.tileEntityRenderRangeMax.get()) {
            if (!Config.GENERAL.tileEntityRenderLimitModdedOnly.get() || !OutOfSight.getCanonicalNameCached(tileEntityIn.getClass()).startsWith("net.minecraft")) {
                return;
            }
        }
        dispatcher.render(tileEntityIn, partialTicks, destroyStage);
    }

    public double getDistanceSq(TileEntity tileEntity, double x, double y, double z) {
        double d0 = (double)tileEntity.getPos().getX() + 0.5D - x;
        double d1 = (double)tileEntity.getPos().getY() + 0.5D - y;
        double d2 = (double)tileEntity.getPos().getZ() + 0.5D - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }
}