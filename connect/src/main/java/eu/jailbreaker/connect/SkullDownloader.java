package eu.jailbreaker.connect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SkullDownloader {
    private static final String HEAD_URL = "https://minecraft-heads.com/scripts/api.php?cat=%s&tags=true";

    public static Set<SkullEntry> download(String category) throws IOException {
        final String link = String.format(HEAD_URL, category.toLowerCase());
        final URL url = new URL(link);
        final HttpURLConnection connection = ((HttpURLConnection) url.openConnection());
        final InputStream in = connection.getInputStream();
        final InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        final Gson gson = new GsonBuilder().serializeNulls().create();
        final JsonArray rawElements = gson.fromJson(reader, JsonArray.class);
        final Spliterator<JsonElement> spliterator = rawElements.spliterator();
        final Set<SkullEntry> entries = StreamSupport.stream(spliterator, false)
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .map(SkullEntry::new)
                .collect(Collectors.toSet());
        Bukkit.getLogger().info("Downloaded " + entries.size() + " skull entries");
        return entries;
    }
}
