package net.realitiesofdreams.story.engine;

/** Things that happen to a player that can move the story forward. */
public sealed interface StoryEvent {
    record DialogueFinished(String dialogueId) implements StoryEvent {}
    record CinematicFinished(String cinematicId) implements StoryEvent {}
    record Reached(String location) implements StoryEvent {}
    record ClueExamined(String clueId) implements StoryEvent {}
    record FlagSet(String flag) implements StoryEvent {}
}
