package net.realitiesofdreams.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.entity.StoryNpcEntity;
import net.realitiesofdreams.story.character.CharacterDef;

import java.util.HashMap;
import java.util.Map;

/** Draws story characters with their registry skin on the classic or slim player model. */
public class StoryNpcRenderer extends HumanoidMobRenderer<StoryNpcEntity, PlayerModel<StoryNpcEntity>> {
    private static final Map<String, ResourceLocation> TEXTURES = new HashMap<>();
    private final PlayerModel<StoryNpcEntity> wide;
    private final PlayerModel<StoryNpcEntity> slim;

    public StoryNpcRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        this.wide = this.model;
        this.slim = new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER_SLIM), true);
    }

    @Override
    public void render(StoryNpcEntity entity, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        this.model = Story.content().characters().get(entity.characterId()).map(CharacterDef::slim).orElse(false) ? slim : wide;
        super.render(entity, yaw, partial, pose, buffers, light);
    }

    @Override
    protected void scale(StoryNpcEntity entity, PoseStack pose, float partial) {
        pose.scale(0.9375F, 0.9375F, 0.9375F); // same as players
    }

    @Override
    public ResourceLocation getTextureLocation(StoryNpcEntity entity) {
        return TEXTURES.computeIfAbsent(entity.characterId(), id -> Story.content().characters().get(id)
                .filter(c -> c.texture() != null)
                .map(c -> ResourceLocation.parse(c.texture()))
                .orElse(DefaultPlayerSkin.getDefaultTexture()));
    }
}
