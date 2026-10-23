package net.realitiesofdreams.story.json;

/** Thrown when a story content file is malformed. */
public class StoryFormatException extends RuntimeException {
    public StoryFormatException(String message) {
        super(message);
    }
}
