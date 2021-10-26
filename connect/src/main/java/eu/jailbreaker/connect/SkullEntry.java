package eu.jailbreaker.connect;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public final class SkullEntry {
    private final String name;
    private final String uuid;
    private final String value;
    private final Set<String> tags;

    public SkullEntry(JsonObject object) {
        this.name = object.get("name").getAsString();
        this.uuid = object.get("uuid").getAsString();
        this.value = object.get("value").getAsString();
        if (object.has("tags")) {
            final JsonElement tags = object.get("tags");
            if (tags.isJsonObject()) {
                this.tags = Arrays.stream(tags.getAsString().split(",")).collect(Collectors.toSet());
            } else {
                this.tags = Sets.newHashSet();
            }
        } else {
            this.tags = Sets.newHashSet();
        }
    }
}