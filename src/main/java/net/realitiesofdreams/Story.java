package net.realitiesofdreams;

import net.realitiesofdreams.story.StoryContent;
import net.realitiesofdreams.story.engine.SceneEngine;

/** Shared access to the story content shipped in the mod jar (same on client and server). */
public final class Story {
    private static StoryContent content;
    private static SceneEngine engine;

    private Story() {}

    public static synchronized StoryContent content() {
        if (content == null) content = StoryContent.loadFromClasspath(Story.class);
        return content;
    }

    public static synchronized SceneEngine engine() {
        if (engine == null) engine = new SceneEngine(content());
        return engine;
    }
}
