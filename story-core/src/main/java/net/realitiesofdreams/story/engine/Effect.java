package net.realitiesofdreams.story.engine;

import net.realitiesofdreams.story.scene.SceneDef;

import java.util.List;

/** Something the game must do in the world or on the player's screen. */
public sealed interface Effect {
    record ShowObjective(String sceneLabel, String sceneTitle, String text, SceneDef.Location target, String progress) implements Effect {}
    record ClearObjective() implements Effect {}
    record StartDialogue(String dialogueId) implements Effect {}
    record StartCinematic(String cinematicId) implements Effect {}
    /** kind: follow, stay, spawn, despawn, move */
    record NpcCommand(String kind, String character, String location) implements Effect {}
    record ShowTitle(String title, String subtitle) implements Effect {}
    record GiveItem(String item, int count, String name, List<String> lore) implements Effect {}
    record PlaySound(String sound, float volume, float pitch) implements Effect {}
    record WorldChange(String change) implements Effect {}
    record Message(String text) implements Effect {}
    record SceneCompleted(String sceneId, String nextScene) implements Effect {}
    record Checkpoint(String location) implements Effect {}
}
