package net.realitiesofdreams.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.realitiesofdreams.client.ClientAppearance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Draws a player with their campaign appearance (Primordial Steve) when the server has assigned one. */
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
    @Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
    private void realitiesofdreams$campaignSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        PlayerSkin skin = ClientAppearance.skinFor(((AbstractClientPlayer) (Object) this).getUUID());
        if (skin != null) cir.setReturnValue(skin);
    }
}
