package net.realitiesofdreams.world.site;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.realitiesofdreams.story.json.StoryFormatException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * An authored site's placement registry (rod-story/sites/&lt;site&gt;.json): which pieces stand where,
 * in build order, and which few blocks the story needs protected.
 */
public record SiteLayout(String site, String universe, int[] clear, List<Piece> pieces, List<Region> protectedRegions) {
    /**
     * One building or feature. {@code box} is its footprint (x0, z0, x1, z1) relative to the anchor;
     * {@code options} carries the piece type's own settings (door side, palette, interior...).
     */
    public record Piece(String id, String type, String name, @Nullable int[] box, JsonObject options) {
        public String option(String key, String fallback) {
            return options.has(key) ? options.get(key).getAsString() : fallback;
        }

        public int option(String key, int fallback) {
            return options.has(key) ? options.get(key).getAsInt() : fallback;
        }

        public boolean flag(String key) {
            return options.has(key) && options.get(key).getAsBoolean();
        }
    }

    /** A box (x0, y0, z0, x1, y1, z1) relative to the anchor. */
    public record Region(String id, int[] box) {
        public boolean contains(int x, int y, int z) {
            return x >= Math.min(box[0], box[3]) && x <= Math.max(box[0], box[3]) && y >= Math.min(box[1], box[4]) && y <= Math.max(box[1], box[4])
                    && z >= Math.min(box[2], box[5]) && z <= Math.max(box[2], box[5]);
        }
    }

    @Nullable
    public Piece piece(String id) {
        for (Piece p : pieces) if (p.id().equals(id)) return p;
        return null;
    }

    public static SiteLayout load(String site) {
        String path = "/rod-story/sites/" + site + ".json";
        try (InputStream in = SiteLayout.class.getResourceAsStream(path)) {
            if (in == null) throw new StoryFormatException(path + " missing");
            return parse(JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject());
        } catch (IOException e) {
            throw new StoryFormatException(path + " unreadable: " + e.getMessage());
        }
    }

    public static SiteLayout parse(JsonObject o) {
        List<Piece> pieces = new ArrayList<>();
        for (JsonElement e : o.getAsJsonArray("pieces")) {
            JsonObject p = e.getAsJsonObject();
            pieces.add(new Piece(p.get("id").getAsString(), p.get("type").getAsString(), p.has("name") ? p.get("name").getAsString() : p.get("id").getAsString(),
                    p.has("box") ? ints(p.getAsJsonArray("box")) : null, p));
        }
        List<Region> regions = new ArrayList<>();
        if (o.has("protected")) {
            for (JsonElement e : o.getAsJsonArray("protected")) {
                JsonObject r = e.getAsJsonObject();
                regions.add(new Region(r.get("id").getAsString(), ints(r.getAsJsonArray("box"))));
            }
        }
        return new SiteLayout(o.get("site").getAsString(), o.get("universe").getAsString(), ints(o.getAsJsonArray("clear")),
                List.copyOf(pieces), List.copyOf(regions));
    }

    private static int[] ints(com.google.gson.JsonArray a) {
        int[] out = new int[a.size()];
        for (int i = 0; i < out.length; i++) out[i] = a.get(i).getAsInt();
        return out;
    }
}
