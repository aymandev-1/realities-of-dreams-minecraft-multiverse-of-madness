package net.realitiesofdreams.story.engine;

import net.realitiesofdreams.story.scene.SceneDef;

import java.util.List;

/** Something the game must do in the world or on the player's screen. */
public sealed interface Effect {
    record ShowObjective(String sceneLabel, String sceneTitle, String text, SceneDef.Location target, String progress) implements Effect {}
    record ClearObjective() implements Effect {}
    record StartDialogue(String dialogueId) implements Effect {}
    record StartCinematic(String cinematicId) implements Effect {}
    /** kind: follow, stay, spawn, despawn, move, sleep, wake, approach, look */
    record NpcCommand(String kind, String character, String location) implements Effect {}
    /** A short on-screen tip; {forward}, {jump}, {inventory}... become the player's own key names. */
    record Hint(String text, int ticks) implements Effect {}
    /** The ground trembles under the player (camera shake outside cinematics). */
    record Shake(float intensity, int ticks) implements Effect {}
    /** A character says something in passing, without stopping anything. */
    record Say(String character, String text) implements Effect {}
    record TeleportPlayer(String location, float yaw) implements Effect {}
    /** A new page was added to Steve's journal. */
    record Journal(String entryId) implements Effect {}
    record Particles(String particle, String location, int count, double spread) implements Effect {}
    record ShowTitle(String title, String subtitle) implements Effect {}
    record GiveItem(String item, int count, String name, List<String> lore) implements Effect {}

    /** Puts an item the player carries into their hand, e.g. raising the compass for a cinematic. */
    record HoldItem(String item) implements Effect {}
    record PlaySound(String sound, float volume, float pitch) implements Effect {}
    record WorldChange(String change) implements Effect {}
    record Message(String text) implements Effect {}
    record SceneCompleted(String sceneId, String nextScene) implements Effect {}
    record Checkpoint(String location) implements Effect {}
}
