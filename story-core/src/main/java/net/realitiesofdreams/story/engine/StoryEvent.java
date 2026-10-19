package net.realitiesofdreams.story.engine;

/** Things that happen to a player that can move the story forward. */
public sealed interface StoryEvent {
    record DialogueFinished(String dialogueId) implements StoryEvent {}
    record CinematicFinished(String cinematicId) implements StoryEvent {}
    record Reached(String location) implements StoryEvent {}
    record ClueExamined(String clueId) implements StoryEvent {}
    record FlagSet(String flag) implements StoryEvent {}
    /** The player now carries everything the current ITEMS stage asks for. */
    record ItemsCollected(String stage) implements StoryEvent {}
    /** Every spot of the current BLOCKS stage now matches (built, lit, cleared...). */
    record BlocksDone(String stage) implements StoryEvent {}
}
