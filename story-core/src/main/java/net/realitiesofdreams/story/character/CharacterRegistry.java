package net.realitiesofdreams.story.character;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.realitiesofdreams.story.json.Json;
import net.realitiesofdreams.story.json.StoryFormatException;

import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CharacterRegistry {
    private final Map<String, CharacterDef> byId;

    private CharacterRegistry(Map<String, CharacterDef> byId) {
        this.byId = Collections.unmodifiableMap(byId);
    }

    public static CharacterRegistry load(Reader reader) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        Map<String, CharacterDef> map = new LinkedHashMap<>();
        for (CharacterDef c : Json.list(root, "characters", CharacterDef::fromJson)) {
            if (map.put(c.id(), c) != null) throw new StoryFormatException("duplicate character id " + c.id());
        }
        return new CharacterRegistry(map);
    }

    public Optional<CharacterDef> get(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public CharacterDef require(String id) {
        CharacterDef c = byId.get(id);
        if (c == null) throw new StoryFormatException("unknown character " + id);
        return c;
    }

    /** The name shown on subtitles: "Steve" for Primordial Steve, "Farmer" for a farmer. */
    public String displayName(String id) {
        CharacterDef c = byId.get(id);
        return c == null ? id : c.speakerName();
    }

    public Collection<CharacterDef> all() {
        return byId.values();
    }

    public List<CharacterDef> inUniverse(String universe) {
        return byId.values().stream().filter(c -> universe.equals(c.universe())).toList();
    }

    public int size() {
        return byId.size();
    }
}
